package backend.service;

import backend.dto.AuthResponseDTO;
import backend.dto.LoginDTO;
import backend.dto.RegisterDTO;
import backend.dto.RoleDTO;
import backend.entity.RoleEntity;
import backend.entity.UserEntity;
import backend.exception.DataValidationException;
import backend.exception.ResourceNotFoundException;
import backend.repository.RoleEntityRepository;
import backend.repository.UserEntityRepository;
import backend.util.CustomDateFormatter;
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

import static backend.constants.ResponseConst.USER_EMAIL_EXIST;
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
    private CustomDateFormatter customDateFormatter;

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

        String result = authService.register(registerDto);

        assertEquals(USER_REGISTER_SUCCESSFULLY, result);

        verify(validationUtil, times(1)).isValid(any());
        verify(userRepository, times(1)).existsByEmail(any());
        verify(modelMapper, times(1)).map(any(), eq(UserEntity.class));
        verify(passwordEncoder, times(1)).encode(any());
        verify(roleRepository, times(2)).findByName(any());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testRegister_InvalidRegistration_ThrowsDataValidationException() {
        RegisterDTO registerDto = new RegisterDTO();
        registerDto.setEmail("invalid-email");
        registerDto.setPassword("password");

        when(validationUtil.isValid(any())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> authService.register(registerDto));

        verify(validationUtil, times(1)).isValid(any());
        verify(userRepository, never()).existsByEmail(any());
        verify(modelMapper, never()).map(any(), eq(UserEntity.class));
        verify(passwordEncoder, never()).encode(any());
        verify(roleRepository, never()).findByName(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegister_UserAlreadyExists_ThrowsResourceAlreadyExistsException() {
        RegisterDTO registerDto = new RegisterDTO();
        registerDto.setEmail("existing@example.com");
        registerDto.setPassword("password");

        when(validationUtil.isValid(any())).thenReturn(true);
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertEquals(USER_EMAIL_EXIST, authService.register(registerDto));

        verify(validationUtil, times(1)).isValid(any());
        verify(userRepository, times(1)).existsByEmail(any());
        verify(modelMapper, never()).map(any(), eq(UserEntity.class));
        verify(passwordEncoder, never()).encode(any());
        verify(roleRepository, never()).findByName(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterDataValidationException() {
        RegisterDTO registerDTO = new RegisterDTO();

        when(validationUtil.isValid(registerDTO)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> authService.register(registerDTO));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterResourceAlreadyExistsException() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("existing@example.com");

        when(validationUtil.isValid(registerDTO)).thenReturn(true);
        when(userRepository.existsByEmail(registerDTO.getEmail())).thenReturn(true);

        assertEquals(USER_EMAIL_EXIST, authService.register(registerDTO));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testLogin_ValidCredentials_ReturnsAuthResponseDTO() {
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

        AuthResponseDTO result = authService.login(loginDto);

        assertEquals(loginDto.getEmail(), result.getEmail());
        assertEquals("password", result.getPassword());

        verify(validationUtil, times(2)).isValid(any());
        verify(userRepository, times(1)).findByEmail(any());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void testLoginDataValidationException() {
        LoginDTO loginDTO = new LoginDTO();

        when(validationUtil.isValid(loginDTO)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> authService.login(loginDTO));
    }

    @Test
    void testLoginResourceNotFoundException() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("nonexistent@example.com");

        when(validationUtil.isValid(loginDTO)).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> authService.login(loginDTO));
    }

}
