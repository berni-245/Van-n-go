package ar.edu.itba.paw.webapp.controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalControllerAdvice {
    @ModelAttribute
    public void addUserToModel(ModelAndView mav, HttpSession session) {
        // If you are using Spring Security, get the logged-in user's details like this:
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // Assuming the principal contains user details (custom user object or UserDetails)
            Object principal = authentication.getPrincipal();

            // Add the user object to the ModelAndView
            mav.addObject("user", principal);
        }
    }
}
