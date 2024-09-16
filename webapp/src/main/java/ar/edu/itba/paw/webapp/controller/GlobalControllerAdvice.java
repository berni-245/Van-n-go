package ar.edu.itba.paw.webapp.controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addUserDetailsToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            model.addAttribute("username", user.getUsername());
            model.addAttribute("accountNonExpired", user.isAccountNonExpired());
            model.addAttribute("credentialsNonExpired", user.isCredentialsNonExpired());
            model.addAttribute("accountNonLocked", user.isAccountNonLocked());
            model.addAttribute("authorities", user.getAuthorities());
        }
    }
}