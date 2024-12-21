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
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;


    public UserService(PasswordEncoder passwordEncoder,
                       UserRepository userRepository
                     ) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;

    }

    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return UserMapper.toUserDTO(user);
    }

    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        return UserMapper.toUserDTO(user);
    }

    public MessageResponse updateCurrentUser(UserUpdateRequest userUpdateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (Objects.equals(userUpdateRequest.getUsername(), userDetails.getUsername())) {
            if (Objects.equals(userUpdateRequest.getEmailAddress(), userDetails.getEmailAddress())) {
                user.setFirstName(userUpdateRequest.getFirstName());
                user.setLastName(userUpdateRequest.getLastName());
                user.setResidence(userUpdateRequest.getResidence());

                userRepository.save(user);
                return new MessageResponse("User updated successfully.");
            } else if (userRepository.existsByEmailAddress(userUpdateRequest.getEmailAddress())) {
                return new MessageResponse("Email address is already in use.");
            } else {
                user.setEmailAddress(userUpdateRequest.getEmailAddress());
                user.setFirstName(userUpdateRequest.getFirstName());
                user.setLastName(userUpdateRequest.getLastName());
                user.setResidence(userUpdateRequest.getResidence());

                userRepository.save(user);
                return new MessageResponse("User updated successfully.");
            }
        } else if (userRepository.existsByUsername(userUpdateRequest.getUsername())) {
            return new MessageResponse("Username is already taken.");
        } else {
            user.setUsername(userUpdateRequest.getUsername());
            user.setEmailAddress(userUpdateRequest.getEmailAddress());
            user.setFirstName(userUpdateRequest.getFirstName());
            user.setLastName(userUpdateRequest.getLastName());
            user.setResidence(userUpdateRequest.getResidence());

            userRepository.save(user);
            return new MessageResponse("User updated successfully.");
        }
    }

    public MessageResponse changePassword(PasswordChangeRequest passwordChangeRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (!passwordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPassword())) {
            return new MessageResponse("Old password is incorrect.");
        }

        if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getRepeatNewPassword())) {
            return new MessageResponse("New passwords do not match.");
        }

        user.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));

        userRepository.save(user);
        return new MessageResponse("Password changed successfully.");
    }
}
