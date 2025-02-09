package uns.ac.rs.user_service.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uns.ac.rs.user_service.dto.UserDTO;
import uns.ac.rs.user_service.dto.request.PasswordChangeRequest;
import uns.ac.rs.user_service.dto.request.UserUpdateRequest;
import uns.ac.rs.user_service.dto.response.MessageResponse;
import uns.ac.rs.user_service.service.UserService;

@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        log.info("Fetching current user details.");
        UserDTO currentUser = userService.getCurrentUser();
        log.info("Current user details fetched successfully: {}", currentUser.getUsername());
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        log.info("Fetching user by username: {}", username);
        UserDTO user = userService.getUserByUsername(username);
        log.info("User details retrieved successfully for username: {}", username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCurrentUser(@Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        log.info("Updating user: {}", userUpdateRequest.getUsername());
        MessageResponse messageResponse = userService.updateCurrentUser(userUpdateRequest);
        log.info("User {} updated successfully.", userUpdateRequest.getUsername());
        return ResponseEntity.ok(messageResponse);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        log.info("Changing password current user.");
        MessageResponse messageResponse = userService.changePassword(passwordChangeRequest);
        log.info("Password changed successfully for current user.");
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCurrentUser(@RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        log.info("Deleting current user associated with token.");
        MessageResponse messageResponse = userService.deleteCurrentUser(jwtToken);
        log.info("User successfully deleted.");
        return ResponseEntity.ok(messageResponse);
    }
}
