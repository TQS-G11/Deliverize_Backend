package tqs.g11.deliverize.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tqs.g11.deliverize.model.User;

@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String role;

    @Getter
    @Setter
    private String companyStatus;

    @Getter
    @Setter
    private String riderStatus;

    @Getter
    @Setter
    private Double riderRating;

    @Getter
    @Setter
    private Integer ratingCount;

    public UserDto(User user) {
        id = user.getId();
        username = user.getUsername();
        name = user.getName();
        password = user.getPassword();
        role = user.getRole();
        companyStatus = user.getCompanyStatus();
        riderStatus = user.getRiderStatus();
        riderRating = user.getRiderRating();
        ratingCount = user.getRatingCount();
    }
}
