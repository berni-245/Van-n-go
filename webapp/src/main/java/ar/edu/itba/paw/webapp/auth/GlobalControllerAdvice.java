/*

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addUserDetailsToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof PawUserDetails user) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("loggedUser", user.getUser());
            model.addAttribute("authorities", user.getAuthorities());
        }
    }

    @ModelAttribute(value = "loggedUser", binding = false)
    public User getLoggedUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof PawUserDetails pud) {
            return pud.getUser();
        }
        return null;
    }
}*/
