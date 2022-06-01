package tqs.g11.deliverize.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class ChangeCompanyStatusRE {
    @Getter
    @Setter
    private List<String> errors = new ArrayList<>();

    @Getter
    @Setter
    private UserDto company;

    public void addError(String error) {
        errors.add(error);
    }
}
