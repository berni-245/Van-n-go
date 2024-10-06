package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.MailService;
import ar.edu.itba.paw.services.ZoneService;
import ar.edu.itba.paw.webapp.form.AvailabilitySearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ClientController {
    @Autowired
    private DriverService ds;

    @Autowired
    private ZoneService zs;
    @Autowired
    private MailService mailService;

    @Autowired
    private ClientService cs;

    public ClientController(DriverService ds, ClientService cs, ZoneService zs) {
        this.ds = ds;
        this.cs = cs;
        this.zs = zs;
    }

    @RequestMapping("/bookings")
    public ModelAndView bookings(@ModelAttribute("loggedUser") Client loggedUser) {
        ModelAndView mav = new ModelAndView("client/bookings");
        mav.addObject("bookings", cs.getBookings(loggedUser.getId()));
        return mav;
    }

    @RequestMapping("/client/history")
    public ModelAndView clientHistory(@ModelAttribute("loggedUser") Client loggedUser) {
        ModelAndView mav = new ModelAndView("client/history");
        mav.addObject("history", cs.getHistory(loggedUser.getId()));
        return mav;
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
        return mav;
    }

    @RequestMapping("/availability/{id:\\d+}")
    public ModelAndView driverAvailability(
            @PathVariable(name = "id") long id,
            @ModelAttribute("loggedUser") Client loggedUser
    ) {
        Optional<Driver> driver = ds.findById(id);
        if (driver.isPresent()) {
            final ModelAndView mav = new ModelAndView("client/driverAvailability");
            List<String> workingDays = new ArrayList<>();
            ds.getWeeklyAvailability(id).forEach(weeklyAvailability -> workingDays.add(weeklyAvailability.getWeekDayString()));

            mav.addObject("workingDays", workingDays);
            mav.addObject("bookings", ds.getBookings(driver.get().getId()));
            mav.addObject("driver", driver.get());
            return mav;
        } else {
            return new ModelAndView("redirect:/403");
        }
    }

    @RequestMapping(path = "/availability/contact", method = RequestMethod.POST)
    public ModelAndView sendRequestServiceMail(
            @RequestParam("clientId") long clientId,
            @RequestParam("clientName") String clientName,
            @RequestParam("clientMail") String clientMail,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam("driverId") long driverId,
            @RequestParam("driverName") String driverName,
            @RequestParam("driverMail") String driverMail,
            @RequestParam("bookingDate") String date
    ) {
        //TODO cambiar driverId to vehicleId
        // TODO add HourInterval logc
        Optional<Booking> booking = cs.appointBooking(driverId, clientId, date, new HourInterval(0, 24));
        if (booking.isPresent()) {
            mailService.sendRequestedHauler(clientMail, driverMail, clientName, driverName, jobDescription);
            return new ModelAndView("redirect:/bookings");
        }
        return new ModelAndView("redirect:/bookings");
    }

    @RequestMapping(path = "/sendReview", method = RequestMethod.POST)
    public ModelAndView sendReview(@RequestParam("bookingId") long bookingId, @RequestParam("rating") int rating){
        cs.setRating(bookingId,rating);
        return  new ModelAndView("redirect:/client/history");
    }

}