package dev.coph.simplesql.exception;

/**
 * An exception thrown to indicate that a request could not be executed.
 *
 * This runtime exception is typically used in scenarios where an operation
 * cannot proceed due to unforeseen constraints or issues during execution.
 * The underlying cause of the exception is provided to describe the specific
 * reason for the failure.
 */
public class RequestNotExecutableException extends RuntimeException {

    /**
     * Constructs a new {@code RequestNotExecutableException} with the specified cause.
     * This exception indicates that a request could not be executed, and it is primarily
     * intended to wrap the underlying cause of the failure.
     *
     * @param cause the underlying cause of the exception, typically an exception
     *              encountered during the execution of a database request or operation
     */
    public RequestNotExecutableException(Throwable cause) {
        super("The request could not be executed.", cause);
    }

}
