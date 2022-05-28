package tqs.g11.deliverize.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
public class SignupRE {
    @Getter
    @Setter
    private List<String> errors;

    @Getter
    @Setter
    private UserDto userDto;

    public void addError(String error) {
        errors.add(error);
    }

    public SignupRE() {
        errors = new ArrayList<>();
    }
}
