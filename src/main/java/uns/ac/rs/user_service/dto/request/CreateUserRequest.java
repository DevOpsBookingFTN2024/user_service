package uns.ac.rs.user_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank
    @Size(min = 5, max = 20)
    private String username;
}
