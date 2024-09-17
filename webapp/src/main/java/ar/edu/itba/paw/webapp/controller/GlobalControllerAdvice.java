package ar.edu.itba.paw.webapp.controller;
import ar.edu.itba.paw.models.PawUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addUserDetailsToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof PawUserDetails user) {
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("id", user.getId());
            model.addAttribute("authorities", user.getAuthorities());
        }
    }

    @ModelAttribute(value = "loggedUser",binding = false)
    public PawUserDetails getLoggedUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() instanceof PawUserDetails pud) {
            return pud;
        }
        return null;
    }
}