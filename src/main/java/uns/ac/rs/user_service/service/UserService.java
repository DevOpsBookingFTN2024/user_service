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
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder,
                       UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

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
        UUID userId = userDetails.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

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
        UUID userId = userDetails.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

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
}
