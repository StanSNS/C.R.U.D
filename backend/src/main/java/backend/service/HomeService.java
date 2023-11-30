package backend.service;

import backend.dto.AuthResponseDTO;
import backend.dto.EditDetailsDTO;
import backend.dto.UserDetailsDTO;
import backend.entity.RoleEntity;
import backend.entity.UserEntity;
import backend.exception.*;
import backend.repository.UserEntityRepository;
import backend.util.ValidateData;
import backend.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static backend.constants.ActionConst.*;

@Service
@RequiredArgsConstructor
public class HomeService {

    /**
     * initializing dependencies with lombok @RequiredArgsConstructor
     */
    private final ValidateData validateData;
    private final UserEntityRepository userEntityRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final PasswordEncoder passwordEncoder;


    /**
     * Retrieves a page of users with default sorting.
     *
     * @param email    The email for user validation.
     * @param password The password for user validation.
     * @param page     The page number to retrieve.
     * @param size     The number of items per page.
     * @return Page<UserDetailsDTO> A page of user details.
     * @throws DataValidationException If user data validation fails.
     */
    public Page<UserDetailsDTO> getAllUsersByDefault(String email, String password, Integer page, Integer size) {
        validateData.validateUserWithPassword(email, password);

        PageRequest pageRequest = PageRequest.of(page, size);


        return userEntityRepository
                .findAll(pageRequest)
                .map(user -> {
                    UserDetailsDTO userDetailsDTO = modelMapper.map(user, UserDetailsDTO.class);
                    if (!validationUtil.isValid(userDetailsDTO)) {
                        throw new DataValidationException();
                    }
                    return userDetailsDTO;
                });
    }


    /**
     * Retrieves a page of users ordered by last name and date of birth.
     *
     * @param email    The email for user validation.
     * @param password The password for user validation.
     * @param page     The page number to retrieve.
     * @param size     The number of items per page.
     * @return Page<UserDetailsDTO> A page of user details.
     * @throws DataValidationException If user data validation fails.
     */
    public Page<UserDetailsDTO> getAllUsersOrderedByLastNameAndDateOfBirth(String email, String password, Integer page, Integer size) {
        validateData.validateUserWithPassword(email, password);

        PageRequest pageRequest = PageRequest.of(page, size);

        return userEntityRepository
                .findAllUsersOrderedByLastNameAndDateOfBirth(pageRequest)
                .map(user -> {
                    UserDetailsDTO userDetailsDTO = modelMapper.map(user, UserDetailsDTO.class);
                    if (!validationUtil.isValid(userDetailsDTO)) {
                        throw new DataValidationException();
                    }
                    return userDetailsDTO;
                });
    }


    /**
     * Retrieves details of a selected user based on the provided email and password.
     *
     * @param email             The email of the user making the request.
     * @param password          The password of the user making the request.
     * @param selectedUserEmail The email of the user for whom details are to be retrieved.
     * @return UserDetailsDTO    Details of the selected user.
     * @throws ResourceNotFoundException If the user with the specified email is not found.
     * @throws DataValidationException   If the retrieved user details are not valid.
     */
    public UserDetailsDTO getSelectedUser(String email, String password, String selectedUserEmail) {
        validateData.validateUserWithPassword(email, password);

        UserEntity userEntity = userEntityRepository.findByEmail(selectedUserEmail);
        if (userEntity == null) {
            throw new ResourceNotFoundException();
        }

        UserDetailsDTO userDetailsDTO = modelMapper.map(userEntity, UserDetailsDTO.class);
        if (!validationUtil.isValid(userDetailsDTO)) {
            throw new DataValidationException();
        }

        return userDetailsDTO;
    }


    /**
     * Retrieves a page of users based on the specified search parameters.
     *
     * @param email                The email for user validation.
     * @param password             The password for user validation.
     * @param searchTerm           The term to search for.
     * @param selectedSearchOption The option selected for searching (e.g., by first name, last name, etc.).
     * @param page                 The page number to retrieve.
     * @param size                 The number of items per page.
     * @return Page<UserDetailsDTO> A page of user details based on the search criteria.
     * @throws DataValidationException   If user data validation fails.
     * @throws MissingParameterException If required parameters are missing.
     */
    public Page<UserDetailsDTO> getAllUsersByParameter(String email, String password, String searchTerm, String selectedSearchOption, Integer page, Integer size) {
        validateData.validateUserWithPassword(email, password);

        PageRequest pageRequest = PageRequest.of(page, size);

        switch (selectedSearchOption) {
            case SEARCH_USERS_BY_FIRST_NAME -> {
                return userEntityRepository
                        .findAllByFirstName(searchTerm, pageRequest)
                        .map(user -> {
                                    UserDetailsDTO userDetailsDTO = modelMapper.map(user, UserDetailsDTO.class);
                                    if (!validationUtil.isValid(userDetailsDTO)) {
                                        throw new DataValidationException();
                                    }
                                    return userDetailsDTO;
                                }
                        );
            }
            case SEARCH_USERS_BY_LAST_NAME -> {

                return userEntityRepository
                        .findAllByLastName(searchTerm, pageRequest)
                        .map(user -> {
                                    UserDetailsDTO userDetailsDTO = modelMapper.map(user, UserDetailsDTO.class);
                                    if (!validationUtil.isValid(userDetailsDTO)) {
                                        throw new DataValidationException();
                                    }
                                    return userDetailsDTO;
                                }
                        );
            }
            case SEARCH_USERS_BY_PHONE_NUMBER -> {
                if (searchTerm.contains("%20")) {
                    searchTerm = searchTerm.replace("%20", "+");
                }

                return userEntityRepository
                        .findAllByPhoneNumber(searchTerm, pageRequest)
                        .map(user -> {
                                    UserDetailsDTO userDetailsDTO = modelMapper.map(user, UserDetailsDTO.class);
                                    if (!validationUtil.isValid(userDetailsDTO)) {
                                        throw new DataValidationException();
                                    }
                                    return userDetailsDTO;
                                }
                        );
            }
            case SEARCH_USERS_BY_EMAIL -> {
                return userEntityRepository
                        .findAllByEmail(searchTerm, pageRequest)
                        .map(user -> {
                                    UserDetailsDTO userDetailsDTO = modelMapper.map(user, UserDetailsDTO.class);
                                    if (!validationUtil.isValid(userDetailsDTO)) {
                                        throw new DataValidationException();
                                    }
                                    return userDetailsDTO;
                                }
                        );
            }
        }

        throw new MissingParameterException();
    }


