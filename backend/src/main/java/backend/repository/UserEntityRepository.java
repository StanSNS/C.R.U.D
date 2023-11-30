package backend.repository;

import backend.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository for managing users in the database.
@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Finds users by last name.
     *
     * @param lastName The last name to search for.
     * @param pageable Pagination information.
     * @return Page<UserEntity> A page of users with the specified last name.
     */
    Page<UserEntity> findAllByLastName(String lastName, Pageable pageable);


    /**
     * Finds users by first name.
     *
     * @param firstName The first name to search for.
     * @param pageable Pagination information.
     * @return Page<UserEntity> A page of users with the specified first name.
     */
    Page<UserEntity> findAllByFirstName(String firstName, Pageable pageable);


    /**
     * Finds users by phone number.
     *
     * @param phoneNumber The phone number to search for.
     * @param pageable Pagination information.
     * @return Page<UserEntity> A page of users with the specified phone number.
     */
    Page<UserEntity> findAllByPhoneNumber(String phoneNumber, Pageable pageable);


    /**
     * Finds users by email.
     *
     * @param email The email to search for.
     * @param pageable Pagination information.
     * @return Page<UserEntity> A page of users with the specified email.
     */
    Page<UserEntity> findAllByEmail(String email, Pageable pageable);


    /**
     * Finds all users.
     *
     * @param pageable Pagination information.
     * @return Page<UserEntity> A page containing all users.
     */
    Page<UserEntity> findAll(Pageable pageable);


    /**
     * Finds all users ordered by last name and date of birth.
     *
     * @param pageable Pagination information.
     * @return Page<UserEntity> A page of users ordered by last name and date of birth.
     */
    @Query(value = "SELECT * FROM users ORDER BY last_name, STR_TO_DATE(date_of_birth, '%d/%m/%Y')", nativeQuery = true)
    Page<UserEntity> findAllUsersOrderedByLastNameAndDateOfBirth(Pageable pageable);


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