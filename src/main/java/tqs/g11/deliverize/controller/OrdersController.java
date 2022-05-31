package tqs.g11.deliverize.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tqs.g11.deliverize.dto.*;
import tqs.g11.deliverize.service.OrdersService;

import java.time.LocalDateTime;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/deliveries")
public class OrdersController {
    private final OrdersService ordersService;

    @GetMapping()
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<OrdersRE> findOrders(
            Long id,
            Long companyId,
            Long riderId,
            String buyer,
            String destination,
            String notes,
            String deliveryStatus,
            String origin,
            Double price,
            LocalDateTime requestedAt,
            LocalDateTime acceptedAt,
            LocalDateTime deliveredAt
    ) {
        return ordersService.managerFindOrders(id, companyId, riderId, buyer, destination, notes, deliveryStatus, origin,
                price, requestedAt, acceptedAt, deliveredAt);
    }

    @PostMapping("/company")
    @PreAuthorize("hasAnyRole('COMPANY')")
    public ResponseEntity<CreateOrderRE> createOrder(Authentication auth, @RequestBody OrderDto orderDto) {
        return ordersService.companyCreateOrder(auth, orderDto);
    }

    @PostMapping("/rider/accept")
    @PreAuthorize("hasAnyRole('RIDER')")
    public ResponseEntity<AcceptOrderRE> acceptOrder(Authentication auth, @RequestBody Long orderId) {
        return ordersService.riderAcceptOrder(auth, orderId);
    }

    @PostMapping("/rider/update")
    @PreAuthorize("hasAnyRole('RIDER')")
    public ResponseEntity<AcceptOrderRE> updateCurrentOrderStatus(Authentication auth) {
        return ordersService.riderUpdateCurrentOrderStatus(auth);
    }

    @PostMapping("/company/rate-rider")
    @PreAuthorize("hasAnyRole('COMPANY')")
    public ResponseEntity<RatingRE> rateRider(Authentication auth, @RequestAttribute Double rating,
                                              @RequestAttribute Long orderId) {
        return ordersService.companyRateRider(auth, rating, orderId);
    }

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }
}
