package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.UserRole;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
        try {
            setAuthenticationContextFromToken(token, false);
        } catch (ExpiredJwtException e) {
            String refreshToken = request.getHeader("Paw-refresh-token"); //TODO: poner esto en properties
            if (refreshToken != null)
                setAuthenticationContextFromToken(refreshToken, true);
        }
        chain.doFilter(request, response);
    }

    private void setAuthenticationContextFromToken(String token, boolean mustRefresh) {
        Claims claims = jwtTokenUtil.getPayloadFromToken(token);
        String type = (String) claims.get("type");
        int id = (Integer) claims.get("id");
        final Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        PawUserDetails userDetails = getUserDetails(type, id, authorities);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, authorities
        );
        if (mustRefresh) auth.setDetails("REFRESH");
        SecurityContextHolder.getContext().setAuthentication(auth);

    }

    private PawUserDetails getUserDetails(String type, int id, Collection<SimpleGrantedAuthority> authorities) {
        if (type.equals("client")) {
            Client c = cs.findById(id);
            authorities.add(new SimpleGrantedAuthority(UserRole.CLIENT.role()));
            return new PawUserDetails(c, authorities);
        }
        Driver d = ds.findById(id);
        authorities.add(new SimpleGrantedAuthority(UserRole.DRIVER.role()));
        return new PawUserDetails(d, authorities);

    }
}
