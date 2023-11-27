package backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RegisterDTO {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @NotNull
    private String phoneNumber;

    @Email
    @NotNull
    private String email;

    @NotNull
    private String password;
}
