package backend.repository;

import backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository for managing users in the database.
@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Finds all users with a given last name.
     *
     * @param name The last name to search for.
     * @return List<UserEntity> A list of user entities with the specified last name.
     */
    List<UserEntity> findAllByLastName(String name);

    /**
     * Finds all users ordered by last name and date of birth.
     *
     * @return List<UserEntity> A list of user entities ordered by last name and date of birth.
     */
    @Query(value = "SELECT * FROM users ORDER BY last_name, STR_TO_DATE(date_of_birth, '%d/%m/%Y')", nativeQuery = true)
    List<UserEntity> findAllUsersOrderedByLastNameAndDateOfBirth();


    /**
     * Finds a user by email.
     *
     * @param email The email of the user to find.
     * @return UserEntity The user entity with the specified email.
     */
    UserEntity findByEmail(String email);


    /**
     * Checks if a user exists with the given email.
     *
     * @param email The email to check for existence.
     * @return boolean True if a user with the email exists, false otherwise.
     */
    boolean existsByEmail(String email);

}