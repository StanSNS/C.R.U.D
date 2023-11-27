package backend.init;

import backend.entity.RoleEntity;
import backend.repository.RoleEntityRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static backend.constants.RoleConst.ADMIN_CONSTANT;
import static backend.constants.RoleConst.USER_CONSTANT;

@Component
@RequiredArgsConstructor
public class RoleEntityInit {

    /**
     * initializing dependencies with lombok @RequiredArgsConstructor
     */
    private final RoleEntityRepository roleRepository;

    /**
     * Initializes user roles if no records exist in the RoleRepository.
     * Creates and saves default RoleEntity records for user roles.
     */
    @PostConstruct
    public void rolesInit() {
        // Check if there are no existing RoleEntity records
        if (roleRepository.count() == 0) {
            // Create and save default RoleEntity records for user roles
            roleRepository.save(new RoleEntity(USER_CONSTANT));
            roleRepository.save(new RoleEntity(ADMIN_CONSTANT));
        }
    }

}
