package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.form.AvailabilitySearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDate;
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


    @RequestMapping("/availability")
    public ModelAndView availability(
            @ModelAttribute("loggedUser") User loggedUser,
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

    @RequestMapping(path = "/availability/contact", method = RequestMethod.POST)
    public ModelAndView sendRequestServiceMail(@RequestParam("clientMail") String clientMail,
                                               @RequestParam("jobDescription") String jobDescription,
                                               @RequestParam("driverMail") String driverMail,
                                               @RequestParam("driverName") String driverName,
                                               @RequestParam("clientName") String clientName,
                                               @RequestParam("bookingDate") String date) {
        Optional<Booking> booking = cs.appointBooking(driverName, clientName, LocalDate.parse(date));
        if (booking.isPresent()) {
            mailService.sendRequestedHauler(clientMail, driverMail, clientName, driverName, jobDescription);
            return new ModelAndView("redirect:/availability");
        }
        return new ModelAndView("redirect:/availability");
    }

}