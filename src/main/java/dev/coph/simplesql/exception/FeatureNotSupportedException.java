package dev.coph.simplesql.exception;

import dev.coph.simplesql.driver.DriverType;

/**
 * An exception thrown to indicate that a specific feature is not supported
 * by the current database driver being utilized.
 * <p>
 * This runtime exception is typically thrown when attempting to execute an
 * operation or use a feature that is incompatible or unavailable with the
 * {@code DriverType} currently in use.
 * <p>
 * The {@code DriverType} is provided as an argument to the constructor, which
 * allows the error message to specify the unsupported feature and the associated
 * driver type.
 */
public class FeatureNotSupportedException extends RuntimeException {

    /**
     * Constructs a new {@code FeatureNotSupportedException} with a message indicating
     * that the requested feature is not supported by the specified {@code DriverType}.
     *
     * @param driver the database driver type associated with the feature; used to
     *               indicate which driver is not compatible with the requested feature
     */
    public FeatureNotSupportedException(DriverType driver) {
        super("The is feature is not available with your current driver (" + (driver == null ? "N/A" : driver.name()) + ").");
    }

}
