package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.UserRole;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ClientService cs;
    @Autowired
    private DriverService ds;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.split(" ")[1];
        if (!jwtTokenUtil.validate(token)) {
            chain.doFilter(request, response);
            return;
        }

        Claims claims = jwtTokenUtil.getPayloadFromToken(token);
        String type = (String) claims.get("type");
        int id = (Integer) claims.get("id");
        final Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        PawUserDetails userDetails;
        if (type.equals("client")) {
            Client c = cs.findById(id);
            authorities.add(new SimpleGrantedAuthority(UserRole.CLIENT.role()));
            userDetails = new PawUserDetails(c, authorities);
        } else {
            Driver d = ds.findById(id);
            authorities.add(new SimpleGrantedAuthority(UserRole.DRIVER.role()));
            userDetails = new PawUserDetails(d, authorities);
        }
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, authorities
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        chain.doFilter(request, response);
    }
}
