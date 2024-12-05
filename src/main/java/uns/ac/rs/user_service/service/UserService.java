package uns.ac.rs.user_service.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uns.ac.rs.user_service.dto.UserDTO;
import uns.ac.rs.user_service.dto.request.CreateUserRequest;
import uns.ac.rs.user_service.dto.response.MessageResponse;
import uns.ac.rs.user_service.mapper.UserMapper;
import uns.ac.rs.user_service.model.User;
import uns.ac.rs.user_service.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public MessageResponse createUser(CreateUserRequest createUserRequest) {
        if (userRepository.existsByUsername(createUserRequest.getUsername())) {
            return new MessageResponse("Error: Username is already taken.");
        } else {
            User newUser = new User(createUserRequest.getUsername());

            userRepository.save(newUser);
            return new MessageResponse("User created successfully.");
        }
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return UserMapper.toUserDTO(user);
    }
}
