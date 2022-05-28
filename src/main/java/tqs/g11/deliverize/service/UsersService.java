package tqs.g11.deliverize.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tqs.g11.deliverize.dto.UserDto;
import tqs.g11.deliverize.enums.UserRoles;
import tqs.g11.deliverize.model.User;
import tqs.g11.deliverize.repository.UsersRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UsersService implements UserDetailsService {
    @Autowired
    private UsersRepository usersRepository;

    public List<User> getAllUsers() {
        return usersRepository.findAll();
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
            throw new UsernameNotFoundException("Invalid credentials.");
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        return authorities;
    }

    public boolean isUsernameUnique(String username) {
        try {
            return usersRepository.findByUsername(username) == null;
        } catch (Exception e) {
            return false;
        }
    }
}
