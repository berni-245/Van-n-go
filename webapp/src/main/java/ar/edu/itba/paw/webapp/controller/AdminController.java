package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping(path = "/admin")
@Controller
public class AdminController {
    @RequestMapping("/dashboard")
    public ModelAndView adminDashboard(
            @ModelAttribute("loggedUser") User loggedUser
    ) {return new ModelAndView("admin/adminboard");}

    @RequestMapping("/mod/dashboard")
    public ModelAndView moderatorDashboard(
            @ModelAttribute("loggedUser") User loggedUser
    ) {return new ModelAndView("admin/modboard");}
}