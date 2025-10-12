package dev.coph.simplesql.utils;

import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;

public class DatabaseCheck {

    public static void requireDriver(DriverType driver, DriverType... allowed) {
        for (DriverType d : allowed) if (d == driver) return;
        throw new FeatureNotSupportedException(driver);
    }

    public static void unsupportedDriver(DriverType driver, DriverType... disallowed) {
        for (DriverType d : disallowed)
            if (d == driver)
                throw new FeatureNotSupportedException(driver);
    }
}
