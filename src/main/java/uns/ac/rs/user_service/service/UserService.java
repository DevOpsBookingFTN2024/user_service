package uns.ac.rs.user_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uns.ac.rs.user_service.dto.UserDTO;
import uns.ac.rs.user_service.dto.request.PasswordChangeRequest;
import uns.ac.rs.user_service.dto.request.UserUpdateRequest;
import uns.ac.rs.user_service.dto.response.MessageResponse;
import uns.ac.rs.user_service.mapper.UserMapper;
import uns.ac.rs.user_service.model.User;
import uns.ac.rs.user_service.repository.UserRepository;
import uns.ac.rs.user_service.security.services.UserDetailsImpl;
import uns.ac.rs.user_service.service.client.AccommodationServiceClient;
import uns.ac.rs.user_service.service.client.ReservationServiceClient;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final ReservationServiceClient reservationServiceClient;

    private final AccommodationServiceClient accommodationServiceClient;

    public UserService(PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       ReservationServiceClient reservationServiceClient,
                       AccommodationServiceClient accommodationServiceClient) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.reservationServiceClient = reservationServiceClient;
        this.accommodationServiceClient = accommodationServiceClient;
    }

    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userDetails.getId()));

        return UserMapper.toUserDTO(user);
    }

    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found with username: " + username));

        return UserMapper.toUserDTO(user);
    }

    public MessageResponse updateCurrentUser(UserUpdateRequest userUpdateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userDetails.getId()));

        if (!Objects.equals(userUpdateRequest.getUsername(), userDetails.getUsername())
                && userRepository.existsByUsername(userUpdateRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        if (!Objects.equals(userUpdateRequest.getEmailAddress(), userDetails.getEmailAddress())
                && userRepository.existsByEmailAddress(userUpdateRequest.getEmailAddress())) {
            throw new IllegalArgumentException("Email address is already in use.");
        }

        user.setUsername(userUpdateRequest.getUsername());
        user.setEmailAddress(userUpdateRequest.getEmailAddress());
        user.setFirstName(userUpdateRequest.getFirstName());
        user.setLastName(userUpdateRequest.getLastName());
        user.setResidence(userUpdateRequest.getResidence());

        userRepository.save(user);

        return new MessageResponse("User updated successfully.");
    }

    public MessageResponse changePassword(PasswordChangeRequest passwordChangeRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userDetails.getId()));

        if (!passwordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect.");
        }

        if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getRepeatNewPassword())) {
            throw new IllegalArgumentException("New passwords do not match.");
        }

        user.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));

        userRepository.save(user);

        return new MessageResponse("Password changed successfully.");
    }

    public MessageResponse deleteCurrentUser(String jwtToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userDetails.getId()));

        Set<String> roles = user
                .getRoles()
                .stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        if (roles.contains("ROLE_GUEST")) {
            if(!reservationServiceClient.isGuestHasAcceptedReservation(user.getUsername())) {
                reservationServiceClient.cancelMyPendingReservationsGuest(jwtToken);

                userRepository.delete(user);

                return new MessageResponse("User deleted successfully.");
            } else {
                throw new SecurityException("You cannot delete your account.");
            }
        } else if (roles.contains("ROLE_HOST")) {
            if(!reservationServiceClient.isHostHasAcceptedReservation(user.getUsername())) {
                reservationServiceClient.declineMyPendingReservationsHost(jwtToken);

                accommodationServiceClient.deleteAllAccommodationsByHost(jwtToken);

                userRepository.delete(user);

                return new MessageResponse("User deleted successfully.");
            } else {
                throw new SecurityException("You cannot delete your account.");
            }
        } else {
            throw new SecurityException("You cannot delete your account.");
        }
    }
}
