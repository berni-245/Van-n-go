package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Vehicle;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.webapp.form.VehicleForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class DriverController {

    @Autowired
    private DriverService ds;

    public DriverController(final DriverService ds) {
        this.ds = ds;
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

    @RequestMapping(path = "/driver/{driverId:\\d+}/vehicle/create", method = RequestMethod.POST)
    public ModelAndView addVehicle(
            @Valid @ModelAttribute("vehicleForm") VehicleForm vehicleForm,
            BindingResult errors,
            @PathVariable long driverId
    ) {
        if (errors.hasErrors()) {
            return addVehicleForm(vehicleForm, driverId);
        }
        final Vehicle vehicle = ds.addVehicle(
                driverId,
                vehicleForm.getPlateNumber(),
                vehicleForm.getVolume(),
                vehicleForm.getDescription()
        );
        if (vehicle == null) {
            errors.addError(new FieldError(
                    "vehicleForm",
                    "plateNumber",
                    "Plate number already registered")
            );
            return addVehicleForm(vehicleForm, driverId);
        }
        return new ModelAndView("redirect:/driver/" + driverId);
    }

    @RequestMapping(path = "/driver/{driverId:\\d+}/vehicle/create", method = RequestMethod.GET)
    public ModelAndView addVehicleForm(
            @ModelAttribute("vehicleForm") VehicleForm vehicleForm,
            @PathVariable long driverId
    ) {
        Optional<Driver> driver = ds.findById(driverId);
        if (driver.isPresent()) {
            final ModelAndView mav = new ModelAndView("driver/add_vehicle");
            mav.addObject("username", driver.get().getUsername());
            return mav;
        } else {
            return new ModelAndView();
        }
    }
}