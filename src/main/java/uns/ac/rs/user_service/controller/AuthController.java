package uns.ac.rs.user_service.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uns.ac.rs.user_service.dto.request.LoginRequest;
import uns.ac.rs.user_service.dto.request.RegistrationRequest;
import uns.ac.rs.user_service.dto.response.JwtResponse;
import uns.ac.rs.user_service.dto.response.MessageResponse;
import uns.ac.rs.user_service.service.AuthService;

@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        log.info("Received registration request for username: {}", registrationRequest.getUsername());
        MessageResponse messageResponse = authService.registerUser(registrationRequest);
        log.info("User {} registered successfully.", registrationRequest.getUsername());
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Authentication attempt for username: {}", loginRequest.getUsername());
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        log.info("User {} authenticated successfully.", loginRequest.getUsername());
        return ResponseEntity.ok(jwtResponse);
    }
}
