package ar.edu.itba.paw.webapp.auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String error = "1";  // Default error for unknown issue
        if (exception.getMessage().contains("Bad credentials")) {
            error = "password";  // Incorrect password
        } else if (exception.getMessage().contains("UserDetailsService returned null")) {
            error = "user";  // Unknown user
        }
        LOGGER.error(exception.getMessage());
        response.sendRedirect(request.getContextPath() + "/login?error=" + error);
    }
}
