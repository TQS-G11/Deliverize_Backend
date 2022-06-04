package tqs.g11.deliverize;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tqs.g11.deliverize.dto.*;
import tqs.g11.deliverize.enums.*;
import tqs.g11.deliverize.model.Order;
import tqs.g11.deliverize.model.User;
import tqs.g11.deliverize.repository.OrdersRepository;
import tqs.g11.deliverize.service.OrdersService;
import tqs.g11.deliverize.service.UsersService;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTests {
    @Mock(lenient = true)
    private OrdersRepository ordersRepository;

    @InjectMocks
    private OrdersService ordersService;

    @Mock(lenient = true)
    private UsersService usersService;

    private User company;

    private User rider;

    private User rider2;

    private Order order1;

    private Order order2;

    private Order orderFetching;

    private List<Order> orders;

    @BeforeEach
    void setUp() {
        company = new User("zap", "Zap", "companypassword", UserRoles.COMPANY);
        company.setId(1L);
        rider = new User("babydweet", "Dwight Fairfield", "riderpassword", UserRoles.RIDER);
        rider.setId(2L);
        rider2 = new User("Blendette", "Claudette Morel", "riderpassword", UserRoles.RIDER);
        rider2.setId(3L);

        OrderDto orderDto = new OrderDto();
        orderDto.setCompany(company);
        orderDto.setBuyer("Walter White");
        orderDto.setDestination("308 Negra Aroya Lane, Albuquerque, New Mexico, 87104");
        orderDto.setNotes("I am the one who knocks.");
        orderDto.setOrigin("Some Zap store");
        orderDto.setStoreLat(.0);
        orderDto.setStoreLon(.0);
        order1 = new Order(orderDto);
        order1.setId(1L);

        orderDto = new OrderDto();
        orderDto.setCompany(company);
        orderDto.setBuyer("Mike Ehrmantraut");
        orderDto.setDestination("204 Edith Blvd. NE, Albuquerque, New Mexico 87102");
        orderDto.setNotes("");
        orderDto.setOrigin("Some Zap store");
        orderDto.setStoreLat(.0);
        orderDto.setStoreLon(.0);
        order2 = new Order(orderDto);
        order2.setId(2L);

        orders = Arrays.asList(order1, order2);

        Arrays.asList(company, rider, rider2)
                .forEach(usr -> when(usersService.getUserById(usr.getId())).thenReturn(Optional.of(usr)));

        when(ordersRepository.findOrders(null, null, null, null, null, null,
                null, null, null, null, null, null))
                .thenReturn(orders);

        when(ordersRepository.findOrders(null, company, null, null, null, null,
                DeliveryStatus.REQUESTED.toString(), "Some Zap store", 5.0, null, null,
                null))
                .thenReturn(orders);

        when(ordersRepository.findOrders(order1.getId(), null, null, null, null, null,
                null, null, null, null, null, null))
                .thenReturn(List.of(order1));
    }


    @Test
    void testManagerGetOrdersNoFilters() {
        ResponseEntity<OrdersRE> re = ordersService.managerFindOrders(
                null, null, null, null, null, null, null,
                null, null, null, null, null
        );
        assertThat(re.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(Objects.requireNonNull(re.getBody()).getOrders(), equalTo(orders));
        assertThat(re.getBody().getErrors().isEmpty(), equalTo(true));
    }

    @Test
    void testManagerGetOrdersFilters() {
        // Filter by order1 ID
        ResponseEntity<OrdersRE> re = ordersService.managerFindOrders(order1.getId(), null, null,
                null, null, null, null, null, null, null,
                null, null
        );
        assertThat(re.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(Objects.requireNonNull(re.getBody()).getOrders(), equalTo(List.of(order1)));
        assertThat(re.getBody().getErrors().isEmpty(), equalTo(true));

        // Filter by multiple attributes, common to both orders
        re = ordersService.managerFindOrders(null, company.getId(), null, null, null, null,
                DeliveryStatus.REQUESTED.toString(), "Some Zap store", 5.0, null, null,
                null);
        assertThat(re.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(Objects.requireNonNull(re.getBody()).getOrders(), equalTo(orders));
        assertThat(re.getBody().getErrors().isEmpty(), equalTo(true));

        // Invalid companyId and riderId (rider ID used as companyId, company ID used as riderId)
        re = ordersService.managerFindOrders(null, rider.getId(), company.getId(), null, null, null,
                null, null, null, null, null, null);
        assertThat(re.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(Objects.requireNonNull(re.getBody()).getOrders(), equalTo(List.of()));
        assertThat(re.getBody().getErrors(),
                equalTo(List.of(ErrorMsg.COMPANY_ID_NOT_FOUND.toString(), ErrorMsg.RIDER_ID_NOT_FOUND.toString())));
    }


    @Test
    void testCompanyCreateOrderApprovedAndValid() {
        setUpCreateOrderTest();

        final String destination = "Kaer Morhen";
        final String buyer = "Geralt of Rivia";
        final String origin = "Zap Novigrad";

        company.setCompanyStatus(CompanyStatus.APPROVED.toString());

        OrderDto orderDto = new OrderDto();
        orderDto.setBuyer(buyer);
        orderDto.setDestination(destination);
        orderDto.setOrigin(origin);

        Authentication auth = setUpUserMockAuth(company);
        ResponseEntity<CreateOrderRE> re = ordersService.companyCreateOrder(auth, orderDto);

        assertThat(re.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(Objects.requireNonNull(re.getBody()).getErrors().isEmpty(), equalTo(true));
    }

    @Test
    void testCompanyCreateOrderNotApprovedAndInvalid() {
        setUpCreateOrderTest();

        OrderDto orderDto = new OrderDto();
        orderDto.setDestination("");

        Authentication auth = setUpUserMockAuth(company);
        ResponseEntity<CreateOrderRE> re = ordersService.companyCreateOrder(auth, orderDto);

        assertThat(re.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(Objects.requireNonNull(re.getBody()).getErrors().containsAll(Arrays.asList(
                ErrorMsg.COMPANY_NOT_APPROVED.toString(),
                ErrorMsg.BUYER_NULL_OR_BLANK.toString(),
                ErrorMsg.DESTINATION_NULL_OR_BLANK.toString(),
                ErrorMsg.ORIGIN_NULL_OR_BLANK.toString()
        )), equalTo(true));
    }

    @Test
    void testRiderAcceptOrderValid() {
        Authentication auth = setUpUserMockAuth(rider);
        setUpAcceptOrderTest(order1, rider);

        ResponseEntity<AcceptOrderRE> re = ordersService.riderAcceptOrder(auth, order1.getId());

        assertThat(re.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(Objects.requireNonNull(re.getBody()).getErrors().isEmpty(), equalTo(true));
        OrderDto orderDto = re.getBody().getOrderDto();
        assertThat(orderDto.getRider(), equalTo(rider));
        assertThat(orderDto.getDeliveryStatus(), equalTo(DeliveryStatus.FETCHING.toString()));
        assertThat(orderDto.getAcceptedAt(), notNullValue());
    }

    @Test
    void testRiderAcceptOrderInvalidOrderAlreadyAccepted() {
        rider.setRiderStatus(RiderStatus.FETCHING.toString());
        Authentication auth = setUpUserMockAuth(rider);
        order1.setDeliveryStatus(DeliveryStatus.FETCHING.toString());

        ResponseEntity<AcceptOrderRE> re = ordersService.riderAcceptOrder(auth, order1.getId());

        assertThat(re.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(Objects.requireNonNull(re.getBody()).getErrors().containsAll(Arrays.asList(
                ErrorMsg.RIDER_NOT_FREE.toString(),
                ErrorMsg.ORDER_ALREADY_ACCEPTED.toString()
        )), equalTo(true));
    }

    @Test
    void testRiderAcceptOrderInvalidOrderNotFound() {
        Authentication auth = setUpUserMockAuth(rider);

        ResponseEntity<AcceptOrderRE> re = ordersService.riderAcceptOrder(auth, -1L);

        assertThat(re.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(Objects.requireNonNull(re.getBody()).getErrors().contains(
                ErrorMsg.ORDER_ID_NOT_FOUND.toString()
        ), equalTo(true));
    }

    @Test
    void testRiderUpdateCurrentOrderStatusFetching() {
        order1.setDeliveryStatus(DeliveryStatus.FETCHING.toString());
        order1.setRider(rider);
        rider.setRiderStatus(RiderStatus.FETCHING.toString());

        when(ordersRepository.getOrdersByRiderEqualsAndDeliveryStatusIn(rider, Arrays.asList(
                DeliveryStatus.FETCHING.toString(), DeliveryStatus.DELIVERING.toString()
        ))).thenReturn(List.of(order1));

        Authentication auth = setUpUserMockAuth(rider);

        ResponseEntity<AcceptOrderRE> re = ordersService.riderUpdateCurrentOrderStatus(auth);

        assertThat(re.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(Objects.requireNonNull(re.getBody()).getErrors().isEmpty(), equalTo(true));
        assertThat(order1.getDeliveryStatus(), equalTo(DeliveryStatus.DELIVERING.toString()));
        assertThat(rider.getRiderStatus(), equalTo(RiderStatus.DELIVERING.toString()));
    }

    @Test
    void testRiderUpdateCurrentOrderStatusDelivering() {
        order1.setDeliveryStatus(DeliveryStatus.DELIVERING.toString());
        order1.setRider(rider);
        rider.setRiderStatus(RiderStatus.DELIVERING.toString());

        when(ordersRepository.getOrdersByRiderEqualsAndDeliveryStatusIn(rider, Arrays.asList(
                DeliveryStatus.FETCHING.toString(), DeliveryStatus.DELIVERING.toString()
        ))).thenReturn(List.of(order1));

        Authentication auth = setUpUserMockAuth(rider);

        ResponseEntity<AcceptOrderRE> re = ordersService.riderUpdateCurrentOrderStatus(auth);

        assertThat(re.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(Objects.requireNonNull(re.getBody()).getErrors().isEmpty(), equalTo(true));
        assertThat(order1.getDeliveryStatus(), equalTo(DeliveryStatus.DELIVERED.toString()));
        assertThat(rider.getRiderStatus(), equalTo(RiderStatus.FREE.toString()));
    }

    @Test
    void testRiderUpdateCurrentOrderInvalid() { // No current order
        rider.setRiderStatus(RiderStatus.DELIVERING.toString());

        when(ordersRepository.getOrdersByRiderEqualsAndDeliveryStatusIn(rider, Arrays.asList(
                DeliveryStatus.FETCHING.toString(), DeliveryStatus.DELIVERING.toString()
        ))).thenReturn(new ArrayList<>());

        Authentication auth = setUpUserMockAuth(rider);

        ResponseEntity<AcceptOrderRE> re = ordersService.riderUpdateCurrentOrderStatus(auth);

        assertThat(re.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(Objects.requireNonNull(re.getBody()).getErrors().contains(
                ErrorMsg.RIDER_NO_CURRENT_ORDER.toString()
        ), equalTo(true));
    }

    @Test
    void testCompanyRateRiderValid() {
        Double rating = 4.0;

        company.setCompanyStatus(CompanyStatus.APPROVED.toString());
        rider.setRatingCount(1);
        rider.setRiderRating(2.0);
        order1.setRider(rider);
        order1.setDeliveryStatus(DeliveryStatus.DELIVERED.toString());
        Authentication auth = setUpUserMockAuth(company);

        ResponseEntity<RatingRE> re = ordersService.companyRateRider(auth, rating, order1.getId());

        assertThat(re.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(Objects.requireNonNull(re.getBody()).getErrors().isEmpty(), equalTo(true));
        assertThat(re.getBody().getRatingDto().getRating(), equalTo(rating));
        assertThat(rider.getRiderRating(), equalTo(3.0)); // 3: Average of 2 and 4
        assertThat(rider.getRatingCount(), equalTo(2));
    }

    @Test
    void testCompanyRateRiderInvalid() {
        Double rating = 5.1;

        order1.setRider(rider);
        order1.setDeliveryStatus(DeliveryStatus.DELIVERED.toString());
        order1.setRiderRating(3.0);
        Authentication auth = setUpUserMockAuth(company);

        ResponseEntity<RatingRE> re = ordersService.companyRateRider(auth, rating, order1.getId());

        assertThat(re.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(Objects.requireNonNull(re.getBody()).getErrors().containsAll(Arrays.asList(
                ErrorMsg.INVALID_RATING.toString(),
                ErrorMsg.COMPANY_NOT_APPROVED.toString(),
                ErrorMsg.DRIVER_ALREADY_RATED_FOR_DELIVERY.toString())
        ), equalTo(true));
    }

    private Authentication setUpUserMockAuth(User user) {
        Authentication auth = mock(Authentication.class);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities
        );
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(usersService.getAuthUser(userDetails)).thenReturn(user);
        return auth;
    }

    private void setUpCreateOrderTest() {
        when(ordersRepository.save(any())).thenReturn(
                new Order(new OrderDto(
                        3L,
                        company,
                        null,
                        "buyer",
                        "destination",
                        "notes",
                        DeliveryStatus.REQUESTED.toString(),
                        "origin",
                        5.0,
                        LocalDateTime.now(),
                        null,
                        null,
                        null,
                        null,
                        .0,
                        .0,
                        null
                ))
        );
    }

    private void setUpAcceptOrderTest(Order order, User rider) {
        OrderDto dto = new OrderDto(order);
        dto.setRider(rider);
        dto.setDeliveryStatus(DeliveryStatus.FETCHING.toString());
        dto.setAcceptedAt(LocalDateTime.now());
        when(ordersRepository.save(any())).thenReturn(new Order(dto));
    }
}
