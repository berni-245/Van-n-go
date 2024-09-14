package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.MailService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class PublicController {

    @Autowired
    private UserService us;
    @Autowired
    private PawUserDetailsService puds;

    @Autowired
    private DriverService ds;

    public PublicController(UserService us, DriverService ds) {
        this.us = us;
        this.ds = ds;
    }

    @RequestMapping(path = {"/", "/home"})
    public ModelAndView index() {
        return new ModelAndView("public/home");
    }

    @RequestMapping("/{userId:\\d+}")
    public ModelAndView profile(@PathVariable(name = "userId") long userId) {
        final ModelAndView mav = new ModelAndView("public/profile");
        Optional<User> user = us.findById(userId);
        final String username;
        if (user.isPresent()) {
            username = user.get().getUsername();
        } else {
            username = "No user found";
        }
        mav.addObject("username", username);
        mav.addObject("userId", userId);
        return mav;
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("userForm") UserForm userForm, BindingResult errors) {
        if (errors.hasErrors()) {
            return createForm(userForm);
        }
        final User user = us.create(userForm.getUsername(), userForm.getMail(),userForm.getPassword());
        puds.loadUserByUsername(user.getUsername());
        return new ModelAndView("redirect:/home");
    }

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("userForm") UserForm userForm) {
        return new ModelAndView("public/register");
    }

    @RequestMapping(path = "/login")
    public ModelAndView login() {
        return new ModelAndView("public/login");
    }
}
