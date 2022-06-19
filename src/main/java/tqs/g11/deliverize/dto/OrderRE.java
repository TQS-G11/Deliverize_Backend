package tqs.g11.deliverize.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tqs.g11.deliverize.model.Order;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class OrderRE {
    @Getter
    @Setter
    private List<String> errors = new ArrayList<>();

    @Getter
    @Setter
    private Order order;

    public void addError(String error) {
        errors.add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
