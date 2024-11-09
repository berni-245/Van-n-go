package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.InvalidImageException;
import ar.edu.itba.paw.exceptions.InvalidRecipientException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.form.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Controller
public class ClientController extends ParentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientController.class);
    @Autowired
    private DriverService ds;
    @Autowired
    private ZoneService zs;
    @Autowired
    private ClientService cs;
    @Autowired
    private ImageService is;
    @Autowired
    private MessageService ms;

    @RequestMapping(path = "/client/bookings", method = RequestMethod.GET)
    public ModelAndView bookings(
            @ModelAttribute("loggedUser") Client loggedUser,
            @RequestParam(name = "pendingPage", defaultValue = "1") int pendingPage,
            @RequestParam(name = "acceptedPage", defaultValue = "1") int acceptedPage,
            @RequestParam(name = "finishedPage", defaultValue = "1") int finishedPage,
            @RequestParam(name = "rejectedPage", defaultValue = "1") int rejectedPage,
            @RequestParam(name = "canceledPage", defaultValue = "1") int canceledPage,
            @RequestParam(name = "activeTab", defaultValue = "PENDING") BookingState activeTab,
            RedirectAttributes redirectAttrs
    ) {
        return super.userBookings(
                "client/bookings",
                cs,
                loggedUser,
                pendingPage,
                acceptedPage,
                finishedPage,
                rejectedPage,
                canceledPage,
                activeTab,
                redirectAttrs
        );
    }

    @RequestMapping(path = "/client/bookings/upload/pop", method = RequestMethod.POST)
    public ModelAndView submit(
            @RequestParam("proofOfPayment") MultipartFile file,
            @RequestParam("bookingId") long bookingId,
            @ModelAttribute("loggedUser") User loggedUser
    ) throws IOException {
        if (file == null || file.isEmpty())
            throw new InvalidImageException();
        is.uploadPop(file.getBytes(), file.getOriginalFilename(), bookingId);
        // TODO agregar query params.
        return redirect("/client/bookings?activeTab=%s", BookingState.ACCEPTED);
    }

    @RequestMapping(path = "/client/booking/{id:\\d+}/review", method = RequestMethod.GET)
    public ModelAndView reviewForm(
            @PathVariable("id") long bookingId,
            @RequestParam(name = "driverId") long driverId,
            @ModelAttribute("loggedUser") Client loggedUser,
            @ModelAttribute("bookingReviewForm") BookingReviewForm form
    ) {
        ModelAndView mav = new ModelAndView("client/bookingReview");
        mav.addObject("driver", ds.findById(driverId));
        mav.addObject("bookingId", bookingId);
        return mav;
    }

    @RequestMapping(path = "/client/booking/{id:\\d+}/review/send", method = RequestMethod.POST)
    public ModelAndView sendReview(
            @PathVariable("id") long bookingId,
            @RequestParam(name = "driverId") long driverId,
            @ModelAttribute("loggedUser") Client loggedUser,
            @Valid @ModelAttribute("bookingReviewForm") BookingReviewForm form,
            BindingResult errors,
            RedirectAttributes redirectAttrs
    ) {
        if (errors.hasErrors()) {
            LOGGER.warn("Invalid params in BookingReviewForm");
            return reviewForm(bookingId, driverId, loggedUser, form);
        } else {
            cs.setBookingRatingAndReview(bookingId, form.getRating(), form.getReview());
            setToasts(redirectAttrs, new Toast(ToastType.success, "toast.booking.review.success"));
        }
        return redirect(
                "/client/bookings?activeTab=%s",
                BookingState.FINISHED
        );
    }

    @RequestMapping(path = "/client/booking/{id:\\d+}/cancel", method = RequestMethod.POST)
    public ModelAndView cancelBooking(
            @PathVariable("id") long bookingId,
            @ModelAttribute("loggedUser") Client loggedUser,
            RedirectAttributes redirectAttributes
    ) {
        cs.cancelBooking(bookingId, loggedUser);
        setToasts(redirectAttributes, new Toast(ToastType.success, "toast.booking.cancel.success"));
        return redirect("/client/bookings?activeTab=%s", BookingState.CANCELED);
    }

    @RequestMapping(path = "/client/search", method = RequestMethod.GET)
    public ModelAndView search(
            @ModelAttribute("loggedUser") Client loggedUser,
            @ModelAttribute("availabilitySearchForm") AvailabilitySearchForm form
    ) {
        ModelAndView mav = new ModelAndView("client/search");
        if (loggedUser.getZone() != null)
            form.setZoneId(loggedUser.getZone().getId());
        List<Zone> zones = zs.getAllZones();
        mav.addObject("zones", zones);
        return mav;
    }

    @RequestMapping(path = "/client/availability", method = RequestMethod.GET)
    public ModelAndView availability(
            @RequestParam(name = "zoneId", required = false) Long zoneId,
            @RequestParam(name = "size", required = false) Size size,
            @RequestParam(name = "priceMin", required = false) Double priceMin,
            @RequestParam(name = "priceMax", required = false) Double priceMax,
            @RequestParam(name = "weekday", required = false) DayOfWeek weekday,
            @RequestParam(name = "rating", required = false) Integer rating,
            @RequestParam(name = "order", required = false) SearchOrder order,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @ModelAttribute("loggedUser") Client loggedUser,
            @ModelAttribute("availabilitySearchForm") AvailabilitySearchForm form
    ) {
        form.setZoneId(Objects.requireNonNullElseGet(zoneId, () -> loggedUser.getZone() != null ? loggedUser.getZone().getId() : 1L));
        zoneId = form.getZoneId();
        final ModelAndView mav = new ModelAndView("client/availability");
        mav.addObject(
                "drivers",
                ds.getSearchResults(zoneId, size, priceMin, priceMax, weekday, rating, order, page)
        );
        mav.addObject("zones", zs.getAllZones());
        mav.addObject("currentPage", page);
        mav.addObject(
                "totalPages",
                ds.getSearchResultPages(zoneId, size, priceMin, priceMax, weekday, rating)
        );
        mav.addObject("zoneId", zoneId);
        mav.addObject("size", size);
        mav.addObject("priceMin", priceMin);
        mav.addObject("priceMax", priceMax);
        mav.addObject("weekday", weekday);
        mav.addObject("rating", rating);
        mav.addObject("order", order);
        return mav;
    }

    @RequestMapping(path = "/client/availability/{driverId:\\d+}", method = RequestMethod.GET)
    public ModelAndView driverAvailability(
            @PathVariable(name = "driverId") long driverId,
            @RequestParam(name = "zoneId") long zoneId,
            @RequestParam(name = "size", required = false) Size size,
            @RequestParam(name = "priceMin", required = false) Double priceMin,
            @RequestParam(name = "priceMax", required = false) Double priceMax,
            @RequestParam(name = "weekday", required = false) DayOfWeek weekday,
            @RequestParam(name = "rating", required = false) Integer rating,
            @RequestParam(name = "order", required = false) SearchOrder order,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @ModelAttribute("loggedUser") Client loggedUser,
            @ModelAttribute("bookingForm") BookingForm form,
            RedirectAttributes redirectAttributes
    ) {
        Driver driver = ds.findById(driverId);
        final ModelAndView mav = new ModelAndView("client/driverAvailability");
        mav.addObject("driverId", driverId);
        Zone originZone = zs.getZone(zoneId).orElseThrow();
        List<Vehicle> vehicles = ds.getVehicles(driver, zoneId, size, priceMin, priceMax, weekday);
        Set<DayOfWeek> workingDays = ds.getWorkingDays(vehicles);
        List<Zone> zones = zs.getAllZones();
        mav.addObject("zones", zones);
        mav.addObject("vehicles", vehicles);
        mav.addObject("workingDays", workingDays);
        mav.addObject("shiftPeriods", ShiftPeriod.values());
        mav.addObject("driver", driver);
        mav.addObject("originZone", originZone);
        mav.addObject("size", size);
        mav.addObject("sizeLowerCase", size);
        mav.addObject("priceMin", priceMin);
        mav.addObject("priceMax", priceMax);
        mav.addObject("rating", rating);
        mav.addObject("weekday", weekday);
        mav.addObject("order", order);
        mav.addObject("page", page);
        mav.addObject("toasts", redirectAttributes.getAttribute("toasts"));
        return mav;
    }

    @RequestMapping(path = "/client/availability/{driverId:\\d+}", method = RequestMethod.POST)
    public ModelAndView appointBooking(
            @PathVariable(name = "driverId") long driverId,
            @RequestParam(name = "zoneId") long zoneId,
            @RequestParam(name = "size", required = false) Size size,
            @RequestParam(name = "priceMin", required = false) Double priceMin,
            @RequestParam(name = "priceMax", required = false) Double priceMax,
            @RequestParam(name = "weekday", required = false) DayOfWeek weekday,
            @RequestParam(name = "rating", required = false) Integer rating,
            @RequestParam(name = "order", required = false) SearchOrder order,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @ModelAttribute("loggedUser") Client loggedUser,
            @Valid @ModelAttribute("bookingForm") BookingForm form,
            BindingResult errors,
            RedirectAttributes redirectAttributes
    ) {
        if (errors.hasErrors()) {
            LOGGER.warn("Invalid params in BookingForm");
            return driverAvailability(driverId, zoneId, size, priceMin, priceMax, weekday, rating, order, page, loggedUser, form, redirectAttributes);
        }
        cs.appointBooking(
                form.getVehicleId(),
                loggedUser,
                form.getOriginZoneId(),
                form.getDestinationZoneId(),
                form.getDate(),
                ShiftPeriod.valueOf(form.getShiftPeriod()),
                form.getJobDescription()
        );
        setToasts(redirectAttributes, new Toast(ToastType.success, "toast.booking.success"));
        return new ModelAndView("redirect:/client/bookings");
    }

    @RequestMapping(path = "/client/chat", method = RequestMethod.GET)
    public ModelAndView chat(
            @ModelAttribute("loggedUser") Client loggedUser,
            @RequestParam("recipientId") Long recipientId
    ) {
        final ModelAndView mav = new ModelAndView("client/chat");
        Driver driver = ds.findById(recipientId);
        mav.addObject("recipient", driver);
        List<Message> messages = ms.getConversation(loggedUser, driver);
        mav.addObject("messages", messages);
        return mav;
    }

    @RequestMapping(path = "/client/send", method = RequestMethod.POST)
    public ModelAndView send(
            @ModelAttribute("loggedUser") Client loggedUser,
            @RequestParam("content") String content,
            @RequestParam("recipientId") Long recipientId
    ) {
        ms.sendClientMessage(loggedUser,recipientId,content);
        return new ModelAndView("redirect:/client/chat?recipientId=" + recipientId);
    }

    @RequestMapping(path = "/client/change/password", method = RequestMethod.GET)
    public ModelAndView changePassword(
            @ModelAttribute("loggedUser") Client loggedUser,
            @ModelAttribute("changePasswordForm") ChangePasswordForm form
    ) {
        ModelAndView mav = new ModelAndView("public/changePassword");
        mav.addObject("userTypePath", "client");
        return mav;
    }

    @RequestMapping(path = "/client/change/password", method = RequestMethod.POST)
    public ModelAndView postChangePassword(
            @ModelAttribute("loggedUser") Client loggedUser,
            @Valid @ModelAttribute("changePasswordForm") ChangePasswordForm form,
            BindingResult errors,
            RedirectAttributes redirectAttributes
    ) {
        if (errors.hasErrors()) {
            LOGGER.warn("Invalid params in ChangePasswordForm");
            return changePassword(loggedUser, form);
        }

        cs.updatePassword(loggedUser, form.getPassword());
        setToasts(redirectAttributes, new Toast(ToastType.success, "toast.user.change.password.success"));
        return redirect("/client/profile");
    }

    @RequestMapping(path = "/client/profile", method = RequestMethod.GET)
    public ModelAndView profile(@ModelAttribute("loggedUser") Client loggedUser) {
        ModelAndView mav = new ModelAndView("user/profile");
        // TODO: Por qué es necesario esto??
        mav.addObject("clientZone",zs.getClientZone(loggedUser));
        return mav;
    }

    @RequestMapping(path = "/client/profile/edit", method = RequestMethod.GET)
    public ModelAndView editProfileForm(
            @ModelAttribute("loggedUser") Client loggedUser,
            @ModelAttribute("changeUserInfoForm") ChangeUserInfoForm form,
            BindingResult errors
    ) {
        form.setOldUsername(loggedUser.getUsername());
        form.setOldMail(loggedUser.getMail());
        form.setZoneId(Optional.ofNullable(loggedUser.getZone()).map(Zone::getId).orElse(null));
        if (!errors.hasErrors()) {
            form.setMail(loggedUser.getMail());
            form.setUsername(loggedUser.getUsername());
            form.setLanguage(loggedUser.getLanguage().name());
        }
        ModelAndView mav = new ModelAndView("/user/profileEdit");
        mav.addObject("zones", zs.getAllZones());
        mav.addObject("languages", Language.values());
        return mav;
    }

    @RequestMapping(path = "/client/profile/edit", method = RequestMethod.POST)
    public ModelAndView editProfile(
            @ModelAttribute("loggedUser") Client loggedUser,
            @Valid @ModelAttribute("changeUserInfoForm") ChangeUserInfoForm form,
            BindingResult errors,
            RedirectAttributes redirectAttributes
    ) {
        if (errors.hasErrors()) {
            LOGGER.warn("Invalid params in ChangeUserInfoForm");
            return editProfileForm(loggedUser, form, errors);
        }
        cs.editProfile(loggedUser, form.getUsername(), form.getMail(), form.getZoneId(), form.getLanguage());
        setToasts(redirectAttributes, new Toast(ToastType.success, "toast.user.change.userData.success"));
        return redirect("/client/profile");
    }


}