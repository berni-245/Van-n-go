package ar.edu.itba.paw.exceptions;

import ar.edu.itba.paw.models.User;

public class ForbiddenUserBookingAccessException extends RuntimeException {
    public ForbiddenUserBookingAccessException(User user, int bookingId) {
        super(
                "User %s requested access to booking %d which doesn't exist or isn't theirs"
                        .formatted(user == null ? "null" : user.getId(), bookingId)
        );
    }
}
