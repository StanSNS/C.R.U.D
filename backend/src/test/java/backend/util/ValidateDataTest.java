package backend.util;

import backend.entity.UserEntity;
import backend.exception.AccessDeniedException;
import backend.repository.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
}
