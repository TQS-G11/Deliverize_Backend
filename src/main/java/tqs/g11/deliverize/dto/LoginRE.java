package tqs.g11.deliverize.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class LoginRE {
    @Getter
    @Setter
    private List<String> errors;

    @Getter
    @Setter
    private AuthToken token;

    public LoginRE() {
        errors = new ArrayList<>();
    }

    public void addError(String error) {
        errors.add(error);
    }
}
