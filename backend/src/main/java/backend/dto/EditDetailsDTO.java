package backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditDetailsDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;

}
