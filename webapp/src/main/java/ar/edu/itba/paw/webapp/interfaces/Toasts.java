package ar.edu.itba.paw.webapp.interfaces;

import ar.edu.itba.paw.models.Toast;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

@Controller
public interface Toasts {
    default void setToasts(RedirectAttributes redirectAttributes, Toast... toasts) {
        redirectAttributes.addFlashAttribute("toasts", Arrays.stream(toasts).toList());
    }
}
