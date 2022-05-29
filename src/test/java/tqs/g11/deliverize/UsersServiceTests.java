package tqs.g11.deliverize;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tqs.g11.deliverize.dto.UserDto;
import tqs.g11.deliverize.enums.UserRoles;
import tqs.g11.deliverize.model.User;
import tqs.g11.deliverize.repository.UsersRepository;
import tqs.g11.deliverize.service.UsersService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UsersServiceTests {
    @Mock(lenient = true)
    private UsersRepository repo;

    @InjectMocks
    private UsersService service;

    private User manager;
    private User rider;
    private User company;
    private List<User> users;

    private UserDto newUserDto;

    private User newUser;

    private static final String NONEXISTENT_USERNAME = "NONEXISTENT_USERNAME";

    @BeforeEach
    public void setUp() {
        manager = new User("manager1", "Manager One", "managerpassword", UserRoles.MANAGER);
        manager.setId(1L);
        rider = new User("rider1", "Rider One", "riderpassword", UserRoles.RIDER);
        company = new User("company1", "Company One", "companypassword", UserRoles.COMPANY);
        users = Arrays.asList(manager, rider, company);

        Mockito.when(repo.findByUsername("manager1")).thenReturn(manager);
        Mockito.when(repo.findByUsernameAndPassword("manager1", "managerpassword")).thenReturn(manager);
        Mockito.when(repo.findAll()).thenReturn(users);

        newUserDto = new UserDto(42L, "newuser", "New User", "newpassword", UserRoles.RIDER.toString());
        newUser = new User(newUserDto);
        Mockito.when(repo.save(newUser)).thenReturn(newUser);
    }

    @Test
    void getAllUsersTest() {
        assertThat(service.getAllUsers(), equalTo(users));
    }

    @Test
    void loadUserByUsernameTestNotExists() {
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(NONEXISTENT_USERNAME));
    }

    @Test
    void usernameAvailableTestFalse() {
        assertThat(service.usernameAvailable(manager.getUsername()), equalTo(false));
    }

    @Test
    void usernameAvailableTestTrue() {
        assertThat(service.usernameAvailable(NONEXISTENT_USERNAME), equalTo(true));
    }

}
