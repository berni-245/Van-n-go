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
    private static final Logger log = LoggerFactory.getLogger(ClientController.class);
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
            @RequestParam(value = "page", defaultValue = "0") int page,
            @ModelAttribute("loggedUser") Client loggedUser
            ) {
        ModelAndView mav = new ModelAndView("client/bookings");
        mav.addObject("bookings", cs.getBookings(loggedUser.getId(),page));
        mav.addObject("currentPage", page);
        mav.addObject("totalPages", (int) Math.ceil((double)cs.getTotalBookingCount(loggedUser.getId()) / Pagination.BOOKINGS_PAGE_SIZE));
        return mav;
    }

    @RequestMapping(path = "/client/bookings/upload/pop", method = RequestMethod.POST)
    public ModelAndView submit(
            @RequestParam("proofOfPayment") MultipartFile file,
            @RequestParam("bookingId") long bookingId,
            @ModelAttribute("loggedUser") User loggedUser
    ) throws IOException {
        if(file == null || file.isEmpty())
            throw new InvalidImageException();
        is.uploadPop(file.getBytes(), file.getOriginalFilename(), bookingId);
        return new ModelAndView("redirect:/client/bookings");
    }

    @RequestMapping(path = "/client/history", method = RequestMethod.GET)
    public ModelAndView clientHistory(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @ModelAttribute("bookingReviewForm") BookingReviewForm form,
            @ModelAttribute("loggedUser") Client loggedUser
    ) {
        List<Booking> paginatedHistory = cs.getHistory(loggedUser.getId(), page);
        long totalRecords = cs.getTotalHistoryCount(loggedUser.getId());
        int totalPages = (int) Math.ceil((double) totalRecords / Pagination.BOOKINGS_PAGE_SIZE);
        ModelAndView mav = new ModelAndView("client/history");
        mav.addObject("history", paginatedHistory);
        mav.addObject("currentPage", page);
        mav.addObject("totalPages", totalPages);
        return mav;
    }

    @RequestMapping(path = "/client/history/send/review", method = RequestMethod.POST)
    public ModelAndView sendReview(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Valid @ModelAttribute("bookingReviewForm") BookingReviewForm form,
            @ModelAttribute("loggedUser") Client loggedUser,
            BindingResult errors
            ) {
        if (errors.hasErrors()) {
            return clientHistory(page, form, loggedUser);
        }
        cs.setBookingRatingAndReview(form.getBookingID(), form.getRating(), form.getReview());
        return redirect("/client/history");
    }

    @RequestMapping(path = "/client/booking/{id:\\d+}/cancel", method = RequestMethod.POST)
    public ModelAndView cancelBooking(
            @ModelAttribute("loggedUser") Client loggedUser,
            @PathVariable("id") long bookingId
    ) {
        cs.cancelBooking(bookingId, loggedUser ,LocaleContextHolder.getLocale());
        return redirect("/");
    }

    @RequestMapping(path = "/client/search", method = RequestMethod.GET)
    public ModelAndView search(
            @ModelAttribute("availabilitySearchForm") AvailabilitySearchForm form,
            @ModelAttribute("loggedUser") Client loggedUser
            ) {
        ModelAndView mav = new ModelAndView("client/search");
        if(loggedUser.getZone() != null)
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
            @RequestParam(name = "page", defaultValue = "0") int page,
            @ModelAttribute("availabilitySearchForm") AvailabilitySearchForm form,
            @ModelAttribute("loggedUser") Client loggedUser
    ) {
        form.setZoneId(Objects.requireNonNullElseGet(zoneId, () -> loggedUser.getZone() != null ? loggedUser.getZone().getId() : 1L));
        zoneId = form.getZoneId();
        final ModelAndView mav = new ModelAndView("client/availability");
        List<Driver> drivers = ds.getAll(zoneId, size, priceMin, priceMax, weekday, rating, page);
        List<Zone> zones = zs.getAllZones();
        mav.addObject("drivers", drivers);
        mav.addObject("zones", zones);
        mav.addObject("currentPage", page);
        mav.addObject("totalPages", (int) Math.ceil((double) ds.totalMatches(zoneId,size, priceMin, priceMax, weekday, rating) / Pagination.SEARCH_PAGE_SIZE));
        mav.addObject("zoneId", zoneId);
        mav.addObject("size", size);
        mav.addObject("priceMin", priceMin);
        mav.addObject("priceMax", priceMax);
        mav.addObject("weekday", weekday);
        mav.addObject("rating", rating);
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
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @ModelAttribute("bookingForm") BookingForm form,
            @ModelAttribute("loggedUser") Client loggedUser
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
            mav.addObject("page",page);
            return mav;
        } else {
            return new ModelAndView("redirect:/403");
        }
    }

    @RequestMapping(path = "/client/availability/{driverId:\\d+}", method = RequestMethod.POST)
    public ModelAndView appointBooking(
            @PathVariable(name = "driverId") long driverId,
            @RequestParam(name = "size", required = false) Size size,
            @RequestParam(name = "priceMin", required = false) Double priceMin,
            @RequestParam(name = "priceMax", required = false) Double priceMax,
            @RequestParam(name = "weekday", required = false) DayOfWeek weekday,
            @Valid @ModelAttribute("bookingForm") BookingForm form,
            @ModelAttribute("loggedUser") Client loggedUser,
            BindingResult errors,
            RedirectAttributes redirectAttributes
    ) {
        List<Toast> toasts = new ArrayList<>();
        if (errors.hasErrors()) {
            return driverAvailability(driverId, form.getOriginZoneId(), size, priceMin, priceMax, weekday, 0,0, form, loggedUser);
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
        return new ModelAndView("redirect:/client/bookings");
    }


    @RequestMapping(path = "/client/change/password", method = RequestMethod.GET)
    public ModelAndView changePassword(
            @ModelAttribute("changePasswordForm") ChangePasswordForm form,
            @ModelAttribute("loggedUser") Client loggedUser
    ) {
        ModelAndView mav = new ModelAndView("public/changePassword");
        mav.addObject("loggedUser", loggedUser);
        mav.addObject("userTypePath", "client");
        return mav;
    }

    @RequestMapping(path = "/client/change/password", method = RequestMethod.POST)
    public ModelAndView postChangePassword(
            @Valid @ModelAttribute("changePasswordForm") ChangePasswordForm form,
            @ModelAttribute("loggedUser") Client loggedUser,
            BindingResult errors
    ) {
        if(errors.hasErrors()) return changePassword(form, loggedUser);

        cs.updatePassword(loggedUser.getId(), form.getPassword());
        return new ModelAndView("redirect:/client/profile");
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
        mav.addObject("zones",zs.getAllZones());
        return mav;
    }

    @RequestMapping(path = "/client/profile/edit", method = RequestMethod.POST)
    public ModelAndView editProfile(
            @Valid @ModelAttribute("changeUserInfoForm") ChangeUserInfoForm form,
            @ModelAttribute("loggedUser") Client loggedUser,
            BindingResult errors
    ) {
        if (errors.hasErrors()) return editProfileForm(loggedUser, form, errors);
        cs.editProfile(loggedUser, form.getUsername(), form.getMail(), form.getZoneId());
        return redirect("/client/profile");
    }


}