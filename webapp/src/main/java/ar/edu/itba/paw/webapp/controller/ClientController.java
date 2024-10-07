package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.services.ZoneService;
import ar.edu.itba.paw.webapp.form.AvailabilitySearchForm;
import ar.edu.itba.paw.webapp.form.BookingReviewForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
public class ClientController {
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

    @RequestMapping(path = "/upload/pop", method = RequestMethod.POST)
    public String submit(@RequestParam("proofOfPayment") MultipartFile file, @RequestParam("bookingId") long bookingId, @RequestParam("driverId") long driverId, @ModelAttribute("loggedUser") User loggedUser) {
        if (file == null || file.isEmpty()) {
            return "redirect:/bookings";
        }
        try {
            long img_id = is.uploadPop(file.getBytes(), file.getOriginalFilename(), bookingId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "redirect:/bookings";
    }

    @RequestMapping("/bookings")
    public ModelAndView bookings(@ModelAttribute("loggedUser") Client loggedUser) {
        ModelAndView mav = new ModelAndView("client/bookings");
        mav.addObject("bookings", cs.getBookings(loggedUser.getId()));
        return mav;
    }

    @RequestMapping("/client/history")
    public ModelAndView clientHistory(
            @ModelAttribute("loggedUser") Client loggedUser,
            @ModelAttribute("bookingReviewForm") BookingReviewForm form) {
        ModelAndView mav = new ModelAndView("client/history");
        mav.addObject("history", cs.getHistory(loggedUser.getId()));
        return mav;
    }

    @RequestMapping(path = "/client/history", method = RequestMethod.POST)
    public ModelAndView sendReview(
            @ModelAttribute("loggedUser") Client loggedUser,
            @Valid @ModelAttribute("bookingReviewForm") BookingReviewForm form, BindingResult errors) {
        if (errors.hasErrors()) {
            return clientHistory(loggedUser, form);
        }
        cs.setBookingRatingAndReview(form.getBookingID(), form.getRating(), form.getReview());
        return new ModelAndView("redirect:/client/history");
    }

    @RequestMapping("/availability")
    public ModelAndView availability(
            @ModelAttribute("loggedUser") Client loggedUser,
            @RequestParam(name = "zoneId", required = false) Long zoneId,
            @RequestParam(name = "size", required = false) Size size,
            @Valid @ModelAttribute("availabilitySearchForm") AvailabilitySearchForm form,
            BindingResult errors
    ) {
        // The default should be the client's zone.
        if (zoneId == null) {
            zoneId = 1L;
            form.setZoneId(zoneId);
        }
        if (size == null) {
            size = Size.SMALL;
            form.setSize(size);
        }
        if (errors.hasErrors()) return new ModelAndView();
        final ModelAndView mav = new ModelAndView("client/availability");
        List<Driver> drivers = ds.getAll(zoneId, size);
        List<Zone> zones = zs.getAllZones();
        mav.addObject("drivers", drivers);
        mav.addObject("zones", zones);
        mav.addObject("zoneId", zoneId);
        mav.addObject("size", size);
        return mav;
    }

    @RequestMapping("/availability/{id:\\d+}")
    public ModelAndView driverAvailability(
            @PathVariable(name = "id") long id,
            @RequestParam(name = "zoneId") long zoneId,
            @RequestParam(name = "size") Size size,
            @ModelAttribute("loggedUser") Client loggedUser
    ) {
        Optional<Driver> driver = ds.findById(id);
        if (driver.isPresent()) {
            final ModelAndView mav = new ModelAndView("client/driverAvailability");
            Set<Integer> workingDays = new HashSet<>();
            var wa = ds.getWeeklyAvailability(id, zoneId, size);
            wa.forEach(
                    weeklyAvailability -> workingDays.add(weeklyAvailability.getWeekDay())
            );
            mav.addObject("workingDays", workingDays);
            var vehicles = ds.getVehiclesFull(id, zoneId, size);
            mav.addObject("vehicles", vehicles);
            var bookings = ds.getBookings(driver.get().getId());
            mav.addObject("bookings", bookings);
            mav.addObject("driver", driver.get());
            Optional<Zone> zone = zs.getZone(zoneId);
            if (zone.isEmpty()) return new ModelAndView();
            mav.addObject("zone", zone.get());
            mav.addObject("size", size.name().toLowerCase());
            return mav;
        } else {
            return new ModelAndView("redirect:/403");
        }
    }

    @RequestMapping(path = "/availability/contact", method = RequestMethod.POST)
    public ModelAndView appointbooking(
            @RequestParam("clientId") long clientId,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam("driverId") long driverId,
            @RequestParam("bookingDate") String date
    ) {
        // TODO cambiar driverId to vehicleId
        long vehicleId = driverId;
        // TODO add zoneId logic
        long zoneId = 1;
        // TODO add HourInterval logc
        HourInterval hourInterval = new HourInterval(0, 24);

        Optional<Booking> booking = cs.appointBooking(vehicleId, clientId, zoneId, date, hourInterval, jobDescription);
        if (booking.isPresent()) {
            return new ModelAndView("redirect:/bookings");
        }
        return new ModelAndView("redirect:/bookings");
    }

}