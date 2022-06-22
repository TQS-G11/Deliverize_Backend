package tqs.g11.deliverize.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String password;
}
