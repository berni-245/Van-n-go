package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Vehicle;
import ar.edu.itba.paw.models.WeeklyAvailability;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ZoneService;
import ar.edu.itba.paw.webapp.form.AvailabilityForm;
import ar.edu.itba.paw.webapp.form.VehicleForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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

    @RequestMapping(path = "/driver/vehicle/add", method = RequestMethod.POST)
    public ModelAndView addVehiclePost(
            @ModelAttribute("loggedUser") User loggedUser,
            @Valid @ModelAttribute("vehicleForm") VehicleForm vehicleForm,
            BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return addVehicleGet(vehicleForm);
        }
        final Vehicle vehicle = ds.addVehicle(
                loggedUser.getId(),
                vehicleForm.getPlateNumber(),
                vehicleForm.getVolume(),
                vehicleForm.getDescription()
        );
        return new ModelAndView("redirect:/driver/vehicles");
    }

    @RequestMapping(path = "/driver/vehicle/add", method = RequestMethod.GET)
    public ModelAndView addVehicleGet(@ModelAttribute("vehicleForm") VehicleForm vehicleForm) {
        return new ModelAndView("driver/add_vehicle");
    }

    @RequestMapping(path = "/driver/{driverId:\\d+}/availability/add", method = RequestMethod.POST)
    public ModelAndView addAvailability(
            @Valid @ModelAttribute("availabilityForm") AvailabilityForm form,
            BindingResult errors,
            @PathVariable long driverId
    ) {
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

//        if (checkIfUserIsAllowed(driver)) {
        final ModelAndView mav = new ModelAndView("driver/add_availability");
        mav.addObject("username", driver.get().getUsername());
        mav.addObject("vehicles", ds.getVehicles(driverId));
        mav.addObject("zones", zs.getAllZones());
        return mav;
//        } else {
//            return new ModelAndView("redirect:/");
//        }
    }

    @RequestMapping(path = "/driver/vehicles")
    public ModelAndView vehiclesDashboard(@ModelAttribute("loggedUser") User loggedUser) {
        final ModelAndView mav = new ModelAndView("driver/vehicles");
        mav.addObject("vehicles", ds.getVehicles(loggedUser.getId()));
        return mav;
    }

    @RequestMapping(path = "/driver/availability")
    public ModelAndView availabilityDashboard(@ModelAttribute("loggedUser") User loggedUser) {
        final ModelAndView mav = new ModelAndView("driver/availability");
//        mav.addObject("vehicles", ds.getVehicles(loggedUser.getId()));
        return mav;
    }

    @RequestMapping(path = "/driver/vehicle/edit", method = RequestMethod.GET)
    public ModelAndView editVehicleGet(
            @ModelAttribute("loggedUser") User loggedUser,
            @RequestParam(name = "plateNumber") String plateNumber,
            @ModelAttribute("vehicleForm") VehicleForm form
    ) {
        var vehicle = ds.findVehicleByPlateNumber(loggedUser.getId(), plateNumber);
        if (vehicle.isPresent()) {
            form.setAll(vehicle.get());
            var mav = new ModelAndView("driver/edit_vehicle");
            mav.addObject("plateNumber", plateNumber);
            return mav;
        } else {
            return new ModelAndView();
        }
    }

    @RequestMapping(path = "/driver/vehicle/edit", method = RequestMethod.POST)
    public ModelAndView editVehiclePost(
            @ModelAttribute("loggedUser") User loggedUser,
            @RequestParam("ogPlateNumber") String ogPlateNumber,
            @Valid @ModelAttribute("vehicleForm") VehicleForm form,
            BindingResult errors
    ) {
        // Clearly hay que buscar la manera correcta de ignorar un error particular.
        if (errors.hasErrors()) {
            boolean ignoreError = false;
            if (errors.getErrorCount() == 1 && form.getPlateNumber().equals(ogPlateNumber)) {
                for (var error : errors.getFieldErrors("plateNumber")) {
                    if (error.getCode().equals("ValidPlateNumber")) {
                        ignoreError = true;
                    }
                }
            }
            if (!ignoreError) {
                return editVehicleGet(loggedUser, ogPlateNumber, form);
            }
        }
        boolean success = ds.updateVehicle(loggedUser.getId(), new Vehicle(
                form.getId(),
                loggedUser.getId(),
                form.getPlateNumber(),
                form.getVolume(),
                form.getDescription()
        ));

        return new ModelAndView("redirect:/driver/vehicles");
    }
}