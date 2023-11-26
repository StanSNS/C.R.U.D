package backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class AuthResponseDTO {

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private Set<RoleDTO> roles;

}
