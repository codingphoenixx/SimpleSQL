package dev.coph.simplesql.exception;

import dev.coph.simplesql.driver.DriverType;

public class FeatureNotSupportedException extends RuntimeException {


    public FeatureNotSupportedException(DriverType driver) {
        super("The is feature is not available with your current driver (" + (driver == null ? "N/A" : driver.name()) + ").");
    }
}
