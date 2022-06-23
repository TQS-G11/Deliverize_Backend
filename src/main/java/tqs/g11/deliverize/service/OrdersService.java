package tqs.g11.deliverize.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tqs.g11.deliverize.dto.*;
import tqs.g11.deliverize.enums.*;
import tqs.g11.deliverize.model.Order;
import tqs.g11.deliverize.model.User;
import tqs.g11.deliverize.repository.OrdersRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrdersService {
    private static final Double RIDER_MIN_RATING = .0;
    private static final Double RIDER_MAX_RATING = 5.0;

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
            re.addError(ErrorMsg.COMPANY_ID_NOT_FOUND.toString());
        Optional<User> riderOpt = usersService.getUserById(riderId);
        if (riderId != null && (riderOpt.isEmpty() || !riderOpt.get().getRole().equals(UserRoles.RIDER.toString())))
            re.addError(ErrorMsg.RIDER_ID_NOT_FOUND.toString());

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
            re.addError(ErrorMsg.COMPANY_NOT_APPROVED.toString());
        if (stringIsNullOrBlank(dto.getBuyer()))
            re.addError(ErrorMsg.BUYER_NULL_OR_BLANK.toString());
        if (stringIsNullOrBlank(dto.getDestination()))
            re.addError(ErrorMsg.DESTINATION_NULL_OR_BLANK.toString());
        if (dto.getNotes() == null)
            dto.setNotes("");
        if (stringIsNullOrBlank(dto.getOrigin()))
            re.addError(ErrorMsg.ORIGIN_NULL_OR_BLANK.toString());

        if (re.getErrors().isEmpty()) {
            Order order = ordersRepository.save(new Order(dto));
            re.setOrderDto(new OrderDto(order));
            return ResponseEntity.status(HttpStatus.CREATED).body(re);
        }

        return ResponseEntity.badRequest().body(re);
    }


    public ResponseEntity<AcceptOrderRE> riderAcceptOrder(Authentication auth, Long orderId) {
        AcceptOrderRE re = new AcceptOrderRE();

        User rider = usersService.getAuthUser((UserDetails) auth.getPrincipal());
        Order order = findOrderById(orderId);

        if (!rider.getRiderStatus().equals(RiderStatus.FREE.toString()))
            re.addError(ErrorMsg.RIDER_NOT_FREE.toString());
        if (order == null)
            re.addError(ErrorMsg.ORDER_ID_NOT_FOUND.toString());
        else if (!order.getDeliveryStatus().equals(DeliveryStatus.REQUESTED.toString()))
            re.addError(ErrorMsg.ORDER_ALREADY_ACCEPTED.toString());

        if (re.getErrors().isEmpty()) {
            assert order != null;
            order.setRider(rider);
            order.setDeliveryStatus(DeliveryStatus.FETCHING.toString());
            order.setAcceptedAt(LocalDateTime.now());
            ordersRepository.save(order);
            usersService.updateRiderStatus(rider);
            re.setOrderDto(new OrderDto(order));
            return ResponseEntity.status(HttpStatus.CREATED).body(re);
        }

        return ResponseEntity.badRequest().body(re);
    }


    public ResponseEntity<AcceptOrderRE> riderUpdateCurrentOrderStatus(Authentication auth) {
        AcceptOrderRE re = new AcceptOrderRE();

        User rider = usersService.getAuthUser((UserDetails) auth.getPrincipal());
        Order order = findRiderOrder(rider);

        if (order == null) {
            re.addError(ErrorMsg.RIDER_NO_CURRENT_ORDER.toString());
            return ResponseEntity.badRequest().body(re);
        }

        if (order.getDeliveryStatus().equals(DeliveryStatus.FETCHING.toString())) {
            order.setDeliveryStatus(DeliveryStatus.DELIVERING.toString());
        } else if (order.getDeliveryStatus().equals(DeliveryStatus.DELIVERING.toString())) {
            order.setDeliveryStatus(DeliveryStatus.DELIVERED.toString());
            order.setDeliveredAt(LocalDateTime.now());
        }
        usersService.updateRiderStatus(rider);

        ordersRepository.save(order);

        re.setOrderDto(new OrderDto(order));

        return ResponseEntity.status(HttpStatus.CREATED).body(re);
    }


    public ResponseEntity<RatingRE> companyRateRider(Authentication auth, Double rating, Long orderId) {
        RatingRE re = new RatingRE();
        User company = usersService.getAuthUser((UserDetails) auth.getPrincipal());

        if (rating < RIDER_MIN_RATING || rating > RIDER_MAX_RATING)
            re.addError(ErrorMsg.INVALID_RATING.toString());

        if (!company.getCompanyStatus().equals(CompanyStatus.APPROVED.toString()))
            re.addError(ErrorMsg.COMPANY_NOT_APPROVED.toString());

        Order order = findOrderById(orderId);
        if (order == null)
            re.addError(ErrorMsg.ORDER_ID_NOT_FOUND.toString());
        else if (order.getRiderRating() != null)
            re.addError(ErrorMsg.DRIVER_ALREADY_RATED_FOR_DELIVERY.toString());

        if (re.getErrors().isEmpty()) {
            re.setRatingDto(new RatingDto(rating, orderId));
            assert order != null;
            order.setRiderRating(rating);
            order.getRider().addRiderRating(rating);
            return ResponseEntity.status(HttpStatus.CREATED).body(re);
        }

        return ResponseEntity.badRequest().body(re);
    }

    public ResponseEntity<OrderRE> getOrderById(Long id) {
        OrderRE re = new OrderRE();
        Optional<Order> orderOptional = ordersRepository.findById(id);
        if (orderOptional.isEmpty()) {
            re.addError(ErrorMsg.ORDER_ID_NOT_FOUND.toString());
        }

        if (!re.hasErrors()) {
            re.setOrder(orderOptional.get());
            return ResponseEntity.status(HttpStatus.OK).body(re);
        }

        return ResponseEntity.badRequest().body(re);

    }

    public ResponseEntity<OrdersRE> getOrdersByBuyer(Authentication auth, String buyer) {
        User company = usersService.getAuthUser((UserDetails) auth.getPrincipal());

        return managerFindOrders(null, company.getId(), null, buyer, null,
                null, null, null, null, null, null, null);
    }

    private Order findOrderById(Long id) {
        ResponseEntity<OrdersRE> ordersRE = managerFindOrders(id, null, null, null, null,
                null, null, null, null, null, null, null);
        return getOrdersREOrder(ordersRE);
    }

    private Order findRiderOrder(User rider) {
        List<Order> undeliveredOrders = getRiderUndeliveredOrders(rider);
        assert undeliveredOrders.size() <= 1;
        return undeliveredOrders.isEmpty() ? null : undeliveredOrders.get(0);
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

    private List<Order> getRiderUndeliveredOrders(User rider) {
        return ordersRepository.getOrdersByRiderEqualsAndDeliveryStatusIn(rider, Arrays.asList(
                DeliveryStatus.FETCHING.toString(), DeliveryStatus.DELIVERING.toString()
        ));
    }
}
