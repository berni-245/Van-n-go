package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController {
    // This extra redirect needs to be done or otherwise the page will load without the logged user
    @RequestMapping("/403")
    public ModelAndView forbiddenRedirect() {
        return new ModelAndView("redirect:/forbidden");
    }

    @RequestMapping("/404")
    public ModelAndView notFoundRedirect() {
        return new ModelAndView("redirect:/notFound");
    }

    @RequestMapping("/500")
    public ModelAndView internalServerErrorRedirect() {
        return new ModelAndView("redirect:/internalError");
    }

    @RequestMapping("/forbidden")
    public ModelAndView forbidden() {
        return new ModelAndView("error/403");
    }

    @RequestMapping("/notFound")
    public ModelAndView notFound() {
        return new ModelAndView("error/404");
    }

    @RequestMapping("/internalError")
    public ModelAndView internalServerError() {
        return new ModelAndView("error/500");
    }
}