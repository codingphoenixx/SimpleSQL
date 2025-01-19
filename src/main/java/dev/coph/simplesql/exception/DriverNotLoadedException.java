package dev.coph.simplesql.exception;

public class DriverNotLoadedException extends RuntimeException {

    public DriverNotLoadedException(Throwable cause) {
        super("The Database Driver could not be loaded.", cause);
    }
}
