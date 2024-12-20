package uns.ac.rs.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeRequest {
    @NotBlank(message = "Old password is required.")
    @Size(min = 5, max = 20)
    private String oldPassword;

    @NotBlank(message = "New password is required.")
    @Size(min = 5, max = 20)
    private String newPassword;

    @NotBlank(message = "Repeated new password is required.")
    @Size(min = 5, max = 20)
    private String repeatNewPassword;
}
