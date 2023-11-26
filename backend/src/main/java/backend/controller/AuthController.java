package backend.controller;

import backend.dto.AuthResponseDTO;
import backend.dto.LoginDTO;
import backend.dto.RegisterDTO;
import backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static backend.constants.URLAccessConst.FRONTEND_BASE_URL;

@CrossOrigin(FRONTEND_BASE_URL)
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {


    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDto) {
        return new ResponseEntity<>(authService.register(registerDto), HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody LoginDTO loginDTO) {
        return new ResponseEntity<>(authService.login(loginDTO), HttpStatus.OK);

    }

}
