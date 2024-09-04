package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.DriverForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class HelloWorldController {

    @Autowired
    private UserService us;

    @Autowired
    private DriverService ds;

    public HelloWorldController(final UserService us) {
        this.us = us;
        this.ds = ds;
    }

    @RequestMapping("/")
    public ModelAndView index() {
        final ModelAndView mav = new ModelAndView("helloworld/home");
        return mav;
    }

    @RequestMapping("/{userId:\\d+}")
    public ModelAndView profile(@PathVariable(name = "userId") long userId) {
        final ModelAndView mav = new ModelAndView("helloworld/profile");
        Optional<User> user = us.findById(userId);
        mav.addObject("username", user.get().getUsername());
        mav.addObject("userId", user.get().getId());
        return mav;
    }

    //TODO: Hashear la password en us.create

    PasswordEncoder pw = new BCryptPasswordEncoder();

    @RequestMapping(path = "/driver/register", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("driverForm") DriverForm driverForm, BindingResult errors) {
        if (errors.hasErrors()) {
            return createForm(driverForm);
        }
        final User user = ds.create(
                driverForm.getUsername(),
                driverForm.getMail(),
                pw.encode(driverForm.getPassword()),
                driverForm.getExtra1()
        );
        return new ModelAndView("redirect:/availability");
    }

    @RequestMapping(path = "/driver/register", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("driverForm") DriverForm driverForm) {
        return new ModelAndView("driver/register");
    }

    @RequestMapping(path = "/test")
    public ModelAndView test() {
        return new ModelAndView("helloworld/materialTest");
    }

    @RequestMapping(path = "/home")
    public ModelAndView home() {
        return new ModelAndView("helloworld/home");
    }

    @RequestMapping(path = "/login")
    public ModelAndView login() {
        return new ModelAndView("helloworld/login");
    }
}
