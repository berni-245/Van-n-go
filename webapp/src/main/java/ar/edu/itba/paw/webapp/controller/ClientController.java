package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Vehicle;
import ar.edu.itba.paw.models.WeeklyAvailability;
import ar.edu.itba.paw.models.Zone;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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

    @RequestMapping("/availability")
    public ModelAndView availability() {
        final ModelAndView mav = new ModelAndView("client/availability");
        List<Driver> drivers = ds.getAll();
        List<Map<Long, Vehicle>> vehicles = drivers.stream()
                .map(driver -> ds.getVehicles(driver.getId()).stream()
                        .collect(Collectors.toMap(
                                Vehicle::getId,
                                vehicle -> vehicle
                        ))
                )
                .toList();
        List<List<WeeklyAvailability>> availabilities = drivers.stream().map(
                driver -> ds.getWeeklyAvailability(driver.getId())
        ).toList();
        Map<Long, Zone> zones = zs.getAllZones().stream().collect(Collectors.toMap(
                Zone::getId,
                zone -> zone
        ));
        mav.addObject("drivers", drivers);
        mav.addObject("vehicleLists", vehicles);
        mav.addObject("availabilityLists", availabilities);
        mav.addObject("zones", zones);
        return mav;
    }

}