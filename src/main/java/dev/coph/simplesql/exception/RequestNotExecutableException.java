package dev.coph.simplesql.exception;

public class RequestNotExecutableException extends RuntimeException {
    public RequestNotExecutableException(Throwable cause) {
        super("The request could not be executed.", cause);
    }

}
