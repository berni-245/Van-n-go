package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Vehicle;
import ar.edu.itba.paw.models.WeeklyAvailability;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ZoneService;
import ar.edu.itba.paw.webapp.form.AvailabilityForm;
import ar.edu.itba.paw.webapp.form.VehicleForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class DriverController {

    @Autowired
    private DriverService ds;

    @Autowired
    private ZoneService zs;

    public DriverController(final DriverService ds, ZoneService zs) {
        this.ds = ds;
        this.zs = zs;
    }

    @RequestMapping("/driver/{id:\\d+}")
    public ModelAndView profile(@PathVariable(name = "id") long id) {
        Optional<Driver> driver = ds.findById(id);
        if (driver.isPresent()) {
            final ModelAndView mav = new ModelAndView("driver/manage_availability");
            mav.addObject("username", driver.get().getUsername());
            mav.addObject("driverId", driver.get().getId());
            return mav;
        } else {
            return new ModelAndView();
        }
    }

    @RequestMapping(path = "/driver/{driverId:\\d+}/vehicle/add", method = RequestMethod.POST)
    public ModelAndView addVehicle(
            @Valid @ModelAttribute("vehicleForm") VehicleForm vehicleForm,
            BindingResult errors,
            @PathVariable long driverId
    ) {
        if(! checkIfUserIsAllowed(driverId))
            return new ModelAndView("redirect:/");
        if (errors.hasErrors()) {
            return addVehicleForm(vehicleForm, driverId);
        }
        final Vehicle vehicle = ds.addVehicle(
                driverId,
                vehicleForm.getPlateNumber(),
                vehicleForm.getVolume(),
                vehicleForm.getDescription()
        );
        return new ModelAndView("redirect:/availability");
    }

    @RequestMapping(path = "/driver/{driverId:\\d+}/vehicle/add", method = RequestMethod.GET)
    public ModelAndView addVehicleForm(
            @ModelAttribute("vehicleForm") VehicleForm vehicleForm,
            @PathVariable long driverId
    ) {
        Optional<Driver> driver = ds.findById(driverId);
        if (checkIfUserIsAllowed(driver)) {
            final ModelAndView mav = new ModelAndView("driver/add_vehicle");
            mav.addObject("username", driver.get().getUsername());
            return mav;
        } else {
            return new ModelAndView("redirect:/");
        }
    }

    @RequestMapping(path = "/driver/{driverId:\\d+}/availability/add", method = RequestMethod.POST)
    public ModelAndView addAvailability(
            @Valid @ModelAttribute("availabilityForm") AvailabilityForm form,
            BindingResult errors,
            @PathVariable long driverId
    ) {
        if(! checkIfUserIsAllowed(driverId))
            return new ModelAndView("redirect:/");
        if (errors.hasErrors()) {
            return addAvailabilityForm(form, driverId);
        }
        List<WeeklyAvailability> successfulInsertions = ds.addWeeklyAvailability(
                driverId,
                form.getWeekDays(),
                form.getTimeStart(),
                form.getTimeEnd(),
                form.getZoneIds(),
                form.getVehicleIds()
        );
        return new ModelAndView("redirect:/availability");
    }

    @RequestMapping(path = "/driver/{driverId:\\d+}/availability/add", method = RequestMethod.GET)
    public ModelAndView addAvailabilityForm(
            @ModelAttribute("availabilityForm") AvailabilityForm availabilityForm,
            @PathVariable long driverId
    ) {
        Optional<Driver> driver = ds.findById(driverId);

        if (checkIfUserIsAllowed(driver)) {
            final ModelAndView mav = new ModelAndView("driver/add_availability");
            mav.addObject("username", driver.get().getUsername());
            mav.addObject("vehicles", ds.getVehicles(driverId));
            mav.addObject("zones", zs.getAllZones());
            return mav;
        } else {
            return new ModelAndView("redirect:/");
        }
    }

    private boolean checkIfUserIsAllowed(Optional<Driver> driver) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return driver.isPresent() && currentUser.getUsername().equals(driver.get().getUsername());
    }

    private boolean checkIfUserIsAllowed(long driverId) {
        Optional<Driver> driver = ds.findById(driverId);
        return checkIfUserIsAllowed(driver);
    }
}
