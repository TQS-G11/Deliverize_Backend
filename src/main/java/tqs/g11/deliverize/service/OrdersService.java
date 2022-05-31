package tqs.g11.deliverize.service;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tqs.g11.deliverize.dto.*;
import tqs.g11.deliverize.enums.CompanyStatus;
import tqs.g11.deliverize.enums.DeliveryStatus;
import tqs.g11.deliverize.enums.RiderStatus;
import tqs.g11.deliverize.enums.UserRoles;
import tqs.g11.deliverize.model.Order;
import tqs.g11.deliverize.model.User;
import tqs.g11.deliverize.repository.OrdersRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrdersService {
    private final OrdersRepository ordersRepository;

    private final UsersService usersService;

    public OrdersService(OrdersRepository ordersRepository, UsersService usersService) {
        this.ordersRepository = ordersRepository;
        this.usersService = usersService;
    }

    public ResponseEntity<OrdersRE> managerFindOrders(
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
        OrdersRE re = new OrdersRE();

        Optional<User> companyOpt = usersService.getUserById(companyId);
        if (companyId != null && (companyOpt.isEmpty() || !companyOpt.get().getRole().equals(UserRoles.COMPANY.toString())))
            re.addError("Company with provided ID not found.");
        Optional<User> riderOpt = usersService.getUserById(riderId);
        if (riderId != null && (riderOpt.isEmpty() || !riderOpt.get().getRole().equals(UserRoles.RIDER.toString())))
            re.addError("Rider with provided ID not found.");

        if (re.getErrors().isEmpty()) {
            User company = companyOpt.orElse(null);
            User rider = riderOpt.orElse(null);
            re.setOrders(ordersRepository.findOrders(id, company, rider, buyer, destination, notes,
                    deliveryStatus, origin, price, requestedAt, acceptedAt, deliveredAt));
            return ResponseEntity.ok().body(re);
        }

        return ResponseEntity.badRequest().body(re);
    }

    public ResponseEntity<CreateOrderRE> companyCreateOrder(Authentication auth, OrderDto dto) {
        User company = usersService.getAuthUser((UserDetails) auth.getPrincipal());
        dto.setCompany(company);

        CreateOrderRE re = new CreateOrderRE();

        if (!company.getCompanyStatus().equals(CompanyStatus.APPROVED.toString()))
            re.addError("Company not approved.");
        if (stringIsNullOrBlank(dto.getBuyer()))
            re.addError("Buyer cannot be null or blank.");
        if (stringIsNullOrBlank(dto.getDestination()))
            re.addError("Destination cannot be null or blank.");
        if (dto.getNotes() == null)
            dto.setNotes("");
        if (stringIsNullOrBlank(dto.getOrigin()))
            re.addError("Origin cannot be null or blank.");

        if (re.getErrors().isEmpty()) {
            Order order = ordersRepository.save(new Order(dto));
            re.setOrderDto(new OrderDto(order));
            return ResponseEntity.ok().body(re);
        }

        return ResponseEntity.badRequest().body(re);
    }

    public ResponseEntity<AcceptOrderRE> riderAcceptOrder(Authentication auth, Long orderId) {
        AcceptOrderRE re = new AcceptOrderRE();

        User rider = usersService.getAuthUser((UserDetails) auth.getPrincipal());
        Order order = findOrderById(orderId);

        if (!rider.getRiderStatus().equals(RiderStatus.FREE.toString()))
            re.addError("Rider is not free.");
        if (order == null)
            re.addError("Order with the provided ID does not exist.");
        else if (!order.getDeliveryStatus().equals(DeliveryStatus.REQUESTED.toString()))
            re.addError("Order has already been accepted by another rider.");

        if (re.getErrors().isEmpty()) {
            assert order != null;
            order.setRider(rider);
            order.setDeliveryStatus(DeliveryStatus.FETCHING.toString());
            order.setAcceptedAt(LocalDateTime.now());
            ordersRepository.save(order);
            rider.setRiderStatus(RiderStatus.FETCHING.toString());
        }

        return ResponseEntity.badRequest().body(re);
    }

    public ResponseEntity<AcceptOrderRE> riderUpdateCurrentOrderStatus(Authentication auth) {
        AcceptOrderRE re = new AcceptOrderRE();

        User rider = usersService.getAuthUser((UserDetails) auth.getPrincipal());
        Order order = findRiderOrder(rider);

        if (order == null) {
            re.addError("Rider does not have a current order.");
            return ResponseEntity.badRequest().body(re);
        }

        if (order.getDeliveryStatus().equals(DeliveryStatus.FETCHING.toString())) {
            order.setDeliveryStatus(DeliveryStatus.DELIVERING.toString());
            rider.setRiderStatus(RiderStatus.DELIVERING.toString());
        } else if (order.getDeliveryStatus().equals(DeliveryStatus.DELIVERING.toString())) {
            order.setDeliveryStatus(DeliveryStatus.DELIVERED.toString());
            rider.setRiderStatus(RiderStatus.FREE.toString());
        }

        return ResponseEntity.ok().body(re);
    }

    public ResponseEntity<RatingRE> companyRateRider(Authentication auth, Double rating, Long orderId) {
        RatingRE re = new RatingRE();
        User company = usersService.getAuthUser((UserDetails) auth.getPrincipal());

        if (!company.getCompanyStatus().equals(CompanyStatus.APPROVED.toString()))
            re.addError("Company not approved.");

        Order order = findOrderById(orderId);
        if (order == null)
            re.addError("Order with the provided ID does not exist.");
        else if (order.getRiderRating() != null)
            re.addError("Driver has already been rated for this delivery.");

        if (re.getErrors().isEmpty()) {
            re.setRatingDto(new RatingDto(rating, orderId));
            assert order != null;
            order.setRiderRating(rating);
            return ResponseEntity.ok().body(re);
        }

        return ResponseEntity.badRequest().body(re);

    }


    private Order findOrderById(Long id) {
        ResponseEntity<OrdersRE> ordersRE = managerFindOrders(id, null, null, null, null,
                null, null, null, null, null, null, null);
        return getOrdersREOrder(ordersRE);
    }

    private Order findRiderOrder(Long riderId) {
        ResponseEntity<OrdersRE> ordersRE = managerFindOrders(null, null, riderId, null, null,
                null, null, null, null, null, null, null);
        return getOrdersREOrder(ordersRE);
    }

    private Order findRiderOrder(User rider) {
        return findRiderOrder(rider.getId());
    }

    // Used when only 0 or 1 results are expected
    @Nullable
    private Order getOrdersREOrder(ResponseEntity<OrdersRE> ordersRE) {
        try {
            List<Order> orders = Objects.requireNonNull(ordersRE.getBody()).getOrders();
            assert (orders.size() <= 1);
            return orders.isEmpty() ? null : orders.get(0);
        } catch (NullPointerException e) {
            return null;
        }
    }

    private static boolean stringIsNullOrBlank(String string) {
        return Arrays.asList(null, "").contains(string);
    }

}
