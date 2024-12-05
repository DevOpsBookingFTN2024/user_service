package uns.ac.rs.user_service.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uns.ac.rs.user_service.dto.UserDTO;
import uns.ac.rs.user_service.dto.request.CreateUserRequest;
import uns.ac.rs.user_service.dto.response.MessageResponse;
import uns.ac.rs.user_service.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        MessageResponse messageResponse = userService.createUser(createUserRequest);
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable UUID userId) {
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }
}
