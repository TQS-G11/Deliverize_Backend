package tqs.g11.deliverize.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import tqs.g11.deliverize.dto.UserDto;
import tqs.g11.deliverize.enums.CompanyStatus;
import tqs.g11.deliverize.enums.RiderStatus;
import tqs.g11.deliverize.enums.UserRoles;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Table(name = "app_users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    public static final String DEFAULT_IMG =
            "https://www.seekpng.com/png/detail/428-4287240_no-avatar-user-circle-icon-png.png";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @Column(unique = true)
    private String username;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @JsonIgnore
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

    public User(UserDto dto) {
        username = dto.getUsername();
        name = dto.getName();
        password = dto.getPassword();
        role = dto.getRole();
        ratingCount = dto.getRatingCount();
        companyStatus = (dto.getRole().equals(UserRoles.COMPANY.toString()) ?
                CompanyStatus.PENDING : CompanyStatus.NOT_COMPANY).toString();
        if (dto.getRole().equals(UserRoles.RIDER.toString())) {
            riderStatus = RiderStatus.FREE.toString();
            riderRating = .0;
        } else
            riderStatus = RiderStatus.NOT_RIDER.toString();
    }

    public User(String username, String name, String password, UserRoles role, String img, String companyDescription) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.role = role.toString();
        this.img = img;
        companyStatus = (role.equals(UserRoles.COMPANY) ?
                CompanyStatus.PENDING : CompanyStatus.NOT_COMPANY).toString();
        if (role.equals(UserRoles.RIDER)) {
            riderStatus = RiderStatus.FREE.toString();
            riderRating = .0;
        } else if (role.equals(UserRoles.COMPANY)) {
            this.companyDescription = companyDescription == null ? "" : companyDescription;
        } else
            riderStatus = RiderStatus.NOT_RIDER.toString();
    }

    public User(String username, String name, String password, UserRoles role) {
        this(username, name, password, role, DEFAULT_IMG, null);
    }

    public void addRiderRating(Double rating) {
        assert role.equals(UserRoles.RIDER.toString());
        riderRating = (Optional.ofNullable(riderRating).orElse(.0) + rating) / ++ratingCount;
    }
}