    /**
     * Deletes a user based on the provided email.
     *
     * @param email             The email of the requesting user.
     * @param password          The password of the requesting user.
     * @param userToDeleteEmail The email of the user to be deleted.
     * @throws ResourceNotFoundException If the user to be deleted is not found.
     */
    public void deleteUser(String email, String password, String userToDeleteEmail) {
        UserEntity userEntity = validateData.validateUserWithPassword(email, password);
        UserEntity userEntityToDelete = userEntityRepository.findByEmail(userToDeleteEmail);

        if (userEntityToDelete == null) {
            throw new ResourceNotFoundException();
        }

        if (!validateData.isUserAdmin(userEntity.getRoles()) || validateData.isUserAdmin(userEntityToDelete.getRoles())) {
            throw new AccessDeniedException();
        }

        userEntityRepository.delete(userEntityToDelete);
    }


    /**
     * Logs out the authenticated user, clearing the security context.
     *
     * @param email    The email of the user requesting logout.
     * @param password The password of the user requesting logout.
     * @throws AccessDeniedException If the provided credentials are invalid.
     */
    public void logoutUser(String email, String password) {
        validateData.validateUserWithPassword(email, password);
        SecurityContextHolder.clearContext();
    }


    /**
     * Edits user details based on the provided email, password, and user email to change.
     *
     * @param email             The email of the user initiating the edit (for authentication).
     * @param password          The password of the user initiating the edit (for authentication).
     * @param emailUserToChange The email of the user whose details are to be edited.
     * @param newUserDataObject The request body containing the new user data.
     * @return AuthResponseDTO   A response DTO containing the updated user details.
     * @throws ResourceNotFoundException      If the user to be edited is not found.
     * @throws ResourceAlreadyExistsException If the new email already exists in the system.
     * @throws AccessDeniedException          If the requesting user does not have permission to edit the specified user.
     */
    public AuthResponseDTO editUserDetails(String email, String password, String emailUserToChange, EditDetailsDTO newUserDataObject) {
        UserEntity loggedUser = validateData.validateUserWithPassword(email, password);

        UserEntity userToBeEdited = userEntityRepository.findByEmail(emailUserToChange);

        if (userEntityRepository.existsByEmail(newUserDataObject.getEmail())) {
            throw new ResourceAlreadyExistsException();
        }

        if (userToBeEdited == null) {
            throw new ResourceNotFoundException();
        }

        if (!loggedUser.getEmail().equals(userToBeEdited.getEmail())) {
            throw new AccessDeniedException();
        }

        UserEntity savedUserEntity = updateUserData(userToBeEdited, newUserDataObject);

        userEntityRepository.save(savedUserEntity);

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();

        if (!newUserDataObject.getEmail().trim().isEmpty()) {
            authResponseDTO.setEmail(newUserDataObject.getEmail());
        }

        if (!newUserDataObject.getPassword().trim().isEmpty()) {
            authResponseDTO.setPassword(newUserDataObject.getPassword());
        }

        if (!newUserDataObject.getFirstName().trim().isEmpty()) {
            authResponseDTO.setFirstName(newUserDataObject.getFirstName());
        }

        authResponseDTO.setRoles(savedUserEntity
                .getRoles()
                .stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet()));

        return authResponseDTO;
    }

    /**
     * Updates the user data based on the provided EditDetailsDTO.
     *
     * @param userToBeEdited    The UserEntity object to be updated.
     * @param newUserDataObject The EditDetailsDTO containing the new user data.
     * @return UserEntity         The updated UserEntity object.
     */
    UserEntity updateUserData(UserEntity userToBeEdited, EditDetailsDTO newUserDataObject) {
        updateFieldIfNotEmpty(newUserDataObject.getFirstName(), userToBeEdited::setFirstName);
        updateFieldIfNotEmpty(newUserDataObject.getLastName(), userToBeEdited::setLastName);
        updateFieldIfNotEmpty(newUserDataObject.getEmail(), userToBeEdited::setEmail);
        updateFieldIfNotEmpty(newUserDataObject.getPhoneNumber(), userToBeEdited::setPhoneNumber);
        updateFieldIfNotEmpty(newUserDataObject.getPassword(), password -> userToBeEdited.setPassword(passwordEncoder.encode(password)));

        return userToBeEdited;

    }

    /**
     * Updates a user field if the provided new value is not empty.
     *
     * @param newValue     The new value to be set.
     * @param fieldUpdater A Consumer function to update the field with the new value.
     */
    void updateFieldIfNotEmpty(String newValue, Consumer<String> fieldUpdater) {
        if (!newValue.trim().isEmpty()) {
            fieldUpdater.accept(newValue);
        }
    }

}