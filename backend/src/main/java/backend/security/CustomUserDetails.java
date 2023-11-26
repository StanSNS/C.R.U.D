package backend.security;

import backend.entity.RoleEntity;
import backend.entity.UserEntity;
import backend.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static backend.constants.OtherConst.DEFAULT_USER_PASSWORD;
import static backend.constants.OtherConst.DEFAULT_USER_USERNAME;
import static backend.constants.RoleConst.ROLE_PREFIX_CONSTANT;
import static backend.constants.RoleConst.USER_CONSTANT;

@Service
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetailsService {

    /**
     * initializing dependencies with lombok @RequiredArgsConstructor
     */
    private final UserEntityRepository userEntityRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Load user details by email from the repository
        UserEntity user = userEntityRepository.findByEmail(email);

        if (user == null) {
            // If the user is not found, create a default user with minimal authorities
            return createDefaultUser();
        }

        // Extract and build user authorities from roles
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (user.getRoles() != null) {
            for (RoleEntity role : user.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX_CONSTANT + role.getName()));
            }
        }

        // Create and return a UserDetails object
        return new User(email, user.getPassword(), authorities);
    }

    /**
     * Creates a default UserDetails object with minimal authorities.
     *
     * @return Default UserDetails for unidentified users.
     */
    public UserDetails createDefaultUser() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX_CONSTANT + USER_CONSTANT));
        return new User(DEFAULT_USER_USERNAME, DEFAULT_USER_PASSWORD, authorities);
    }
}
