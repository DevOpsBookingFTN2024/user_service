package uns.ac.rs.user_service.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uns.ac.rs.user_service.dto.request.LoginRequest;
import uns.ac.rs.user_service.dto.request.RegistrationRequest;
import uns.ac.rs.user_service.dto.response.JwtResponse;
import uns.ac.rs.user_service.dto.response.MessageResponse;
import uns.ac.rs.user_service.model.ERole;
import uns.ac.rs.user_service.model.Role;
import uns.ac.rs.user_service.model.User;
import uns.ac.rs.user_service.repository.RoleRepository;
import uns.ac.rs.user_service.repository.UserRepository;
import uns.ac.rs.user_service.security.jwt.JwtUtils;
import uns.ac.rs.user_service.security.services.UserDetailsImpl;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    public MessageResponse registerUser(RegistrationRequest registrationRequest) {
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            return new MessageResponse("Username is already taken.");
        } else if (userRepository.existsByEmailAddress(registrationRequest.getEmailAddress())) {
            return new MessageResponse("Email address is already in use.");
        } else {
            User newUser = new User(
                    registrationRequest.getUsername(),
                    encoder.encode(registrationRequest.getPassword()),
                    registrationRequest.getEmailAddress(),
                    registrationRequest.getFirstName(),
                    registrationRequest.getLastName(),
                    registrationRequest.getResidence()
            );

            Set<String> strRoles = registrationRequest.getRole();
            Set<Role> roles = new HashSet<>();

            if (strRoles == null) {
                Role guestRole = roleRepository.findByName(ERole.ROLE_GUEST)
                        .orElseThrow(() -> new RuntimeException("Role is not found with name: "
                                + ERole.ROLE_GUEST));
                roles.add(guestRole);
            } else {
                strRoles.forEach(role -> {
                    switch (role) {
                        case "admin":
                            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Role is not found with name: "
                                            + ERole.ROLE_ADMIN));
                            roles.add(adminRole);
                            break;
                        case "host":
                            Role hostRole = roleRepository.findByName(ERole.ROLE_HOST)
                                    .orElseThrow(() -> new RuntimeException("Role is not found with name: "
                                            + ERole.ROLE_HOST));
                            roles.add(hostRole);
                            break;
                        default:
                            Role guestRole = roleRepository.findByName(ERole.ROLE_GUEST)
                                    .orElseThrow(() -> new RuntimeException("Role is not found with name: "
                                            + ERole.ROLE_GUEST));
                            roles.add(guestRole);
                    }
                });
            }

            newUser.setRoles(roles);
            userRepository.save(newUser);
            return new MessageResponse("User registered successfully.");
        }
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmailAddress(), roles);
    }
}

