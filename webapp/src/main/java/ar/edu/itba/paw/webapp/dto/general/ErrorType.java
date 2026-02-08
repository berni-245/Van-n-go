package ar.edu.itba.paw.webapp.dto.general;

// I'll leave it in case we need to use numeric codes (waiting for sotuyo's reply)
public enum ErrorType {
    ZONE_NOT_FOUND,
    USER_NOT_FOUND,
    VEHICLE_NOT_FOUND,
    USER_ALREADY_EXISTS,
    MAIL_ALREADY_EXISTS,
    USER_IDENTITY_MISMATCH,
    UNAUTHORIZED,
    FORBIDDEN_CONVERSATION,
    INVALID_ARGUMENTS
}