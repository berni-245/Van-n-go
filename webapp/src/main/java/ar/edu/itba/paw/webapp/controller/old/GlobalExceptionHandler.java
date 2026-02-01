/*
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

    @ExceptionHandler(DriverVehicleLimitReachedException.class)
    public ModelAndView handleDriverVehicleLimitReached(
            DriverVehicleLimitReachedException ex, RedirectAttributes redirectAttributes
    ) {
        setToasts(redirectAttributes, new Toast(ToastType.danger, "toast.driver.vehicle.limit.reached"));
        LOGGER.error(ex.getMessage());
        return redirect("/driver/vehicles");
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex, RedirectAttributes redirectAttributes) {
        LOGGER.error(ex.getMessage());
        return redirect("/error/internalError");
    }
}
*/
