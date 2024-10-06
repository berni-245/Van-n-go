package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.ChangeUserInfoForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @Qualifier("userServiceImpl")
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/account/edit")
    public ModelAndView profileEdit(@ModelAttribute("loggedUser") User loggedUser, @ModelAttribute("changeUserInfoForm") ChangeUserInfoForm form) {
        ModelAndView mav = new ModelAndView("user/accountEdit");


        mav.addObject("loggedUser", loggedUser);
        return mav;
    }

    @RequestMapping(path = "/account/edit", method = RequestMethod.POST)
    public ModelAndView profileEditSummit(@ModelAttribute("loggedUser") User loggedUser, @Valid @ModelAttribute("changeUserInfoForm") ChangeUserInfoForm form, BindingResult errors) {
        if(errors.hasErrors()){
            LOGGER.warn("has errors");
            return profileEdit(loggedUser,form);
        }
        if(form.getMailChanged()){
            LOGGER.warn("changing mail");
            userService.updateMail(loggedUser.getId(), form.getMail());
            loggedUser.setMail(form.getMail());
        }
        if(form.getUsernameChanged()){
            LOGGER.warn("changing username");
            userService.updateUsername(loggedUser.getId(), form.getUsername());
            loggedUser.setUsername(form.getUsername());
        }
        if(form.getPasswordChanged()){
            LOGGER.warn("changing password");
            userService.updatePassword(loggedUser.getId(), form.getPassword());
        }

        return new ModelAndView("redirect:/profile");
    }

}
