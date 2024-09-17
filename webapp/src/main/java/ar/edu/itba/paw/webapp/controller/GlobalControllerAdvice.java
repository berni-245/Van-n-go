package ar.edu.itba.paw.webapp.controller;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService us;

    @ModelAttribute
    public void addUserDetailsToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            ar.edu.itba.paw.models.User loggedUser = us.findByUsername(user.getUsername());

            model.addAttribute("loggedUsername", loggedUser.getUsername());
            model.addAttribute("loggedMail", loggedUser.getMail());
            model.addAttribute("loggedId", loggedUser.getId());
            model.addAttribute("accountNonExpired", user.isAccountNonExpired());
            model.addAttribute("credentialsNonExpired", user.isCredentialsNonExpired());
            model.addAttribute("accountNonLocked", user.isAccountNonLocked());
            model.addAttribute("authorities", user.getAuthorities());
        }
    }
}