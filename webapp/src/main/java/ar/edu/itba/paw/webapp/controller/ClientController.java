package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ZoneService;
import ar.edu.itba.paw.webapp.form.AvailabilitySearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ClientController {
    @Autowired
    private DriverService ds;

    @Autowired
    private ZoneService zs;

    public ClientController(DriverService ds, ZoneService zs) {
        this.ds = ds;
        this.zs = zs;
    }

//    @RequestMapping("/")
//    public ModelAndView index(@RequestParam(name = "userId", defaultValue = "1") long userId) {
//        final ModelAndView mav = new ModelAndView("helloworld/index");
//        mav.addObject("username", us.findById(userId).get().getUsername());
//        mav.addObject("userId", userId);
//        return mav;
//    }

    @RequestMapping("/availability")
    public ModelAndView availability(
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

}