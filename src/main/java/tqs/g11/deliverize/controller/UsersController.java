package tqs.g11.deliverize.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tqs.g11.deliverize.auth.TokenProvider;
import tqs.g11.deliverize.dto.*;
import tqs.g11.deliverize.enums.UserRoles;
import tqs.g11.deliverize.model.User;
import tqs.g11.deliverize.service.UsersService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/users")
public class UsersController {
    private final UsersService usersService;

    private final AuthenticationManager authManager;

    private final TokenProvider jwtTokenUtil;

    @PostMapping("signup")
    public ResponseEntity<SignupRE> signup(@RequestBody UserDto userDto) {
        SignupRE re = new SignupRE();
        if (userDto.getUsername().isEmpty())
            re.addError("Username field cannot be blank.");
        if (userDto.getName().isEmpty())
            re.addError("Name field cannot be blank.");
        if (userDto.getPassword().isEmpty())
            re.addError("Password field cannot be blank.");
        if (userDto.getPassword().length() < 8)
            re.addError("Password must be at least 8 characters long.");
        if (!usersService.usernameAvailable(userDto.getUsername()))
            re.addError("A user with the provided username already exists.");
        if (!UserRoles.validRole(userDto.getRole()))
            re.addError("Invalid role.");
        if (re.getErrors().isEmpty()) {
            re.setUserDto(new UserDto(usersService.createUser(userDto)));
            return ResponseEntity.status(HttpStatus.CREATED).body(re);
        } else
            return ResponseEntity.badRequest().body(re);
    }

    @PostMapping("login")
    public ResponseEntity<LoginRE> login(@RequestBody LoginUser loginUser) {
        LoginRE re = new LoginRE();
        if (loginUser.getUsername().isEmpty())
            re.addError("Username field cannot be blank.");
        if (loginUser.getPassword().isEmpty())
            re.addError("Password field cannot be blank.");

        try {
            final Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            final String token = jwtTokenUtil.generateToken(authentication);
            re.setToken(new AuthToken(token));
            return ResponseEntity.ok().body(re);
        } catch (AuthenticationException e) {
            re.addError("Invalid credentials.");
            return ResponseEntity.badRequest().body(re);
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping("change-company-status")
    public ResponseEntity<ChangeCompanyStatusRE> managerChangeCompanyStatus(@RequestBody UserDto companyDto) {
        return usersService.managerChangeCompanyStatus(companyDto);
    }

    public UsersController(UsersService usersService, AuthenticationManager authManager, TokenProvider jwtTokenUtil) {
        this.usersService = usersService;
        this.authManager = authManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("")
    public List<User> findUsersByRole(String role) {
        return usersService.findUsersByRole(role);
    }
}
