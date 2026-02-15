package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Vehicle;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class ValidChangePlateNumberValidator extends ValidatorWithLoggedUser implements ConstraintValidator<ValidChangePlateNumber, String> {

    @Autowired
    private VehicleService vs;

    @Autowired
    private DriverService ds;

    @Override
    public boolean isValid(String plateNumber, ConstraintValidatorContext constraintValidatorContext) {
        if (getUser() instanceof Driver driver) { //TODO: esto no podria encontrar un vehicle con el mismo numero de patente que licensePlate pero que sea con diferente id al que se quiere actualizar?
            Optional<Vehicle> optional = ds.findVehicleByPlateNumber(driver, plateNumber);
            if (optional.isPresent()) return true;
        }
        return plateNumber == null || !vs.plateNumberExists(plateNumber);
    }
}
