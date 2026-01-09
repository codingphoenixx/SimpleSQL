package dev.coph.simplesql.utils;

import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;

/**
 * Utility class for validating and enforcing database driver constraints during application runtime.
 * This class provides methods to ensure that the required driver is used, detect missing drivers,
 * and handle unsupported drivers for specific database operations.
 */
public class DatabaseCheck {

    /**
     * Ensures that the specified driver is within the allowed set of drivers.
     * If the driver is not included in the allowed drivers, a {@code FeatureNotSupportedException} is thrown.
     *
     * @param driver  the driver type to validate
     * @param allowed a variable-length list of allowed driver types
     * @throws FeatureNotSupportedException if the specified driver is not in the allowed list
     */
    public static void requireDriver(DriverType driver, DriverType... allowed) {
        for (DriverType d : allowed) if (d == driver) return;
        throw new FeatureNotSupportedException(driver);
    }

    /**
     * Checks if the provided {@code driver} is null and throws an {@link IllegalStateException}
     * if it is. This method is used to ensure that a valid driver is specified before
     * proceeding with database operations.
     *
     * @param driver the driver type to be validated; must not be null
     * @throws IllegalStateException if the {@code driver} is null
     */
    public static void missingDriver(DriverType driver) {
        if (driver == null)
            throw new IllegalStateException("Driver is not set.");
    }

    /**
     * Throws a {@code FeatureNotSupportedException} if the specified driver is in the list of disallowed drivers.
     * This method is used to ensure that certain features are not used with unsupported database drivers.
     *
     * @param driver     the driver type to validate
     * @param disallowed a variable-length list of drivers that are not supported
     * @throws FeatureNotSupportedException if the specified driver is in the disallowed list
     */
    public static void unsupportedDriver(DriverType driver, DriverType... disallowed) {
        for (DriverType d : disallowed)
            if (d == driver)
                throw new FeatureNotSupportedException(driver);
    }
}
