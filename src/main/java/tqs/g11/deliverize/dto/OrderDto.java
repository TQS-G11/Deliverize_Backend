package tqs.g11.deliverize.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tqs.g11.deliverize.model.Order;
import tqs.g11.deliverize.model.User;

@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private User company;

    @Getter
    @Setter
    private User rider;

    @Getter
    @Setter
    private String buyer;

    @Getter
    @Setter
    private String destination;

    @Getter
    @Setter
    private String notes;

    @Getter
    @Setter
    private String deliveryStatus;

    @Getter
    @Setter
    private String origin;

    @Getter
    @Setter
    private Double price;

    public OrderDto(Order order) {
        this.id = order.getId();
        this.company = order.getCompany();
        this.rider = order.getRider();
        this.buyer = order.getBuyer();
        this.destination = order.getDestination();
        this.notes = order.getNotes();
        this.deliveryStatus = order.getDeliveryStatus();
        this.origin = order.getOrigin();
        this.price = order.getPrice();
    }
}
