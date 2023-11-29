package backend.service;

import backend.dto.UserDetailsDTO;
import backend.entity.Base.BaseEntity;
import backend.entity.UserEntity;
import backend.exception.AccessDeniedException;
import backend.exception.DataValidationException;
import backend.exception.MissingParameterException;
import backend.exception.ResourceNotFoundException;
import backend.repository.UserEntityRepository;
import backend.util.ValidateData;
import backend.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
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
     * Retrieves a random user.
     *
     * @param email    Email of the user for authentication.
     * @param password Password of the user for authentication.
     * @return List<UserDetailsDTO> A list containing a single UserDetailsDTO representing a randomly selected user.
     * @throws ResourceNotFoundException if no users are available.
     */
    public List<UserDetailsDTO> getRandomUser(String email, String password) {
        validateData.validateUserWithPassword(email, password);

        List<Long> userIDs = userEntityRepository.findAll().stream().map(BaseEntity::getId).toList();

        int lowerBound = 1;
        int upperBound = userIDs.size();

        if (upperBound < lowerBound) {
            throw new ResourceNotFoundException();
        }

        int randomNumber = new Random().nextInt(upperBound - lowerBound + 1) + lowerBound;
        Long randomUserID = userIDs.get(randomNumber - 1);

        return userEntityRepository
                .findById(randomUserID)
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
     * Retrieves all users with a given last name.
     *
     * @param email          Email of the user for authentication.
     * @param password       Password of the user for authentication.
     * @param searchTerm     The provided value from the input;
     * @param selectedSearchOption     The selected option from the field
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
                if(searchTerm.contains("%20")){
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
     * Changes the phone number of a user.
     *
     * @param email             The email of the user initiating the change.
     * @param password          The password of the user initiating the change.
     * @param emailUserToChange The email of the user whose phone number is to be changed.
     * @param phoneNumber       The new phone number to be set for the user.
     * @throws ResourceNotFoundException If the user with the specified emailUserToChange is not found.
     */
    public void changePhoneNumber(String email, String password, String emailUserToChange, String phoneNumber) {
        validateData.validateUserWithPassword(email, password);

        UserEntity userEntity = userEntityRepository.findByEmail(emailUserToChange);

        if (userEntity == null) {
            throw new ResourceNotFoundException();
        }
        userEntity.setPhoneNumber(phoneNumber);
        userEntityRepository.save(userEntity);
    }
}
