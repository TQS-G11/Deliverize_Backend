package tqs.g11.deliverize.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import tqs.g11.deliverize.model.User;

@NoArgsConstructor
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
    @JsonIgnoreProperties
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

    @Getter
    @Setter
    @URL
    private String img;

    @Getter
    @Setter
    private String companyDescription;

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
        img = user.getImg();
        companyDescription = user.getCompanyDescription();
    }

    public UserDto(Long id, String username, String name, String password, String role, String companyStatus,
                   String riderStatus, Double riderRating, Integer ratingCount, String img, String companyDescription) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.password = password;
        this.role = role;
        this.companyStatus = companyStatus;
        this.riderStatus = riderStatus;
        this.riderRating = riderRating;
        this.ratingCount = ratingCount;
        this.img = img == null ? User.DEFAULT_IMG : companyDescription;
        this.companyDescription = companyDescription;
    }
}
