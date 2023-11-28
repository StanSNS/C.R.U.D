package backend.repository;

import backend.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository for managing roles in the database.
@Repository
public interface RoleEntityRepository extends JpaRepository<RoleEntity, Long> {

    /**
     * Finds a role by its name.
     *
     * @param name The name of the role to find.
     * @return RoleEntity The role entity with the specified name.
     */
    RoleEntity findByName(String name);

}
