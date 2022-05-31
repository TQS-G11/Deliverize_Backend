package tqs.g11.deliverize.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tqs.g11.deliverize.model.Order;
import tqs.g11.deliverize.model.User;

import java.time.LocalDateTime;

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

    @Getter
    @Setter
    private LocalDateTime requestedAt;

    @Getter
    @Setter
    private LocalDateTime acceptedAt;

    @Getter
    @Setter
    private LocalDateTime deliveredAt;

    @Getter
    @Setter
    private Double driverLat;

    @Getter
    @Setter
    private Double driverLon;

    @Getter
    @Setter
    private Double storeLat;

    @Getter
    @Setter
    private Double storeLon;

    @Getter
    @Setter
    private Double riderRating;

    public OrderDto(Order order) {
        id = order.getId();
        company = order.getCompany();
        rider = order.getRider();
        buyer = order.getBuyer();
        destination = order.getDestination();
        notes = order.getNotes();
        deliveryStatus = order.getDeliveryStatus();
        origin = order.getOrigin();
        price = order.getPrice();
        requestedAt = order.getRequestedAt();
        acceptedAt = order.getAcceptedAt();
        deliveredAt = order.getDeliveredAt();
        driverLat = order.getDriverLat();
        driverLon = order.getDriverLon();
        storeLat = order.getStoreLat();
        storeLon = order.getStoreLon();
        riderRating = order.getRiderRating();
    }
}
