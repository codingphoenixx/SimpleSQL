package dev.coph.simplesql.query;

/**
 * Abstract base class for query providers focused on SQL update operations.
 * Provides mechanisms to set and retrieve information about the number of rows
 * affected by an update query. Concrete implementations can extend this class
 * to define specific SQL update behaviors and ensure compatibility with
 * their respective query requirements.
 * <p>
 * This class implements the {@code QueryProvider} interface and assumes that
 * extending classes will provide implementations for the methods required by
 * the interface.
 */
public abstract class UpdateingQueryProvider implements QueryProvider {

    private int affectedRows;

    /**
     * Retrieves the number of rows affected by the execution of the query.
     *
     * @return the number of rows affected by the update operation.
     */
    public int affectedRows() {
        return affectedRows;
    }

    /**
     * Sets the number of rows affected by the execution of the update query.
     * This method allows chaining by returning the current instance.
     *
     * @param affectedRows the number of rows affected by the update operation.
     * @return the current instance of {@code UpdateingQueryProvider}.
     */
    public UpdateingQueryProvider affectedRows(int affectedRows) {
        this.affectedRows = affectedRows;
        return this;
    }
}
