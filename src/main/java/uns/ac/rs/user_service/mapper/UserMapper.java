package uns.ac.rs.user_service.mapper;

import uns.ac.rs.user_service.dto.UserDTO;
import uns.ac.rs.user_service.model.User;

public class UserMapper {
    public static UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
