package dev.coph.simplesql.driver;

/**
 * The {@code DriverCompatibility} interface defines a contract for checking
 * compatibility between implementations and specific database driver types.
 * Implementing classes are responsible for determining their compatibility
 * with a given {@code DriverType}.
 */
public interface DriverCompatibility {
    /**
     * Determines whether the given {@code DriverType} is compatible with the current implementation.
     *
     * @param driverType the type of database driver to be checked for compatibility
     * @return {@code true} if the specified {@code DriverType} is compatible, {@code false} otherwise
     */
    boolean isCompatible(DriverType driverType);

}
