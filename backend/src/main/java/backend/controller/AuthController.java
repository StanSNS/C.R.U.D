package backend.controller;

import backend.dto.AuthResponseDTO;
import backend.dto.LoginDTO;
import backend.dto.RegisterDTO;
import backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static backend.constants.ResponseConst.USER_EMAIL_EXIST;
import static backend.constants.ResponseConst.USER_REGISTER_SUCCESSFULLY;
import static backend.constants.URLAccessConst.FRONTEND_BASE_URL;

@CrossOrigin(FRONTEND_BASE_URL)
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * initializing dependencies with lombok @RequiredArgsConstructor
     */
    private final AuthService authService;


    /**
     * Endpoint for user registration.
     *
     * @param registerDto The data transfer object containing registration information.
     * @return ResponseEntity with a string response and appropriate HTTP status.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDto) {
        String registerResponse = authService.register(registerDto);

        if (registerResponse.equals(USER_EMAIL_EXIST)) {
            return new ResponseEntity<>(USER_EMAIL_EXIST, HttpStatus.IM_USED);
        }
        return new ResponseEntity<>(USER_REGISTER_SUCCESSFULLY, HttpStatus.CREATED);
    }

    /**
     * Endpoint for user login.
     *
     * @param loginDTO The data transfer object containing login information.
     * @return ResponseEntity with an authentication response DTO and HTTP status OK.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody LoginDTO loginDTO) {
        return new ResponseEntity<>(authService.login(loginDTO), HttpStatus.OK);
    }

}
