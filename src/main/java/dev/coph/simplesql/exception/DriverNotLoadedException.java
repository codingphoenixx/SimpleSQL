package dev.coph.simplesql.exception;

/**
 * Thrown to indicate that the database driver could not be loaded.
 * <p>
 * This exception typically occurs when the JDBC driver class specified
 * by the {@code DriverType} enumeration cannot be found or loaded during an
 * attempt to establish a connection to the database.
 * <p>
 * This is a runtime exception and is usually caused by misconfiguration, such as
 * the JDBC driver not being present in the classpath.
 */
public class DriverNotLoadedException extends RuntimeException {

    /**
     * Constructs a new {@code DriverNotLoadedException} with the specified cause.
     * This exception indicates that the database driver could not be loaded and
     * commonly occurs when the driver class is not present in the classpath or
     * cannot be found during runtime.
     *
     * @param cause the underlying cause of the exception, typically a {@link ClassNotFoundException}
     *              or another related exception that occurred while attempting to load the driver
     */
    public DriverNotLoadedException(Throwable cause) {
        super("The Database Driver could not be loaded.", cause);
    }
}
