package backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserDetailsDTO {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String dateOfBirth;

    @NotNull
    private Set<RoleDTO> roles;

    @NotNull
    private String email;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String registerDate;

    @NotNull
    private String country;

    @NotNull
    private String currency;

    @NotNull
    private String city;

}
