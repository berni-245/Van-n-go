package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.ChangeUserInfoForm;
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
    @Qualifier("userServiceImpl")
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/profile/edit")
    public ModelAndView profileEdit(@ModelAttribute("loggedUser") User loggedUser, @ModelAttribute("changeUserInfoForm") ChangeUserInfoForm form) {
        ModelAndView mav = new ModelAndView("user/profileEdit");


        mav.addObject("loggedUser", loggedUser);
        return mav;
    }

    @RequestMapping(path = "/profile/edit", method = RequestMethod.POST)
    public ModelAndView profileEditSummit(@ModelAttribute("loggedUser") User loggedUser, @Valid @ModelAttribute("changeUserInfoForm") ChangeUserInfoForm form, BindingResult errors) {
        if((errors.hasFieldErrors("username") && form.getUsernameChanged()) ||
                (errors.hasFieldErrors("mail") && form.getMailChanged())){
            return profileEdit(loggedUser,form);
        }
        if(form.getMailChanged()){
            userService.updateMail(loggedUser.getId(), form.getMail());
            loggedUser.setMail(form.getMail());
        }
        if(form.getUsernameChanged()){
            userService.updateUsername(loggedUser.getId(), form.getUsername());
            loggedUser.setUsername(form.getUsername());
        }

        return new ModelAndView("redirect:/profile");
    }

}
