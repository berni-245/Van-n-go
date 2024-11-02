package ar.edu.itba.paw.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ParentController {
    protected ModelAndView redirect(String formattedPath, Object... args) {
        return new ModelAndView("redirect:" + formattedPath.formatted(args));
    }
}
