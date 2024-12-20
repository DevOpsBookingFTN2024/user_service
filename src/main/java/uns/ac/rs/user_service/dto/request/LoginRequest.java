package uns.ac.rs.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username is required.")
    @Size(min = 5, max = 20)
    private String username;

    @NotBlank(message = "Password is required.")
    @Size(min = 5, max = 20)
    private String password;
}
