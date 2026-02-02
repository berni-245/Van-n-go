package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.UserRole;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ClientService cs;
    @Autowired
    private DriverService ds;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        Optional<Client> client = cs.findByUsername(username);
        if (client.isPresent()) {
            authorities.add(new SimpleGrantedAuthority(UserRole.CLIENT.role()));
            return new PawUserDetails(client.get(), authorities);
        }
        Optional<Driver> driver = ds.findByUsername(username);
        if (driver.isPresent()) {
            authorities.add(new SimpleGrantedAuthority(UserRole.DRIVER.role()));
            return new PawUserDetails(driver.get(), authorities);
        }
        throw new UserNotFoundException();
    }
}
