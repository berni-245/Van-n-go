package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.Toast;
import ar.edu.itba.paw.models.ToastType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFound(UserNotFoundException e, RedirectAttributes redirectAttributes) {
        addToast(redirectAttributes, new Toast(ToastType.danger, "toast.user.not.found"));
        return new ModelAndView("redirect:/notFound");
    }

    @ExceptionHandler(IOException.class)
    public ModelAndView handleIO(IOException e, RedirectAttributes redirectAttributes) {
        addToast(redirectAttributes, new Toast(ToastType.danger, "toast.io.error"));
        return new ModelAndView("redirect:/internalError");
    }

    @ExceptionHandler(InvalidUserOnBookingCancelException.class)
    public ModelAndView handleInvalidUserOnBookingCancel(InvalidUserOnBookingCancelException e, RedirectAttributes redirectAttributes) {
        addToast(redirectAttributes, new Toast(ToastType.danger, "toast.invalid.user.booking.cancel"));
        return new ModelAndView("redirect:/");
    }

    @ExceptionHandler(InvalidUserOnBookingAcceptException.class)
    public ModelAndView handleInvalidUserOnBookingAccept(InvalidUserOnBookingAcceptException e, RedirectAttributes redirectAttributes) {
        addToast(redirectAttributes, new Toast(ToastType.danger, "toast.invalid.user.booking.accept"));
        return new ModelAndView("redirect:/");
    }

    @ExceptionHandler(InvalidUserOnBookingRejectException.class)
    public ModelAndView handleInvalidUserOnBookingReject(InvalidUserOnBookingRejectException e, RedirectAttributes redirectAttributes) {
        addToast(redirectAttributes, new Toast(ToastType.danger, "toast.invalid.user.booking.reject"));
        return new ModelAndView("redirect:/");
    }

    @ExceptionHandler(InvalidUserOnBookingFinishException.class)
    public ModelAndView handleInvalidUserOnBookingFinish(InvalidUserOnBookingFinishException e, RedirectAttributes redirectAttributes) {
        addToast(redirectAttributes, new Toast(ToastType.danger, "toast.invalid.user.booking.finish"));
        return new ModelAndView("redirect:/");
    }

    @ExceptionHandler(InvalidImageException.class)
    public ModelAndView handleInvalidImageException(InvalidImageException ex, RedirectAttributes redirectAttributes) {
        addToast(redirectAttributes, new Toast(ToastType.danger, "toast.image.invalid"));
        return new ModelAndView("redirect:/");
    }

    @ExceptionHandler(ClientAlreadyAppointedException.class)
    public ModelAndView handleClientAlreadyAppointed(ClientAlreadyAppointedException e, RedirectAttributes redirectAttributes,
                                                     HttpServletRequest request) {
        addToast(redirectAttributes, new Toast(ToastType.danger, "toast.booking.client.already.appointed"));
        String currentUrl = request.getRequestURL().toString() + "?" + request.getQueryString();
        return new ModelAndView("redirect:" + currentUrl);
    }

    @ExceptionHandler(TimeAlreadyPassedException.class)
    public ModelAndView handleTimeAlreadyPassed(TimeAlreadyPassedException e, RedirectAttributes redirectAttributes,
                                                     HttpServletRequest request) {
        addToast(redirectAttributes, new Toast(ToastType.danger, "toast.booking.time.already.passed"));
        String currentUrl = request.getRequestURL().toString() + "?" + request.getQueryString();
        return new ModelAndView("redirect:" + currentUrl);
    }

    @ExceptionHandler(VehicleIsAlreadyAcceptedException.class)
    public ModelAndView handleVehicleIsAlreadyAccepted(VehicleIsAlreadyAcceptedException e, RedirectAttributes redirectAttributes,
                                                HttpServletRequest request) {
        addToast(redirectAttributes, new Toast(ToastType.danger, "toast.booking.vehicle.is.already.accepted"));
        String currentUrl = request.getRequestURL().toString() + "?" + request.getQueryString();
        return new ModelAndView("redirect:" + currentUrl);
    }

    @ExceptionHandler(VehicleNotAvailableException.class)
    public ModelAndView handleVehicleNotAvailable(VehicleNotAvailableException e, RedirectAttributes redirectAttributes,
                                                       HttpServletRequest request) {
        addToast(redirectAttributes, new Toast(ToastType.danger, "toast.booking.vehicle.not.available"));
        String currentUrl = request.getRequestURL().toString() + "?" + request.getQueryString();
        return new ModelAndView("redirect:" + currentUrl);
    }

    private void addToast(RedirectAttributes redirectAttributes, Toast toast) {
        ArrayList<Toast> toasts = new ArrayList<>();
        toasts.add(toast);
        redirectAttributes.addFlashAttribute("toasts", toasts);
    }
}
