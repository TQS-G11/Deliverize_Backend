package tqs.g11.deliverize.dto;

import lombok.Getter;
import lombok.Setter;
import tqs.g11.deliverize.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrdersRE {
    @Getter
    @Setter
    private List<String> errors;

    @Getter
    @Setter
    private List<Order> orders;

    public OrdersRE() {
        errors = new ArrayList<>();
        orders = new ArrayList<>();
    }

    public void addError(String error) {
        errors.add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
