package tqs.g11.deliverize.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @Operation(summary = "As a manager, find orders, with optional filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relevant orders found."),
            @ApiResponse(responseCode = "401", description = "Unauthenticated."),
            @ApiResponse(responseCode = "403", description = "Unauthorized (not a manager).")
    })
    @GetMapping()
    @PreAuthorize("hasAnyRole('MANAGER','RIDER')")
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
        return ordersService.managerFindOrders(id, companyId, riderId, buyer, destination, notes, deliveryStatus,
                origin, price, requestedAt, acceptedAt, deliveredAt);
    }

    @Operation(summary = "Place an order as a company, on behalf of one of its users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order placed."),
            @ApiResponse(responseCode = "400", description = "Order not placed (invalid request)."),
            @ApiResponse(responseCode = "401", description = "Unauthenticated."),
            @ApiResponse(responseCode = "403", description = "Unauthorized (not a company).")
    })
    @PostMapping("/company")
    @PreAuthorize("hasAnyRole('COMPANY')")
    public ResponseEntity<CreateOrderRE> createOrder(Authentication auth, @RequestBody OrderDto orderDto) {
        return ordersService.companyCreateOrder(auth, orderDto);
    }

    @Operation(summary = "Accept a delivery as a rider.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Delivery accepted."),
            @ApiResponse(responseCode = "400", description = "Delivery not accepted (invalid request)."),
            @ApiResponse(responseCode = "401", description = "Unauthenticated."),
            @ApiResponse(responseCode = "403", description = "Unauthorized (not a rider).")
    })
    @PostMapping("/rider/accept")
    @PreAuthorize("hasAnyRole('RIDER')")
    public ResponseEntity<AcceptOrderRE> acceptOrder(Authentication auth, @RequestBody Long orderId) {
        return ordersService.riderAcceptOrder(auth, orderId);
    }

    @Operation(summary = "Update the status of a delivery as a rider. FETCHING -> DELIVERING, DELIVERING -> DELIVERED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Delivery status updated."),
            @ApiResponse(responseCode = "400", description = "Delivery status not updated (invalid request)."),
            @ApiResponse(responseCode = "401", description = "Unauthenticated."),
            @ApiResponse(responseCode = "403", description = "Unauthorized (not a rider).")
    })
    @PostMapping("/rider/update")
    @PreAuthorize("hasAnyRole('RIDER')")
    public ResponseEntity<AcceptOrderRE> updateCurrentOrderStatus(Authentication auth) {
        return ordersService.riderUpdateCurrentOrderStatus(auth);
    }

    @Operation(summary = "Rate a rider's delivery as a company, on behalf of the buyer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rating saved."),
            @ApiResponse(responseCode = "400", description = "Rating not saved (invalid request)."),
            @ApiResponse(responseCode = "401", description = "Unauthenticated."),
            @ApiResponse(responseCode = "403", description = "Unauthorized (not a company).")
    })
    @PostMapping("/company/rate-rider")
    @PreAuthorize("hasAnyRole('COMPANY')")
    public ResponseEntity<RatingRE> rateRider(Authentication auth, @RequestAttribute Double rating,
                                              @RequestAttribute Long orderId) {
        return ordersService.companyRateRider(auth, rating, orderId);
    }

    @Operation(summary = "Get a Order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order obtained."),
            @ApiResponse(responseCode = "400", description = "Requested Order Id does not exist (invalid request)."),
            @ApiResponse(responseCode = "401", description = "Unauthenticated."),
            @ApiResponse(responseCode = "403", description = "Unauthorized (not a company).")
    })
    @GetMapping("/{delivery_id}")
    @PreAuthorize("hasAnyRole('COMPANY')")
    public ResponseEntity<OrderRE> getDeliveryByID(Authentication auth, @PathVariable("delivery_id") Long deliveryId) {
        System.out.println("Sussy delivery id");
        return ordersService.getOrderById(deliveryId);
    }

    @Operation(summary = "Get the Orders made by a Company User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders obtained."),
            @ApiResponse(responseCode = "400", description = "Rating not saved (invalid request)."),
            @ApiResponse(responseCode = "401", description = "Unauthenticated."),
            @ApiResponse(responseCode = "403", description = "Unauthorized (not a company).")
    })
    @GetMapping("/company/buyer/{buyer}")
    @PreAuthorize("hasAnyRole('COMPANY')")
    public ResponseEntity<OrdersRE> getDeliveryByCompanyUser(Authentication auth, @PathVariable String buyer) {
//        return ordersService.getOrderById(deliveryId);
        System.out.println("Sussy delivery by buyer");
        return ordersService.getOrdersByBuyer(auth, buyer);
    }
}
