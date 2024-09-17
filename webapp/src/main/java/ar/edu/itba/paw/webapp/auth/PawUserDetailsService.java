package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.PawUserDetails;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserRole;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class PawUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService us;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = us.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        final Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if(us.isDriver(username)){
            authorities.add( new SimpleGrantedAuthority("ROLE_" + UserRole.DRIVER.name()));
        } else {
            authorities.add( new SimpleGrantedAuthority(("ROLE_" + UserRole.CLIENT.name())));
        }
        return new PawUserDetails(username, user.getPassword(),user.getId(),user.getMail(), authorities);
    }
}
