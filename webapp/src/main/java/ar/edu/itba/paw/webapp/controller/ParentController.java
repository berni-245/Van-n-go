package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.BookingState;
import ar.edu.itba.paw.models.Toast;
import ar.edu.itba.paw.models.ToastType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.UserBookingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class ParentController {
    protected ModelAndView redirect(String formattedPath, Object... args) {
        return new ModelAndView("redirect:" + formattedPath.formatted(args));
    }

    private <T extends User> boolean addBookingData(UserBookingService<T> bs, ModelAndView mav, T user, BookingState state, int currentPage) {
        int totPages = bs.getBookingPages(user, state);
        if (totPages == 0) return true;
        if (currentPage > totPages || currentPage < 1) return false;
        String stateLowerCase = state.toString().toLowerCase();
        String stateCapitalized = stateLowerCase.substring(0, 1).toUpperCase() + stateLowerCase.substring(1);
        mav.addObject("tot" + stateCapitalized + "Pages", totPages);
        mav.addObject(stateLowerCase + "Page", currentPage);
        mav.addObject(stateLowerCase + "Bookings", bs.getBookings(user, state, currentPage));
        return true;
    }

    protected <T extends User> ModelAndView userBookings(
            String mavPath,
            UserBookingService<T> bs,
            T loggedUser,
            int pendingPage,
            int acceptedPage,
            int finishedPage,
            int rejectedPage,
            int canceledPage,
            BookingState activeTab,
            RedirectAttributes redirectAttrs
    ) {
        final ModelAndView mav = new ModelAndView(mavPath);
        mav.addObject("currentDate", LocalDate.now());

        boolean errors = !addBookingData(bs, mav, loggedUser, BookingState.PENDING, pendingPage) ||
                         !addBookingData(bs, mav, loggedUser, BookingState.ACCEPTED, acceptedPage) ||
                         !addBookingData(bs, mav, loggedUser, BookingState.FINISHED, finishedPage) ||
                         !addBookingData(bs, mav, loggedUser, BookingState.REJECTED, rejectedPage) ||
                         !addBookingData(bs, mav, loggedUser, BookingState.CANCELED, canceledPage);
        if (errors) {
            setToasts(redirectAttrs, new Toast(ToastType.danger, "toast.booking.page.invalid"));
            return redirect("/notFound");
        }

        mav.addObject("activeTab", activeTab);
        return mav;
    }

    protected void setToasts(RedirectAttributes redirectAttributes, Toast... toasts) {
        redirectAttributes.addFlashAttribute("toasts", Arrays.stream(toasts).toList());
    }
}
