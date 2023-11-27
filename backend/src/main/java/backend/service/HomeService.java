package backend.service;

import backend.dto.UserDetailsDTO;
import backend.entity.UserEntity;
import backend.exception.AccessDeniedException;
import backend.exception.ResourceNotFoundException;
import backend.repository.UserEntityRepository;
import backend.util.ValidateData;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    /**
     * initializing dependencies with lombok @RequiredArgsConstructor
     */
    private final ValidateData validateData;
    private final UserEntityRepository userEntityRepository;
    private final ModelMapper modelMapper;


    /**
     * Retrieves a list of all users.
     *
     * @param email    The email of the requesting user.
     * @param password The password of the requesting user.
     * @return A list of UserDetailsDTO representing all users.
     */
    public List<UserDetailsDTO> getAllUsers(String email, String password) {
        validateData.validateUserWithPassword(email, password);

        return userEntityRepository
                .findAll()
                .stream()
                .map(user -> modelMapper
                        .map(user, UserDetailsDTO.class))
                .collect(Collectors.toList());
    }


    /**
     * Deletes a user based on the provided email.
     *
     * @param email               The email of the requesting user.
     * @param password            The password of the requesting user.
     * @param userToDeleteEmail   The email of the user to be deleted.
     * @throws ResourceNotFoundException If the user to be deleted is not found.
     */
    public void deleteUser(String email, String password, String userToDeleteEmail) {
        UserEntity userEntity = validateData.validateUserWithPassword(email, password);
        UserEntity userEntityToDelete = userEntityRepository.findByEmail(userToDeleteEmail);

        if (userEntityToDelete == null) {
            throw new ResourceNotFoundException();
        }

        if(!validateData.isUserAdmin(userEntity.getRoles()) || validateData.isUserAdmin(userEntityToDelete.getRoles())){
            throw new AccessDeniedException();
        }

        userEntityRepository.delete(userEntityToDelete);
    }


    /**
     * Logs out the authenticated user, clearing the security context.
     *
     * @param email    The email of the user requesting logout.
     * @param password The password of the user requesting logout.
     * @throws AccessDeniedException      If the provided credentials are invalid.
     */
    public void logoutUser(String email, String password) {
        validateData.validateUserWithPassword(email,password);
        SecurityContextHolder.clearContext();
    }
}
