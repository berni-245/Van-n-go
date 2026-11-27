package ar.edu.itba.paw.webapp.utils;

public class ExceptionUtils {
    public static String getExceptionType(Class<? extends Exception> exceptionClass) {
        String className = exceptionClass.getSimpleName().replace("Exception", "");
        return className
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toUpperCase();
    }
}
