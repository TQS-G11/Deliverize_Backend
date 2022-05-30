package tqs.g11.deliverize.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import io.swagger.v3.oas.annotations.media.Schema;
import tqs.g11.deliverize.dto.OrderDto;
import tqs.g11.deliverize.enums.DeliveryStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_orders")
@NoArgsConstructor
public class Order {
    @Schema(description = "Order ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;

    @Schema(description = "Company ordered through")
    @Getter
    @Setter
    @ManyToOne(optional = false)
    private User company;

    @Schema(description = "Rider that accepted the delivery")
    @ManyToOne
    @Getter
    @Setter
    private User rider;

    @Schema(description = "Buyer name")
    @Column(name = "buyer", nullable = false)
    @Getter
    @Setter
    private String buyer;

    @Schema(description = "Address to be delivered to")
    @Column(name = "destination", nullable = false)
    @Getter
    @Setter
    private String destination;

    @Schema(description = "Additional notes")
    @Column(name = "notes")
    @Getter
    @Setter
    private String notes;

    @Schema(description = "Delivery status")
    @Column(name = "deliveryStatus", nullable = false)
    @Getter
    @Setter
    private String deliveryStatus;

    @Schema(description = "Address of the store to be picked up from")
    @Column(name = "origin", nullable = false)
    @Getter
    @Setter
    private String origin;

    @Schema(description = "Price to pay for the delivery")
    @Column(name = "price", nullable = false)
    @Getter
    @Setter
    private Double price;

    @Schema(description = "Time of request")
    @Column(name = "requestedAt", nullable = false)
    @Getter
    @Setter
    private LocalDateTime requestedAt;

    @Schema(description = "Time of acceptance by rider")
    @Column(name = "acceptedAt")
    @Getter
    @Setter
    private LocalDateTime acceptedAt;

    @Schema(description = "Time delivered at")
    @Column(name = "deliveredAt")
    @Getter
    @Setter
    private LocalDateTime deliveredAt;

    @Schema(description = "Latitude of the driver")
    @Column(name = "driverLat")
    @Getter
    @Setter
    private Double driverLat;

    @Schema(description = "Longitude of the driver")
    @Column(name = "driverLon")
    @Getter
    @Setter
    private Double driverLon;

    @Schema(description = "Latitude of the store to be picked up from")
    @Column(name = "storeLat")
    @Getter
    @Setter
    private Double storeLat;

    @Schema(description = "Longitude of the store to be picked up from")
    @Column(name = "driverLon")
    @Getter
    @Setter
    private Double storeLon;

    public Order(OrderDto orderDto) {
        this.company = orderDto.getCompany();
        this.rider = null;
        this.buyer = orderDto.getBuyer();
        this.destination = orderDto.getDestination();
        this.notes = orderDto.getNotes();
        this.deliveryStatus = DeliveryStatus.REQUESTED.toString();
        this.origin = orderDto.getOrigin();
        this.price = orderDto.getPrice();
    }
}
