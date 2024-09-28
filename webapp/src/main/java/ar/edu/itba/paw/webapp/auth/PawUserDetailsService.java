package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.PawUserDetails;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserRole;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class PawUserDetailsService implements UserDetailsService {
    @Qualifier("userServiceImpl")
    @Autowired
    private UserService us;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final Optional<? extends User> user = us.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        final Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (user.get().isDriver()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + UserRole.DRIVER.name()));
        } else {
            authorities.add(new SimpleGrantedAuthority(("ROLE_" + UserRole.CLIENT.name())));
        }
        return new PawUserDetails(user.get(), authorities);
    }
}
