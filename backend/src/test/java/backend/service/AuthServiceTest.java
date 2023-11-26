package backend.service;

import backend.dto.AuthResponseDTO;
import backend.dto.LoginDTO;
import backend.dto.RegisterDTO;
import backend.dto.RoleDTO;
import backend.entity.RoleEntity;
import backend.entity.UserEntity;
import backend.exception.DataValidationException;
import backend.exception.ResourceAlreadyExistsException;
import backend.exception.ResourceNotFoundException;
import backend.repository.RoleEntityRepository;
import backend.repository.UserEntityRepository;
import backend.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

import static backend.constants.ResponseConst.USER_REGISTER_SUCCESSFULLY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserEntityRepository userRepository;

    @Mock
    private RoleEntityRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ValidationUtil validationUtil;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_ValidRegistration_ReturnsSuccessMessage() {
        // Arrange
        RegisterDTO registerDto = new RegisterDTO();
        registerDto.setEmail("test@example.com");
        registerDto.setPassword("password");
        UserEntity userEntity = new UserEntity();
        RoleEntity userRole = new RoleEntity();
        userRole.setName("ROLE_USER");

        when(validationUtil.isValid(any())).thenReturn(true);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(modelMapper.map(any(), eq(UserEntity.class))).thenReturn(userEntity);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(roleRepository.findByName(any())).thenReturn(userRole);
        when(userRepository.count()).thenReturn(0L);

        // Act
        String result = authService.register(registerDto);

        // Assert
        assertEquals(USER_REGISTER_SUCCESSFULLY, result);

        // Verify
        verify(validationUtil, times(1)).isValid(any());
        verify(userRepository, times(1)).existsByEmail(any());
        verify(modelMapper, times(1)).map(any(), eq(UserEntity.class));
        verify(passwordEncoder, times(1)).encode(any());
        verify(roleRepository, times(2)).findByName(any());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testRegister_InvalidRegistration_ThrowsDataValidationException() {
        // Arrange
        RegisterDTO registerDto = new RegisterDTO();
        registerDto.setEmail("invalid-email");
        registerDto.setPassword("password");

        when(validationUtil.isValid(any())).thenReturn(false);

        // Act & Assert
        assertThrows(DataValidationException.class, () -> authService.register(registerDto));

        // Verify
        verify(validationUtil, times(1)).isValid(any());
        verify(userRepository, never()).existsByEmail(any());
        verify(modelMapper, never()).map(any(), eq(UserEntity.class));
        verify(passwordEncoder, never()).encode(any());
        verify(roleRepository, never()).findByName(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegister_UserAlreadyExists_ThrowsResourceAlreadyExistsException() {
        // Arrange
        RegisterDTO registerDto = new RegisterDTO();
        registerDto.setEmail("existing@example.com");
        registerDto.setPassword("password");

        when(validationUtil.isValid(any())).thenReturn(true);
        when(userRepository.existsByEmail(any())).thenReturn(true);

        // Act & Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> authService.register(registerDto));

        // Verify
        verify(validationUtil, times(1)).isValid(any());
        verify(userRepository, times(1)).existsByEmail(any());
        verify(modelMapper, never()).map(any(), eq(UserEntity.class));
        verify(passwordEncoder, never()).encode(any());
        verify(roleRepository, never()).findByName(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterDataValidationException() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();

        when(validationUtil.isValid(registerDTO)).thenReturn(false);

        // Act & Assert
        assertThrows(DataValidationException.class, () -> authService.register(registerDTO));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterResourceAlreadyExistsException() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("existing@example.com");

        when(validationUtil.isValid(registerDTO)).thenReturn(true);
        when(userRepository.existsByEmail(registerDTO.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> authService.register(registerDTO));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testLogin_ValidCredentials_ReturnsAuthResponseDTO() {
        // Arrange
        LoginDTO loginDto = new LoginDTO("test@example.com", "password");
        UserEntity userEntity = new UserEntity();
        userEntity.setRoles(new HashSet<>());
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("ROLE_USER");

        when(validationUtil.isValid(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(userEntity);
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(modelMapper.map(any(), eq(RoleDTO.class))).thenReturn(new RoleDTO());
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        // Act
        AuthResponseDTO result = authService.login(loginDto);

        // Assert
        assertEquals(loginDto.getEmail(), result.getEmail());
        assertEquals("5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8", result.getPassword());

        // Verify
        verify(validationUtil, times(2)).isValid(any());
        verify(userRepository, times(1)).findByEmail(any());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void testLoginDataValidationException() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();

        when(validationUtil.isValid(loginDTO)).thenReturn(false);

        // Act & Assert
        assertThrows(DataValidationException.class, () -> authService.login(loginDTO));
    }

    @Test
    void testLoginResourceNotFoundException() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("nonexistent@example.com");

        when(validationUtil.isValid(loginDTO)).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> authService.login(loginDTO));
    }


}
