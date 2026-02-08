package ar.edu.itba.paw.exceptions;

public class VehicleHasPendingJobsException extends RuntimeException {
    public VehicleHasPendingJobsException() {
        super("Vehicle has pending jobs and thus can't be deleted");
    }
}
