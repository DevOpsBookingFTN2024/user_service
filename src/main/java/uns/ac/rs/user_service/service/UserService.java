package uns.ac.rs.user_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uns.ac.rs.user_service.dto.UserDTO;
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
}
