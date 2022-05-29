package tqs.g11.deliverize;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import tqs.g11.deliverize.dto.SignupRE;
import tqs.g11.deliverize.dto.UserDto;
import tqs.g11.deliverize.enums.UserRoles;
import tqs.g11.deliverize.model.User;
import tqs.g11.deliverize.repository.UsersRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "application-integrationtest.properties")
class UsersControllerIntegrationTests {
    @LocalServerPort
    int randomServerPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsersRepository repo;

    private final UserDto dto = new UserDto(42L, "newuser", "New User", "newpassword", UserRoles.RIDER.toString());

    @AfterEach
    public void resetDb() {
        repo.deleteAll();
    }

    @Test
    void signupTestValid() {
        ResponseEntity<SignupRE> re = restTemplate.postForEntity("/api/users/signup", dto, SignupRE.class);
        assertThat(re.getStatusCode(), equalTo(HttpStatus.OK));
        List<User> found = repo.findAll();
        Assertions.assertThat(found).extracting(User::getUsername).containsOnly(dto.getUsername());
    }
}
