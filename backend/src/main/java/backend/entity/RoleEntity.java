package backend.entity;

import backend.entity.Base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table(name = "roles")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity extends BaseEntity {

    @NotNull
    @Size(min = 4, max = 6)
    private String name;


}
