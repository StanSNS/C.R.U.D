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
     * Retrieves all users by default.
     *
     * @param email    Email of the user for authentication.
     * @param password Password of the user for authentication.
     * @return List<UserDetailsDTO> A list of UserDetailsDTO representing all users.
     */
    public List<UserDetailsDTO> getAllUsersByDefault(String email, String password) {
        validateData.validateUserWithPassword(email, password);

        return userEntityRepository
                .findAll()
                .stream()
                .map(user -> {
                    UserDetailsDTO userDetailsDTO = modelMapper.map(user, UserDetailsDTO.class);
                    if (!validationUtil.isValid(userDetailsDTO)) {
                        throw new DataValidationException();
                    }
                    return userDetailsDTO;
                })
                .collect(Collectors.toList());
    }


    /**
     * Retrieves all users ordered by last name and date of birth.
     *
     * @param email    Email of the user for authentication.
     * @param password Password of the user for authentication.
     * @return List<UserDetailsDTO> A list of UserDetailsDTO representing all users ordered by last name and date of birth.
     */
    public List<UserDetailsDTO> getAllUsersOrderedByLastNameAndDateOfBirth(String email, String password) {
        validateData.validateUserWithPassword(email, password);

        return userEntityRepository
                .findAllUsersOrderedByLastNameAndDateOfBirth()
                .stream()
                .map(user -> {
                    UserDetailsDTO userDetailsDTO = modelMapper.map(user, UserDetailsDTO.class);
                    if (!validationUtil.isValid(userDetailsDTO)) {
                        throw new DataValidationException();
                    }
                    return userDetailsDTO;
                })
                .collect(Collectors.toList());
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
     * Retrieves all users with a given last name.
     *
     * @param email                Email of the user for authentication.
     * @param password             Password of the user for authentication.
     * @param searchTerm           The provided value from the input;
     * @param selectedSearchOption The selected option from the field
     * @return List<UserDetailsDTO> A list of UserDetailsDTO representing users with the specified last name.
     */
    public List<UserDetailsDTO> getAllUsersByParameter(String email, String password, String searchTerm, String selectedSearchOption) {
        validateData.validateUserWithPassword(email, password);

        switch (selectedSearchOption) {
            case SEARCH_USERS_BY_FIRST_NAME -> {
                return userEntityRepository
                        .findAllByFirstName(searchTerm)
                        .stream()
                        .map(user -> {
                                    UserDetailsDTO userDetailsDTO = modelMapper.map(user, UserDetailsDTO.class);
                                    if (!validationUtil.isValid(userDetailsDTO)) {
                                        throw new DataValidationException();
                                    }
                                    return userDetailsDTO;
                                }
                        ).collect(Collectors.toList());
            }
            case SEARCH_USERS_BY_LAST_NAME -> {
                return userEntityRepository
                        .findAllByLastName(searchTerm)
                        .stream()
                        .map(user -> {
                                    UserDetailsDTO userDetailsDTO = modelMapper.map(user, UserDetailsDTO.class);
                                    if (!validationUtil.isValid(userDetailsDTO)) {
                                        throw new DataValidationException();
                                    }
                                    return userDetailsDTO;
                                }
                        ).collect(Collectors.toList());
            }
            case SEARCH_USERS_BY_PHONE_NUMBER -> {
                if (searchTerm.contains("%20")) {
                    searchTerm = searchTerm.replace("%20", "+");
                }

                return userEntityRepository
                        .findAllByPhoneNumber(searchTerm)
                        .stream()
                        .map(user -> {
                                    UserDetailsDTO userDetailsDTO = modelMapper.map(user, UserDetailsDTO.class);
                                    if (!validationUtil.isValid(userDetailsDTO)) {
                                        throw new DataValidationException();
                                    }
                                    return userDetailsDTO;
                                }
                        ).collect(Collectors.toList());
            }
            case SEARCH_USERS_BY_EMAIL -> {
                return userEntityRepository
                        .findAllByEmail(searchTerm)
                        .stream()
                        .map(user -> {
                                    UserDetailsDTO userDetailsDTO = modelMapper.map(user, UserDetailsDTO.class);
                                    if (!validationUtil.isValid(userDetailsDTO)) {
                                        throw new DataValidationException();
                                    }
                                    return userDetailsDTO;
                                }
                        ).collect(Collectors.toList());
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
     * @param email              The email of the user initiating the edit (for authentication).
     * @param password           The password of the user initiating the edit (for authentication).
     * @param emailUserToChange  The email of the user whose details are to be edited.
     * @param newUserDataObject  The request body containing the new user data.
     * @return AuthResponseDTO   A response DTO containing the updated user details.
     * @throws ResourceNotFoundException If the user to be edited is not found.
     * @throws ResourceAlreadyExistsException If the new email already exists in the system.
     * @throws AccessDeniedException If the requesting user does not have permission to edit the specified user.
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
     * @param userToBeEdited      The UserEntity object to be updated.
     * @param newUserDataObject   The EditDetailsDTO containing the new user data.
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
     * @param newValue      The new value to be set.
     * @param fieldUpdater  A Consumer function to update the field with the new value.
     */
    void updateFieldIfNotEmpty(String newValue, Consumer<String> fieldUpdater) {
        if (!newValue.trim().isEmpty()) {
            fieldUpdater.accept(newValue);
        }
    }

}