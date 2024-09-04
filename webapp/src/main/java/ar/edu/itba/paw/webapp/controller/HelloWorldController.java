package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public HelloWorldController(final UserService us) {
        this.us = us;
    }

    @RequestMapping("/")
    public ModelAndView index(@RequestParam(name = "userId", defaultValue = "1") long userId) {
        final ModelAndView mav = new ModelAndView("helloworld/index");
        mav.addObject("username", us.findById(userId).get().getUsername());
        mav.addObject("userId", userId);
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

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("userForm") UserForm userForm, BindingResult errors) {
        if (errors.hasErrors()) {
            return createForm(userForm);
        }
        final User user = us.create(userForm.getUsername(), userForm.getMail(), pw.encode(userForm.getPassword()));
        return new ModelAndView("redirect:/" + user.getId());
    }

    @RequestMapping(path = "/create", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("userForm") UserForm userForm) {
        return new ModelAndView("helloworld/create");
    }

    @RequestMapping(path = "/test")
    public ModelAndView test() {
        return new ModelAndView("helloworld/materialTest");
    }

    @RequestMapping(path = "/home")
    public ModelAndView home(){
        return new ModelAndView("helloworld/home");
    }

    @RequestMapping(path = "/login")
    public ModelAndView login() {
        return new ModelAndView("helloworld/login");
    }
}
