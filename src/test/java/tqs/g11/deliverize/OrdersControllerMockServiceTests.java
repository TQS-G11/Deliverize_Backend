package tqs.g11.deliverize;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import tqs.g11.deliverize.auth.TokenProvider;
import tqs.g11.deliverize.auth.UnauthorizedEntryPoint;
import tqs.g11.deliverize.controller.OrdersController;
import tqs.g11.deliverize.dto.CreateOrderRE;
import tqs.g11.deliverize.dto.OrderDto;
import tqs.g11.deliverize.dto.OrdersRE;
import tqs.g11.deliverize.model.Order;
import tqs.g11.deliverize.service.OrdersService;
import tqs.g11.deliverize.service.UsersService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdersController.class)
class OrdersControllerMockServiceTests {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @MockBean
    private OrdersService ordersService;

    @MockBean
    private UsersService usersService;

    @MockBean
    private UnauthorizedEntryPoint unauthorizedEntryPoint;

    @MockBean
    private TokenProvider tokenProvider;

    private Order order1;

    private Order order2;

    private List<Order> orders;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        orders = Arrays.asList(order1, order2);
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        when(ordersService.managerFindOrders(null, null, null, null, null, null,
                null, null, null, null, null, null))
                .thenReturn(ResponseEntity.ok().body(new OrdersRE(new ArrayList<>(), orders)));
        when(ordersService.companyCreateOrder(any(), any())).thenReturn(ResponseEntity.ok().body(new CreateOrderRE()));
    }

    @SneakyThrows
    @Test
    @WithAnonymousUser
    void testFindOrdersUnauthenticated() {
        mvc.perform(get("/api/deliveries").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @SneakyThrows
    @Test
    @WithMockUser(roles = "RIDER")
    void testFindOrdersForbidden() {
        mvc.perform(get("/api/deliveries").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @SneakyThrows
    @Test
    @WithMockUser(roles = "MANAGER")
    void testFindOrdersOk() {
        mvc.perform(get("/api/deliveries").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    @WithMockUser(roles = "RIDER")
    void testCreateOrderForbidden() {
        String content = objectMapper.writeValueAsString(new OrderDto());
        mvc.perform(post("/api/deliveries/company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isForbidden());
    }

    @SneakyThrows
    @Test
    @WithMockUser(roles = "COMPANY")
    void testCreateOrderOk() {
        String content = objectMapper.writeValueAsString(new OrderDto());
        mvc.perform(post("/api/deliveries/company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());
    }
}
