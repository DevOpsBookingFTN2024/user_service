package uns.ac.rs.user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String token;

    private String type = "Bearer";

    private UUID id;

    private String username;

    private String emailAddress;

    private List<String> roles;

    public JwtResponse(String accessToken,
                       UUID id,
                       String username,
                       String emailAddress,
                       List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.emailAddress = emailAddress;
        this.roles = roles;
    }
}
