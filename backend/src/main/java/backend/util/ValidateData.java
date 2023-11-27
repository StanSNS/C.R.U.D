package backend.util;

import backend.entity.UserEntity;
import backend.exception.AccessDeniedException;
import backend.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidateData {

    /**
     * initializing dependencies with lombok @RequiredArgsConstructor
     */
    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;


    /**
     * Validates the provided user credentials (email and password).
     *
     * @param email The email of the user for validation.
     * @param password The password of the user for validation.
     * @return UserEntity The user entity if validation is successful.
     * @throws AccessDeniedException If the provided credentials are invalid or the user
     *                               is not found, an AccessDeniedException is thrown.
     */
    public UserEntity validateUserWithPassword(String email, String password) {
        UserEntity userEntity = userEntityRepository.findByEmail(email);

        if (userEntity == null || !passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new AccessDeniedException();
        }
        return userEntity;
    }


}
