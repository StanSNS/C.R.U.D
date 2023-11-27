package backend.controller;

import backend.dto.AuthResponseDTO;
import backend.dto.LoginDTO;
import backend.dto.RegisterDTO;
import backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;

import static backend.constants.ResponseConst.USER_EMAIL_EXIST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthControllerTests {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        RegisterDTO registerDTO = new RegisterDTO();
        String expectedResponse = "User registered successfully!";

        when(authService.register(registerDTO)).thenReturn(expectedResponse);

        ResponseEntity<String> responseEntity = authController.register(registerDTO);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());

        verify(authService, times(1)).register(registerDTO);
    }

    @Test
    void testLogin() {
        LoginDTO loginDTO = new LoginDTO();
        AuthResponseDTO expectedResponse = new AuthResponseDTO();
        expectedResponse.setPassword("password");
        expectedResponse.setEmail("email");
        expectedResponse.setRoles(new HashSet<>());

        when(authService.login(loginDTO)).thenReturn(expectedResponse);

        ResponseEntity<AuthResponseDTO> responseEntity = authController.register(loginDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());

        verify(authService, times(1)).login(loginDTO);
    }

    @Test
    void testRegisterUserEmailExist() {
        RegisterDTO registerDTO = new RegisterDTO();

        when(authService.register(registerDTO)).thenReturn(USER_EMAIL_EXIST);

        ResponseEntity<String> responseEntity = authController.register(registerDTO);

        assertEquals(HttpStatus.IM_USED, responseEntity.getStatusCode());
        assertEquals(USER_EMAIL_EXIST, responseEntity.getBody());

        verify(authService, times(1)).register(registerDTO);
    }
}
