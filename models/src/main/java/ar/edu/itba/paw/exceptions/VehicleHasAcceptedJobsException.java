package ar.edu.itba.paw.exceptions;

public class VehicleHasAcceptedJobsException extends RuntimeException {
    public VehicleHasAcceptedJobsException() {
        super("Vehicle has pending jobs and thus can't be deleted");
    }
}
