package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ar.edu.itba.paw.webapp.utils.CustomMediaType;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class BasicAuthFilter extends OncePerRequestFilter {

    private static final String BASIC_PREFIX = "Basic ";

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String encoded = extractBasicHeader(request);
        if (encoded == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Credentials credentials = decodeCredentials(encoded);
            Authentication auth = authenticate(credentials);
            jwtTokenUtil.addTokensToResponse((PawUserDetails) auth.getPrincipal(), response);

            chain.doFilter(request, response);
        } catch (UnauthorizedException e) {
            sendUnauthorizedError(response);
        }
    }

    private void sendUnauthorizedError(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(CustomMediaType.ERROR);
    }

    private String extractBasicHeader(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BASIC_PREFIX)) {
            return null;
        }
        return header.substring(BASIC_PREFIX.length());
    }

    private Credentials decodeCredentials(String encoded) {
        String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Malformed Basic authentication header");
        }

        int sep = decoded.indexOf(':');
        if (sep < 0) {
            throw new UnauthorizedException("Malformed Basic authentication header");
        }
        return new Credentials(decoded.substring(0, sep), decoded.substring(sep + 1));
    }

    private Authentication authenticate(Credentials credentials) {
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.username(), credentials.password())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Invalid credentials");
        }
        return auth;
    }

    private record Credentials(String username, String password) {
    }
}
