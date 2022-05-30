package tqs.g11.deliverize.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tqs.g11.deliverize.dto.CreateOrderRE;
import tqs.g11.deliverize.dto.OrderDto;
import tqs.g11.deliverize.dto.OrdersRE;
import tqs.g11.deliverize.enums.CompanyStatus;
import tqs.g11.deliverize.enums.UserRoles;
import tqs.g11.deliverize.model.Order;
import tqs.g11.deliverize.model.User;
import tqs.g11.deliverize.repository.OrdersRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrdersService {
    private final OrdersRepository ordersRepository;

    private final UsersService usersService;

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
        OrdersRE re = new OrdersRE();

        Optional<User> company = usersService.getUserById(companyId);
        if (companyId != null && (company.isEmpty() || !company.get().getRole().equals(UserRoles.COMPANY.toString())))
            re.addError("Company with provided ID not found.");
        Optional<User> rider = usersService.getUserById(riderId);
        if (riderId != null && (rider.isEmpty() || !rider.get().getRole().equals(UserRoles.RIDER.toString())))
            re.addError("Rider with provided ID not found.");

        if (re.getErrors().isEmpty()) {
            re.setOrders(ordersRepository.findOrders(id, company.get(), rider.get(), buyer, destination, notes,
                    deliveryStatus, origin, price, requestedAt, acceptedAt, deliveredAt));
            return ResponseEntity.ok().body(re);
        }

        return ResponseEntity.badRequest().body(re);
    }

    public ResponseEntity<CreateOrderRE> createOrder(Authentication auth, OrderDto dto) {
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

    private static boolean stringIsNullOrBlank(String string) {
        return Arrays.asList(null, "").contains(string);
    }

    public OrdersService(OrdersRepository ordersRepository, UsersService usersService) {
        this.ordersRepository = ordersRepository;
        this.usersService = usersService;
    }
}
