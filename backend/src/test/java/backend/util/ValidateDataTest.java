package backend.util;

import backend.entity.RoleEntity;
import backend.entity.UserEntity;
import backend.exception.AccessDeniedException;
import backend.repository.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

import static backend.constants.RoleConst.ADMIN_CONSTANT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidateDataTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ValidateData validateData;

    @Test
    void testValidateUserWithValidCredentials() {
        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setEmail("test@email.com");
        mockUserEntity.setPassword("encodedPassword"); // Replace with an actual encoded password

        when(userEntityRepository.findByEmail(anyString())).thenReturn(mockUserEntity);

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        UserEntity result = validateData.validateUserWithPassword("test@email.com", "rawPassword");

        assertEquals("test@email.com", result.getEmail());
    }

    @Test
    void testValidateUserWithInvalidCredentials() {
        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setEmail("test@email.com");
        mockUserEntity.setPassword("encodedPassword"); // Replace with an actual encoded password

        when(userEntityRepository.findByEmail(anyString())).thenReturn(mockUserEntity);

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> validateData.validateUserWithPassword("test@email.com", "invalidPassword"));
    }

    @Test
    void testValidateUserWithNonexistentUser() {
        when(userEntityRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(AccessDeniedException.class, () -> validateData
                .validateUserWithPassword("nonexistent@email.com", "anyPassword"));
    }

    @Test
    void testIsUserAdminWithAdminRole() {
        Set<RoleEntity> roles = new HashSet<>();
        RoleEntity adminRole = new RoleEntity();
        adminRole.setName(ADMIN_CONSTANT);
        roles.add(adminRole);

        assertTrue(validateData.isUserAdmin(roles));
    }

    @Test
    void testIsUserAdminWithoutAdminRole() {
        Set<RoleEntity> roles = new HashSet<>();
        RoleEntity userRole = new RoleEntity();
        userRole.setName("USER_ROLE");
        roles.add(userRole);

        assertFalse(validateData.isUserAdmin(roles));
    }

    @Test
    void testIsUserAdminWithEmptyRoles() {
        Set<RoleEntity> roles = new HashSet<>();

        assertFalse(validateData.isUserAdmin(roles));
    }
}
