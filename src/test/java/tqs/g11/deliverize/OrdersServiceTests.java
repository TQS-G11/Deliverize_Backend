package tqs.g11.deliverize;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import tqs.g11.deliverize.dto.OrderDto;
import tqs.g11.deliverize.dto.OrdersRE;
import tqs.g11.deliverize.enums.UserRoles;
import tqs.g11.deliverize.model.Order;
import tqs.g11.deliverize.model.User;
import tqs.g11.deliverize.repository.OrdersRepository;
import tqs.g11.deliverize.service.OrdersService;
import tqs.g11.deliverize.service.UsersService;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTests {
    @Mock(lenient = true)
    private OrdersRepository ordersRepository;

    @InjectMocks
    private OrdersService ordersService;

    @Mock
    private UsersService usersService;

    private User company;

    private User rider;

    private Order order;

    private List<Order> orders;

    @BeforeEach
    void setUp() {
        company = new User("zap", "Zap", "companypassword", UserRoles.COMPANY);
        rider = new User("babydweet", "Dwight Fairfield", "riderpassword", UserRoles.RIDER);
        OrderDto orderDto = new OrderDto();
        orderDto.setCompany(company);
        orderDto.setBuyer("Walter White");
        orderDto.setDestination("308 Negra Aroya Lane, Albuquerque, New Mexico, 87104");
        orderDto.setNotes("I am the one who knocks.");
        orderDto.setOrigin("Some Zap store");
        orderDto.setStoreLat(.0);
        orderDto.setStoreLon(.0);
        order = new Order(orderDto);
        orders = List.of(order);

        when(ordersRepository.findOrders(null, null, null, null, null, null,
                null, null, null, null, null, null))
                .thenReturn(orders);
    }


    @Test
    void testManagerGetOrdersAsManager() {
        ResponseEntity<OrdersRE> re = ordersService.managerFindOrders(
                null, null, null, null, null, null, null,
                null, null, null, null, null
        );
        assertThat(re.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(Objects.requireNonNull(re.getBody()).getOrders(), equalTo(orders));
        assertThat(re.getBody().getErrors().isEmpty(), equalTo(true));
    }
}
