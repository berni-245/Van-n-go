package ar.edu.itba.paw.webapp.interfaces;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

@Controller
public interface Redirect {
    default ModelAndView redirect(String formattedPath, Object... args) {
        return new ModelAndView("redirect:" + formattedPath.formatted(args));
    }
}
