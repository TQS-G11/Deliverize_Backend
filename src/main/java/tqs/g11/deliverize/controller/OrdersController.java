package tqs.g11.deliverize.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tqs.g11.deliverize.dto.CreateOrderRE;
import tqs.g11.deliverize.dto.OrderDto;
import tqs.g11.deliverize.dto.OrdersRE;
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
        return ordersService.findOrders(id, companyId, riderId, buyer, destination, notes, deliveryStatus, origin,
                price, requestedAt, acceptedAt, deliveredAt);
    }

    @PostMapping("")
    @PreAuthorize("hasAnyRole('COMPANY')")
    public ResponseEntity<CreateOrderRE> createOrder(Authentication auth, @RequestBody OrderDto orderDto) {
        return ordersService.createOrder(auth, orderDto);
    }

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }
}
