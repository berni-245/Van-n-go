package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.PawUserDetails;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomSavedRequestAwareAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {


    private final LocaleResolver localeResolver;

    @Autowired
    public CustomSavedRequestAwareAuthenticationSuccessHandler(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        super.onAuthenticationSuccess(request, response, authentication);
        PawUserDetails pawUserDetails = (PawUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = pawUserDetails.getUser();
        localeResolver.setLocale(request, response, user.getLanguage().getLocale());
    }
}
