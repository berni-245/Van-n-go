package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Vehicle;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.VehicleService;
import ar.edu.itba.paw.webapp.form.VehicleForm;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class ValidPlateNumberValidator extends ValidatorWithLoggedUser implements ConstraintValidator<ValidPlateNumber, VehicleForm> {
    @Autowired
    private VehicleService vs;
    @Autowired
    private DriverService ds;

    @Override
    public boolean isValid(VehicleForm form, ConstraintValidatorContext constraintValidatorContext) {
        String newPN = form.getPlateNumber();
        if (form.getId() != null) {
            Optional<Vehicle> mayBeVehicle = ds.findVehicleById((Driver) getUser(), form.getId());
            if (mayBeVehicle.isEmpty()) return false;
            Vehicle vehicle = mayBeVehicle.get();
            if (vehicle.getPlateNumber().equals(newPN)) return true;
        }
        return !vs.plateNumberExists(newPN);
    }
}