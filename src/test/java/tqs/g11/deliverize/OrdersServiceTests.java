package tqs.g11.deliverize;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tqs.g11.deliverize.dto.OrderDto;
import tqs.g11.deliverize.dto.OrdersRE;
import tqs.g11.deliverize.enums.DeliveryStatus;
import tqs.g11.deliverize.enums.UserRoles;
import tqs.g11.deliverize.model.Order;
import tqs.g11.deliverize.model.User;
import tqs.g11.deliverize.repository.OrdersRepository;
import tqs.g11.deliverize.service.OrdersService;
import tqs.g11.deliverize.service.UsersService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
        ResponseEntity<OrdersRE> re = ordersService.managerFindOrders(order1.getId(), null, null,
                null, null, null, null, null, null, null,
                null, null
        );
        assertThat(re.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(Objects.requireNonNull(re.getBody()).getOrders(), equalTo(List.of(order1)));
        assertThat(re.getBody().getErrors().isEmpty(), equalTo(true));

        re = ordersService.managerFindOrders(null, company.getId(), null, null, null, null,
                DeliveryStatus.REQUESTED.toString(), "Some Zap store", 5.0, null, null,
                null);
        assertThat(re.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(Objects.requireNonNull(re.getBody()).getOrders(), equalTo(orders));
        assertThat(re.getBody().getErrors().isEmpty(), equalTo(true));
    }
}
