package uns.ac.rs.user_service.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uns.ac.rs.user_service.dto.UserDTO;
import uns.ac.rs.user_service.dto.request.PasswordChangeRequest;
import uns.ac.rs.user_service.dto.request.UserUpdateRequest;
import uns.ac.rs.user_service.dto.response.MessageResponse;
import uns.ac.rs.user_service.service.UserService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        UserDTO currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCurrentUser(@Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        MessageResponse messageResponse = userService.updateCurrentUser(userUpdateRequest);
        return ResponseEntity.ok(messageResponse);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        MessageResponse messageResponse = userService.changePassword(passwordChangeRequest);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCurrentUser(@RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        MessageResponse messageResponse = userService.deleteCurrentUser(jwtToken);
        return ResponseEntity.ok(messageResponse);
    }
}
