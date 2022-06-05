package tqs.g11.deliverize.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tqs.g11.deliverize.dto.ChangeCompanyStatusRE;
import tqs.g11.deliverize.dto.UserDto;
import tqs.g11.deliverize.enums.CompanyStatus;
import tqs.g11.deliverize.enums.ErrorMsg;
import tqs.g11.deliverize.model.User;
import tqs.g11.deliverize.repository.UsersRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UsersService implements UserDetailsService {
    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public List<User> getAllUsers() {
        return usersRepository.findAll();
    }

    public List<User> findUsersByRole(String role) {
        return usersRepository.findByRole(role);
    }

    public Optional<User> getUserById(Long id) {
        return usersRepository.getUserById(id);
    }

    public User createUser(UserDto userDto) {
        return usersRepository.save(new User(userDto));
    }

    public User getAuthUser(UserDetails details) {
        return usersRepository.findByUsernameAndPassword(details.getUsername(), details.getPassword());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = usersRepository.findByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("User with such username does not exist.");
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority(user));
    }

    public ResponseEntity<ChangeCompanyStatusRE> managerChangeCompanyStatus(UserDto companyDto) {
        ChangeCompanyStatusRE re = new ChangeCompanyStatusRE();

        Optional<User> companyOpt = usersRepository.getUserById(companyDto.getId());
        User company = companyOpt.orElse(null);

        if (company == null)
            re.addError(ErrorMsg.COMPANY_ID_NOT_FOUND.toString());
        if (!CompanyStatus.validStatus(companyDto.getCompanyStatus()))
            re.addError(ErrorMsg.INVALID_COMPANY_STATUS.toString());

        if (re.getErrors().isEmpty()) {
            assert company != null;
            company.setCompanyStatus(companyDto.getCompanyStatus());
            usersRepository.save(company);
            re.setCompany(new UserDto(company));
            return ResponseEntity.status(HttpStatus.CREATED).body(re);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(re);
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        return authorities;
    }

    public boolean usernameAvailable(String username) {
        try {
            return usersRepository.findByUsername(username) == null;
        } catch (Exception e) {
            return false;
        }
    }
}
