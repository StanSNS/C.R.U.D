package backend.controller;


import backend.dto.UserDetailsDTO;
import backend.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static backend.constants.URLAccessConst.FRONTEND_BASE_URL;

@CrossOrigin(FRONTEND_BASE_URL)
@RequiredArgsConstructor
@RestController
@RequestMapping("/home")
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
    @GetMapping
    public ResponseEntity<List<UserDetailsDTO>> getAllUsers(@RequestParam String email, @RequestParam String password) {
        return new ResponseEntity<>(homeService.getAllUsers(email, password), HttpStatus.OK);
    }


    /**
     * Deletes a user based on the provided email.
     * <p>
     * This endpoint is mapped to "/home" using the HTTP PUT method. It requires three
     * request parameters, "email", "password", and "userToDeleteEmail", to authenticate and delete a user.
     *
     * @param email             The email of the user for authentication.
     * @param password          The password of the user for authentication.
     * @param userToDeleteEmail The email of the user to be deleted.
     * @return ResponseEntity<?> A response entity with no content and a status of HttpStatus.OK if the user deletion is successful.
     * @apiNote This endpoint is designed to be used for deleting a user by providing valid email and password credentials,
     * and the email of the user to be deleted.
     */
    @PutMapping
    public ResponseEntity<?> getAllUsers(@RequestParam String email, @RequestParam String password, @RequestParam String userToDeleteEmail) {
        homeService.deleteUser(email, password, userToDeleteEmail);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Logs the user out.
     *
     * @param email    Email of the user.
     * @param password Password for authentication.
     * @return A ResponseEntity with a status code 200 if the logout is successful, or an error response if the operation fails.
     */
    @PostMapping
    public ResponseEntity<?> logoutUser(@RequestParam String email, @RequestParam String password) {
        homeService.logoutUser(email,password);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
