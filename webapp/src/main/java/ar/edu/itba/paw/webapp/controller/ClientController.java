package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.InvalidImageException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.services.ZoneService;
import ar.edu.itba.paw.webapp.form.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.*;

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

    public ClientController(DriverService ds, ClientService cs, ZoneService zs, ImageService is) {
        this.ds = ds;
        this.cs = cs;
        this.zs = zs;
        this.is = is;
    }

    @RequestMapping(path = "/client/bookings", method = RequestMethod.GET)
    public ModelAndView bookings(
            @ModelAttribute("loggedUser") Client loggedUser,
            @ModelAttribute("bookingReviewForm") BookingReviewForm form,
            BindingResult erros,
            @RequestParam(name = "pendingPage", defaultValue = "1") int pendingPage,
            @RequestParam(name = "acceptedPage", defaultValue = "1") int acceptedPage,
            @RequestParam(name = "finishedPage", defaultValue = "1") int finishedPage,
            @RequestParam(name = "rejectedPage", defaultValue = "1") int rejectedPage,
            @RequestParam(name = "canceledPage", defaultValue = "1") int canceledPage,
            @RequestParam(name = "activeTab", defaultValue = "PENDING") BookingState activeTab,
            RedirectAttributes redirectAttrs
    ) {
        if (erros.hasErrors()) {
            setToasts(redirectAttrs, new Toast(ToastType.danger, "Error in review form handle this better"));
        }
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
        LOGGER.info("Uploaded proof of payment successfully");
        // TODO agregar query params.
        return redirect("/client/bookings");
    }

    @RequestMapping(path = "/client/history/send/review", method = RequestMethod.POST)
    public ModelAndView sendReview(
            @ModelAttribute("loggedUser") Client loggedUser,
            @Valid @ModelAttribute("bookingReviewForm") BookingReviewForm form,
            BindingResult errors,
            RedirectAttributes redirectAttrs
    ) {
        if (errors.hasErrors()) {
            LOGGER.warn("Invalid params in BookingReviewForm");
            setToasts(redirectAttrs, new Toast(ToastType.danger, "toast.booking.review.invalid"));
        } else {
            cs.setBookingRatingAndReview(form.getBookingID(), form.getRating(), form.getReview());
            LOGGER.info("Review sent successfully");
        }
        return redirect(
                "/client/bookings?activeTab=%s",
                BookingState.FINISHED
        );
    }

    @RequestMapping(path = "/client/booking/{id:\\d+}/cancel", method = RequestMethod.POST)
    public ModelAndView cancelBooking(
            @PathVariable("id") long bookingId,
            @ModelAttribute("loggedUser") Client loggedUser
    ) {
        cs.cancelBooking(bookingId, loggedUser, LocaleContextHolder.getLocale());
        LOGGER.info("Successfully cancelled booking");
        return redirect("/");
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
        List<Driver> drivers = ds.getAll(zoneId, size, priceMin, priceMax, weekday, rating, order, page);
        List<Zone> zones = zs.getAllZones();
        mav.addObject("drivers", drivers);
        mav.addObject("zones", zones);
        mav.addObject("currentPage", page);
        mav.addObject("totalPages", (int) Math.ceil((double) ds.totalMatches(zoneId, size, priceMin, priceMax, weekday, rating) / Pagination.SEARCH_PAGE_SIZE));
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
        Optional<Driver> driver = ds.findById(driverId);
        if (driver.isPresent()) {
            final ModelAndView mav = new ModelAndView("client/driverAvailability");
            mav.addObject("driverId", driverId);
            Zone originZone = zs.getZone(zoneId).orElseThrow();
            List<Vehicle> vehicles = ds.getVehicles(driver.get(), zoneId, size, priceMin, priceMax, weekday);
            Set<DayOfWeek> workingDays = ds.getWorkingDays(vehicles);
            List<Zone> zones = zs.getAllZones();
            mav.addObject("zones", zones);
            mav.addObject("vehicles", vehicles);
            mav.addObject("workingDays", workingDays);
            mav.addObject("shiftPeriods", ShiftPeriod.values());
            mav.addObject("driver", driver.get());
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
        } else {
            return new ModelAndView("redirect:/403");
        }
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
        ArrayList<Toast> toasts = new ArrayList<>();
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
                form.getJobDescription(),
                LocaleContextHolder.getLocale()
        );
        redirectAttributes.addFlashAttribute("toasts", toasts);
        toasts.add(new Toast(
                ToastType.success, "toast.booking.success"
        ));
        LOGGER.info("Booking successful");
        return new ModelAndView("redirect:/client/bookings");
    }


    @RequestMapping(path = "/client/change/password", method = RequestMethod.GET)
    public ModelAndView changePassword(
            @ModelAttribute("loggedUser") Client loggedUser,
            @ModelAttribute("changePasswordForm") ChangePasswordForm form
    ) {
        ModelAndView mav = new ModelAndView("public/changePassword");
        mav.addObject("loggedUser", loggedUser);
        mav.addObject("userTypePath", "client");
        return mav;
    }

    @RequestMapping(path = "/client/change/password", method = RequestMethod.POST)
    public ModelAndView postChangePassword(
            @ModelAttribute("loggedUser") Client loggedUser,
            @Valid @ModelAttribute("changePasswordForm") ChangePasswordForm form,
            BindingResult errors
    ) {
        if (errors.hasErrors()) {
            LOGGER.warn("Invalid params in ChangePasswordForm");
            return changePassword(loggedUser, form);
        }

        cs.updatePassword(loggedUser.getId(), form.getPassword());
        LOGGER.info("Password changed successfully");
        return redirect("/client/profile");
    }

    @RequestMapping(path = "/client/profile", method = RequestMethod.GET)
    public ModelAndView profile(@ModelAttribute("loggedUser") Client loggedUser) {
        ModelAndView mav = new ModelAndView("user/profile");
        mav.addObject("loggedUser", loggedUser);
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
        }
        ModelAndView mav = new ModelAndView("/user/profileEdit");
        mav.addObject("zones", zs.getAllZones());
        return mav;
    }

    @RequestMapping(path = "/client/profile/edit", method = RequestMethod.POST)
    public ModelAndView editProfile(
            @ModelAttribute("loggedUser") Client loggedUser,
            @Valid @ModelAttribute("changeUserInfoForm") ChangeUserInfoForm form,
            BindingResult errors
    ) {
        if (errors.hasErrors()) {
            LOGGER.warn("Invalid params in ChangeUserInfoForm");
            return editProfileForm(loggedUser, form, errors);
        }
        cs.editProfile(loggedUser, form.getUsername(), form.getMail(), form.getZoneId());
        LOGGER.info("Successfully changed client info");
        return redirect("/client/profile");
    }


}