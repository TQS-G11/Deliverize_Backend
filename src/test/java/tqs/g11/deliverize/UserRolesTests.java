package tqs.g11.deliverize;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tqs.g11.deliverize.enums.UserRoles;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class UserRolesTests {
    @DisplayName("validRole method: valid roles")
    @Test
    void validRoleTestValid() {
        List<String> rolesNames = Stream.of(UserRoles.values())
                .map(Enum::toString)
                .collect(Collectors.toList());
        rolesNames.forEach(roleName -> assertThat(UserRoles.validRole(roleName), is(true)));
    }

    @DisplayName("validRole method: invalid role")
    @Test
    void validRoleTestInvalid() {
        assertThat(UserRoles.validRole("IMPOSTOR"), is(false));
    }
}
