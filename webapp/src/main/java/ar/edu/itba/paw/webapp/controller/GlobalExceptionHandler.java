package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.Toast;
import ar.edu.itba.paw.models.ToastType;
import ar.edu.itba.paw.webapp.interfaces.Redirect;
import ar.edu.itba.paw.webapp.interfaces.Toasts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler implements Redirect, Toasts {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFound(UserNotFoundException ex, RedirectAttributes redirectAttributes) {
        setToasts(redirectAttributes, new Toast(ToastType.danger, "toast.user.not.found"));
        LOGGER.error(ex.getMessage());
        return redirect("/error/notFound");
    }

    @ExceptionHandler(IOException.class)
    public ModelAndView handleIO(IOException ex, RedirectAttributes redirectAttributes) {
        setToasts(redirectAttributes, new Toast(ToastType.danger, "toast.io.error"));
        LOGGER.error(ex.getMessage());
        return redirect("/error/internalError");
    }

    @ExceptionHandler(ForbiddenClientCancelBookingException.class)
    public ModelAndView handleForbiddenClientCancelBookingException(ForbiddenClientCancelBookingException ex, RedirectAttributes redirectAttributes) {
        setToasts(redirectAttributes, new Toast(ToastType.danger, "toast.forbidden.booking.operation"));
        LOGGER.error(ex.getMessage());
        return redirect("/client/bookings");
    }

    @ExceptionHandler(value = {ForbiddenDriverCancelBookingException.class, ForbiddenBookingStateOperationException.class})
    public ModelAndView handleForbiddenDriverBookingStateOperation(ForbiddenBookingStateOperationException ex, RedirectAttributes redirectAttributes) {
        setToasts(redirectAttributes, new Toast(ToastType.danger, "toast.forbidden.booking.operation"));
        LOGGER.error(ex.getMessage());
        return redirect("/driver/bookings");
    }

    @ExceptionHandler(InvalidImageException.class)
    public ModelAndView handleInvalidImageException(InvalidImageException ex, RedirectAttributes redirectAttributes) {
        setToasts(redirectAttributes, new Toast(ToastType.danger, "toast.image.invalid"));
        LOGGER.error(ex.getMessage());
        return redirect("/");
    }

    @ExceptionHandler(ClientAlreadyAppointedException.class)
    public ModelAndView handleClientAlreadyAppointed(ClientAlreadyAppointedException ex, RedirectAttributes redirectAttributes,
                                                     HttpServletRequest request) {
        setToasts(redirectAttributes, new Toast(ToastType.danger, "toast.booking.client.already.appointed"));
        LOGGER.error(ex.getMessage());
        String currentPostUrl = request.getRequestURL().toString() + "?" + request.getQueryString();
        return redirect(currentPostUrl);
    }

    @ExceptionHandler(TimeAlreadyPassedException.class)
    public ModelAndView handleTimeAlreadyPassed(TimeAlreadyPassedException ex, RedirectAttributes redirectAttributes,
                                                HttpServletRequest request) {
        setToasts(redirectAttributes, new Toast(ToastType.danger, "toast.booking.time.already.passed"));
        LOGGER.error(ex.getMessage());
        String currentPostUrl = request.getRequestURL().toString() + "?" + request.getQueryString();
        return redirect(currentPostUrl);
    }

    @ExceptionHandler(VehicleIsAlreadyAcceptedException.class)
    public ModelAndView handleVehicleIsAlreadyAccepted(VehicleIsAlreadyAcceptedException ex, RedirectAttributes redirectAttributes,
                                                       HttpServletRequest request) {
        setToasts(redirectAttributes, new Toast(ToastType.danger, "toast.booking.vehicle.is.already.accepted"));
        LOGGER.error(ex.getMessage());
        String currentPostUrl = request.getRequestURL().toString() + "?" + request.getQueryString();
        return redirect(currentPostUrl);
    }

    @ExceptionHandler(VehicleNotAvailableException.class)
    public ModelAndView handleVehicleNotAvailable(VehicleNotAvailableException ex, RedirectAttributes redirectAttributes,
                                                  HttpServletRequest request) {
        setToasts(redirectAttributes, new Toast(ToastType.danger, "toast.booking.vehicle.not.available"));
        LOGGER.error(ex.getMessage());
        String currentPostUrl = request.getRequestURL().toString() + "?" + request.getQueryString();
        return redirect(currentPostUrl);
    }

    @ExceptionHandler(InvalidMessageException.class)
    public ModelAndView handleInvalidMessage(InvalidMessageException ex, RedirectAttributes redirectAttributes) {
        setToasts(redirectAttributes, new Toast(ToastType.danger, "toast.user.invalid.message"));
        LOGGER.error(ex.getMessage());
        return redirect("/error/internalError");
    }

    @ExceptionHandler(ForbiddenConversationException.class)
    public ModelAndView handleForbiddenConversation(ForbiddenConversationException ex, RedirectAttributes redirectAttributes) {
        setToasts(redirectAttributes, new Toast(ToastType.danger, "toast.message.forbidden.conversation"));
        LOGGER.error(ex.getMessage());
        return redirect("/error/forbidden");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ModelAndView handleNoSuchElement(NoSuchElementException ex, RedirectAttributes redirectAttributes) {
        setToasts(redirectAttributes, new Toast(ToastType.danger, "toast.no.such.element"));
        LOGGER.error(ex.getMessage());
        return redirect("/error/notFound");
    }

    @ExceptionHandler(ForbiddenUserBookingAccessException.class)
    public ModelAndView handleForbiddenUserBookingAccess(
            ForbiddenUserBookingAccessException ex, RedirectAttributes redirectAttributes
    ) {
        setToasts(redirectAttributes, new Toast(ToastType.danger, "toast.user.forbidden.booking"));
        LOGGER.error(ex.getMessage());
        return redirect("/notFound");
    }

    // TODO De-comment when deploying, Do not delete
//    @ExceptionHandler(Exception.class)
//    public ModelAndView handleGenericException(Exception ex, RedirectAttributes redirectAttributes) {
//        LOGGER.error(ex.getMessage());
//        return redirect("/error/internalError");
//    }
}
