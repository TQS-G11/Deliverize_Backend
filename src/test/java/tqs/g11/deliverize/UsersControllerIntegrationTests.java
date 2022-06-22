package tqs.g11.deliverize;


import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import tqs.g11.deliverize.dto.*;
import tqs.g11.deliverize.enums.CompanyStatus;
import tqs.g11.deliverize.enums.UserRoles;
import tqs.g11.deliverize.model.User;
import tqs.g11.deliverize.repository.UsersRepository;
import tqs.g11.deliverize.service.UsersService;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsersControllerIntegrationTests {
    private static final String AUTH_HEADER_PATTERN = "Bearer {0}";

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UsersService usersService;

    private User company1 = new User("company1", "Company 1", "company1password", UserRoles.COMPANY);

    private User manager1 = new User("manager1", "Manager 1", "manager1password", UserRoles.MANAGER);

    private User rider1 = new User("rider1", "Rider 1", "rider1password", UserRoles.RIDER);

    {
        company1.setId(1L);
        manager1.setId(2L);
        rider1.setId(3L);
    }

    @Test
    @Order(1)
    void signUpTestValid() {
        UserDto newUser = new UserDto(manager1);
        ResponseEntity<SignupRE> response = restTemplate.postForEntity("/api/users/signup", newUser, SignupRE.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(usersRepository.findAll()).extracting(User::getUsername).contains(manager1.getUsername());

        newUser = new UserDto(company1);
        response = restTemplate.postForEntity("/api/users/signup", newUser, SignupRE.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(usersRepository.findAll()).extracting(User::getUsername).contains(company1.getUsername());

        newUser = new UserDto(rider1);
        response = restTemplate.postForEntity("/api/users/signup", newUser, SignupRE.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(usersRepository.findAll()).extracting(User::getUsername).contains(rider1.getUsername());
    }

    @Test
    @Order(2)
    void signUpTestInvalid() {
        UserDto newUser = new UserDto(manager1); // Already exists
        newUser.setName(""); // Blank name
        newUser.setPassword("1234567"); // Password shorter than 8 characters
        newUser.setRole("invalid"); // Invalid role
        ResponseEntity<SignupRE> response = restTemplate.postForEntity("/api/users/signup", newUser, SignupRE.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(usersRepository.findAll()).extracting(User::getUsername).contains(manager1.getUsername());

        newUser = new UserDto();  // Everything blank
        newUser.setUsername("");
        newUser.setName("");
        newUser.setPassword("");
        newUser.setRole("");
        response = restTemplate.postForEntity("/api/users/signup", newUser, SignupRE.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(response.getBody()).getErrors()).hasSize(5);
    }

    @Test
    @Order(3)
    void loginTestValid() {
        LoginUser manager1lu = new LoginUser(manager1.getUsername(), manager1.getPassword());
        ResponseEntity<LoginRE> response = restTemplate.postForEntity("/api/users/login", manager1lu, LoginRE.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getToken()).isNotNull();
    }

    @Test
    @Order(4)
    void loginTestInvalid() {
        LoginUser manager1lu = new LoginUser(manager1.getUsername(), "");
        ResponseEntity<LoginRE> response = restTemplate.postForEntity("/api/users/login", manager1lu, LoginRE.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(5)
    void managerChangeCompanyStatusTestValid() {
        UserDto companyDto = new UserDto(company1);
        companyDto.setCompanyStatus(CompanyStatus.APPROVED.toString());

        HttpEntity<UserDto> entity = new HttpEntity<>(companyDto, getUserHeadersWithAuth(manager1));

        ResponseEntity<ChangeCompanyStatusRE> response = restTemplate.postForEntity(
                "/api/users/change-company-status",
                entity,
                ChangeCompanyStatusRE.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @Order(6)
    void managerChangeCompanyStatusTestInvalid401() {
        // No authentication, 401 Unauthorized means unauthenticated :)
        UserDto companyDto = new UserDto(company1);
        companyDto.setCompanyStatus(CompanyStatus.APPROVED.toString());
        HttpEntity<UserDto> entity = new HttpEntity<>(companyDto, new HttpHeaders());
        ResponseEntity<ChangeCompanyStatusRE> response = restTemplate.postForEntity(
                "/api/users/change-company-status",
                entity,
                ChangeCompanyStatusRE.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @Order(7)
    void managerChangeCompanyStatusTestInvalid403() {
        // Authenticated as a non-manager, leading to 403 Forbidden
        UserDto companyDto = new UserDto(company1);
        companyDto.setCompanyStatus(CompanyStatus.APPROVED.toString());
        HttpEntity<UserDto> entity = new HttpEntity<>(companyDto, getUserHeadersWithAuth(rider1));
        ResponseEntity<ChangeCompanyStatusRE> response = restTemplate.postForEntity(
                "/api/users/change-company-status",
                entity,
                ChangeCompanyStatusRE.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(8)
    void managerFindUsersByRoleTestValid() {
        HttpEntity<UserDto> entity = new HttpEntity<>(null, getUserHeadersWithAuth(manager1));
        ResponseEntity<List<User>> response = restTemplate.exchange(
                "/api/users?role={role}",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                },
                UserRoles.RIDER.toString()
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()).containsExactly(rider1);
    }

    @Test
    @Order(9)
    void managerGetUserByIdValid() {
        HttpEntity<UserDto> entity = new HttpEntity<>(null, getUserHeadersWithAuth(manager1));
        ResponseEntity<User> response = restTemplate.exchange(
                "/api/users/{id}",
                HttpMethod.GET,
                entity,
                User.class,
                company1.getId()
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private String getUserAuthToken(User user) {
        LoginUser loginUser = new LoginUser(user.getUsername(), user.getPassword());
        ResponseEntity<LoginRE> response = restTemplate.postForEntity("/api/users/login", loginUser, LoginRE.class);
        assert response.getStatusCode().equals(HttpStatus.OK);
        return Objects.requireNonNull(response.getBody()).getToken().getToken();
    }

    private HttpHeaders getUserHeadersWithAuth(User user) {
        String token = getUserAuthToken(user);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", MessageFormat.format(AUTH_HEADER_PATTERN, token));
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}