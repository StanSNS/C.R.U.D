package backend.controller;


import backend.dto.AuthResponseDTO;
import backend.dto.EditDetailsDTO;
import backend.exception.MissingParameterException;
import backend.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import static backend.constants.ActionConst.*;
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
     * Retrieves users based on the specified action and parameters.
     *
     * @param action              The action to perform (e.g., "all_users_default", "all_users_sort_by_last_name_and_dob", etc.).
     * @param email               The email for user validation.
     * @param password            The password for user validation.
     * @param selectedUserEmail   The email of the selected user (required for the "get_selected_user" action).
     * @param searchTerm          The term to search for (required for the "all_users_found_by_parameter" action).
     * @param selectedSearchOption The option selected for searching (required for the "all_users_found_by_parameter" action).
     * @param currentPage         The current page number.
     * @param sizeOnPage          The number of items per page.
     * @return ResponseEntity<?> A response entity containing the result of the specified action.
     * @throws MissingParameterException If required parameters are missing.
     */
    @GetMapping
    public ResponseEntity<?> getUsers(@RequestParam String action,
                                      @RequestParam String email,
                                      @RequestParam String password,
                                      @RequestParam(required = false) String selectedUserEmail,
                                      @RequestParam(required = false) String searchTerm,
                                      @RequestParam(required = false) String selectedSearchOption,
                                      @RequestParam Integer currentPage,
                                      @RequestParam Integer sizeOnPage) {

        return switch (action) {
            case ALL_USERS_DEFAULT ->
                    new ResponseEntity<>(homeService.getAllUsersByDefault(email, password, currentPage, sizeOnPage), HttpStatus.OK);

            case ALL_USERS_SORT_BY_LAST_NAME_AND_DOB ->
                    new ResponseEntity<>(homeService.getAllUsersOrderedByLastNameAndDateOfBirth(email, password, currentPage, sizeOnPage), HttpStatus.OK);

            case ALL_USERS_FOUND_BY_PARAMETER -> new ResponseEntity<>(homeService
                    .getAllUsersByParameter(email,
                            password,
                            UriComponentsBuilder.fromUriString(searchTerm).build().encode().toUriString(),
                            selectedSearchOption,
                            currentPage,
                            sizeOnPage)
                    , HttpStatus.OK);

            case GET_SELECTED_USER ->
                    new ResponseEntity<>(homeService.getSelectedUser(email, password, selectedUserEmail), HttpStatus.OK);

            default -> throw new MissingParameterException();
        };
    }


    /**
     * Deletes a user based on the provided email.
     * <p>
     * This endpoint is mapped to "/home" using the HTTP DELETE method. It requires three
     * request parameters, "email", "password", and "userToDeleteEmail", to authenticate and delete a user.
     *
     * @param email             The email of the user for authentication.
     * @param password          The password of the user for authentication.
     * @param userToDeleteEmail The email of the user to be deleted.
     * @return ResponseEntity<?> A response entity with no content and a status of HttpStatus.OK if the user deletion is successful.
     * @apiNote This endpoint is designed to be used for deleting a user by providing valid email and password credentials,
     * and the email of the user to be deleted.
     */
    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestParam String email, @RequestParam String password, @RequestParam String userToDeleteEmail) {
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
        homeService.logoutUser(email, password);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Edits user details based on the provided email, password, and user email to change.
     * <p>
     * This endpoint is mapped to "/home" using the HTTP PUT method. It requires three
     * request parameters, "email", "password", "emailUserToChange", and a request body containing
     * the new user data, to authenticate and edit a user's details.
     *
     * @param email             The email of the user initiating the edit (for authentication).
     * @param password          The password of the user initiating the edit (for authentication).
     * @param emailUserToChange The email of the user whose details are to be edited.
     * @param newUserDataObject The request body containing the new user data.
     * @return ResponseEntity<AuthResponseDTO> A response entity containing the updated user details
     * and authentication data with a status of HttpStatus.OK if the edit is successful.
     * @apiNote This endpoint is designed to be used for editing a user's details by providing valid
     * email and password credentials, the email of the user whose details are to be edited,
     * and the new user data in the request body.
     */
    @PutMapping
    public ResponseEntity<AuthResponseDTO> editUser(@RequestParam String email,
                                                    @RequestParam String password,
                                                    @RequestParam String emailUserToChange,
                                                    @RequestBody EditDetailsDTO newUserDataObject) {

        return new ResponseEntity<>(homeService.editUserDetails(email, password, emailUserToChange, newUserDataObject), HttpStatus.OK);

    }
}
