package pl.javageek.exchange.user;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        val optional = userRepository.findByUsername(username);
        if (!optional.isPresent()) {
            throw new UsernameNotFoundException("No user found with username: " + username);
        }
        val user = optional.get();
        val enabled = true;
        val accountNonExpired = true;
        val credentialsNonExpired = true;
        val accountNonLocked = true;
        return new org.springframework.security.core.userdetails.User
            (user.getUsername(),
                    user.getPassword(), enabled, accountNonExpired, credentialsNonExpired,
                    accountNonLocked, getAuthorities());
    }

    private Collection<? extends GrantedAuthority> getAuthorities(){
        return getGrantedAuthorities(getRoles());
    }

    private List<String> getRoles() {
        return Collections.singletonList("ROLE_USER");
    }
    private static List<GrantedAuthority> getGrantedAuthorities (List<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}