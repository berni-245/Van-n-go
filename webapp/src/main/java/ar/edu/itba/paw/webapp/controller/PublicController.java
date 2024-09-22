package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserRole;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class PublicController {

    @Autowired
    private UserService us;
    @Autowired
    private ClientService cs;
    @Autowired
    private DriverService ds;

    public PublicController(UserService us, ClientService cs, DriverService ds) {
        this.us = us;
        this.cs = cs;
        this.ds = ds;
    }

    @RequestMapping(path = {"/", "/home"})
    public ModelAndView index(@ModelAttribute("loggedUser") User loggedUser) {
        if (loggedUser == null || !loggedUser.getIsDriver()) {
            return new ModelAndView("public/home");
        } else {
            final ModelAndView mav = new ModelAndView("driver/home");
            List<Booking> bookings = ds.getBookings(loggedUser.getId());
            mav.addObject("bookings",bookings);
            return mav;
        }

    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("userForm") UserForm userForm, BindingResult errors) {
        if (errors.hasErrors()) {
            return createForm(userForm);
        }
        final User user;
        if (userForm.getUserType().equals(UserRole.DRIVER.name()))
            user = ds.create(userForm.getUsername(), userForm.getMail(), userForm.getPassword(), "");
        else
            user = cs.create(userForm.getUsername(), userForm.getMail(),userForm.getPassword());
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), userForm.getPassword());
        SecurityContextHolder.getContext().setAuthentication(token);
        return new ModelAndView("redirect:/");
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
