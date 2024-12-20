package uns.ac.rs.user_service.dataloader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uns.ac.rs.user_service.model.ERole;
import uns.ac.rs.user_service.model.Role;
import uns.ac.rs.user_service.repository.RoleRepository;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!roleRepository.existsByName(ERole.ROLE_ADMIN)) {
            roleRepository.save(new Role(null, ERole.ROLE_ADMIN));
        }
        if (!roleRepository.existsByName(ERole.ROLE_HOST)) {
            roleRepository.save(new Role(null, ERole.ROLE_HOST));
        }
        if (!roleRepository.existsByName(ERole.ROLE_GUEST)) {
            roleRepository.save(new Role(null, ERole.ROLE_GUEST));
        }
    }
}
