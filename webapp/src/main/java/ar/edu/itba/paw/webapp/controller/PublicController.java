package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserRole;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
public class PublicController {

    private static final Logger log = LoggerFactory.getLogger(PublicController.class);
    @Autowired
    private UserService us;
    @Autowired
    private ClientService cs;
    @Autowired
    private DriverService ds;
    @Autowired
    private ImageService is;

    public PublicController(UserService us, ClientService cs, DriverService ds, ImageService is) {
        this.us = us;
        this.cs = cs;
        this.ds = ds;
        this.is = is;
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

    @RequestMapping(path = "/upload/pfp", method = RequestMethod.POST)
    public ModelAndView uploadProfilePicture(@RequestParam("file") MultipartFile file, @ModelAttribute("loggedUser") User loggedUser) {
        try {
            is.uploadPfp(file.getBytes(), file.getOriginalFilename(), (int) loggedUser.getId());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new ModelAndView("redirect:/profile");
    }

    @RequestMapping(path = "/profile")
    public ModelAndView profile(@ModelAttribute("loggedUser") User loggedUser) {
        ModelAndView mav = new ModelAndView("public/profile");
        Image pfp = is.getPfp((int) loggedUser.getId());
        mav.addObject("profilePic", pfp);
        return mav;
    }
}
