package uns.ac.rs.user_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @NotBlank(message = "Username is required.")
    @Size(min = 5, max = 20)
    private String username;

    @NotBlank(message = "Email address is required.")
    @Size(max = 50)
    @Email
    private String emailAddress;

    @NotBlank(message = "First name is required.")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 50)
    private String lastName;

    @NotBlank(message = "Residence is required.")
    @Size(max = 50)
    private String residence;
}
