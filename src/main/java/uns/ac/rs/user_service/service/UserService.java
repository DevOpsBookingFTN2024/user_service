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
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class UserService {

}
