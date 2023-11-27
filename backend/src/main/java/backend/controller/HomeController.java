package backend.controller;


import backend.dto.UserDetailsDTO;
import backend.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static backend.constants.URLAccessConst.FRONTEND_BASE_URL;

@CrossOrigin(FRONTEND_BASE_URL)
@RequiredArgsConstructor
@RestController
@RequestMapping
public class HomeController {


    /**
     * initializing dependencies with lombok @RequiredArgsConstructor
     */
    private final HomeService homeService;


    /**
     * Retrieves a list of user details based on the provided email and password.
     * <p>
     * This endpoint is mapped to "/home" using the HTTP GET method. It requires two
     * request parameters, "email" and "password", to authenticate and fetch user details.
     *
     * @param email    The email of the user for authentication.
     * @param password The password of the user for authentication.
     * @return ResponseEntity<List < UserDetailsDTO>> A response entity containing a list of UserDetailsDTO objects representing user details.
     * @apiNote This endpoint is designed to be used for fetching user details by providing valid email and password credentials.
     */
    @GetMapping("/home")
    public ResponseEntity<List<UserDetailsDTO>> getAllUsers(@RequestParam String email, @RequestParam String password) {
        return new ResponseEntity<>(homeService.getAllUsers(email, password), HttpStatus.OK);

    }
}
