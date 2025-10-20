package dev.coph.simplesql.query;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A wrapper class for {@link ResultSet} that provides additional utility methods
 * to navigate and process the result set with custom functional interfaces.
 * This class is designed to simplify result set iteration and processing operations
 * using functional programming patterns.
 */
public record SimpleResultSet(ResultSet resultSet) {

    /**
     * Advances the underlying ResultSet to the next row and applies the given consumer to it.
     *
     * @param consumer a functional interface to process the ResultSet when it advances to the next row
     * @return the current SimpleResultSet instance for method chaining
     * @throws SQLException if an SQL error occurs during result set navigation or consumer execution
     */
    public SimpleResultSet next(ResultSetConsumer consumer) throws SQLException {
        if (resultSet.next())
            consumer.accept(resultSet);
        return this;
    }

    /**
     * Advances the underlying ResultSet to the next row and applies the given consumer to it.
     * If the consumer throws an exception, the exceptionConsumer is invoked with the thrown exception.
     *
     * @param consumer          a functional interface to process the ResultSet when it advances to the next row
     * @param exceptionConsumer a functional interface to handle exceptions thrown by the consumer
     * @return the current SimpleResultSet instance for method chaining
     * @throws SQLException if an SQL error occurs during result set navigation
     */
    public SimpleResultSet next(ResultSetConsumer consumer, ExceptionConsumer exceptionConsumer) throws SQLException {
        if (resultSet.next()) {
            try {
                consumer.accept(resultSet);
            } catch (Throwable e) {
                exceptionConsumer.accept(e);
            }
        }
        return this;
    }

    /**
     * Advances the underlying ResultSet to the next row and applies the given consumer to it.
     * If no rows are available, the emptyConsumer is invoked.
     *
     * @param consumer      a functional interface to process the ResultSet when it advances to the next row
     * @param emptyConsumer a functional interface to handle the case when there are no more rows in the ResultSet
     * @return the current SimpleResultSet instance for method chaining
     * @throws SQLException if an SQL error occurs during result set navigation or consumer execution
     */
    public SimpleResultSet next(ResultSetConsumer consumer, EmptyResultSetConsumer emptyConsumer) throws SQLException {
        if (resultSet.next())
            consumer.accept(resultSet);
        else
            emptyConsumer.accept();
        return this;
    }

    /**
     * Advances the underlying ResultSet to the next row and processes it using the provided consumer.
     * If no rows are available, the emptyConsumer is invoked. If an exception occurs, the exceptionConsumer
     * is invoked with the thrown exception.
     *
     * @param consumer          a functional interface to process the ResultSet when it advances to the next row
     * @param emptyConsumer     a functional interface to handle the case when there are no more rows in the ResultSet
     * @param exceptionConsumer a functional interface to handle exceptions thrown during result set navigation or consumer execution
     * @return the current SimpleResultSet instance for method chaining
     * @throws SQLException if an SQL error occurs during result set navigation
     */
    public SimpleResultSet next(ResultSetConsumer consumer, EmptyResultSetConsumer emptyConsumer, ExceptionConsumer exceptionConsumer) throws SQLException {
        try {
            if (resultSet.next())
                consumer.accept(resultSet);
            else
                emptyConsumer.accept();
        } catch (Exception e) {
            exceptionConsumer.accept(e);
        }
        return this;
    }

    /**
     * Iterates over the rows of the underlying ResultSet and applies the given consumer
     * to each row in sequence. If the consumer throws an exception, it is caught and
     * printed using the stack trace.
     *
     * @param consumer a functional interface to process each row of the ResultSet
     * @return the current SimpleResultSet instance for method chaining
     * @throws SQLException if an SQL error occurs while navigating the ResultSet
     */
    public SimpleResultSet forEach(ResultSetConsumer consumer) throws SQLException {

        while (resultSet.next()) {
            try {
                consumer.accept(resultSet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * Iterates over the rows of the underlying ResultSet and applies the given consumer
     * to process each row. If an exception occurs during the consumer's execution, the
     * exceptionConsumer is invoked.
     *
     * @param consumer          a functional interface to process each row of the ResultSet
     * @param exceptionConsumer a functional interface to handle exceptions thrown by the consumer
     * @return the current SimpleResultSet instance for method chaining
     * @throws SQLException if an SQL error occurs while navigating the ResultSet
     */
    public SimpleResultSet forEach(ResultSetConsumer consumer, ExceptionConsumer exceptionConsumer) throws SQLException {

        while (resultSet.next()) {
            try {
                consumer.accept(resultSet);
            } catch (Exception e) {
                exceptionConsumer.accept(e);
            }
        }

        return this;
    }

    /**
     * Iterates over the rows of the underlying ResultSet and applies the given consumer
     * to each row in sequence. If no rows are available, the emptyConsumer is invoked.
     * If an exception is thrown by the consumer, the exception is caught and its stack trace is printed.
     *
     * @param consumer      a functional interface that processes each row of the ResultSet
     * @param emptyConsumer a functional interface that is invoked if there are no rows in the ResultSet
     * @return the current SimpleResultSet instance for method chaining
     * @throws SQLException if an SQL error occurs during ResultSet navigation or consumer execution
     */
    public SimpleResultSet forEach(ResultSetConsumer consumer, EmptyResultSetConsumer emptyConsumer) throws SQLException {
        int count = 0;

        while (resultSet.next()) {
            try {
                consumer.accept(resultSet);
            } catch (Exception e) {
                e.printStackTrace();
            }
            count++;
        }

        if (count == 0)
            emptyConsumer.accept();
        return this;
    }

    /**
     * Iterates over the rows of the underlying ResultSet and applies the given consumer
     * to each row in sequence. If no rows are available, the emptyConsumer is invoked.
     * If an exception occurs during the consumer's execution, the exceptionConsumer is invoked
     * with the thrown exception.
     *
     * @param consumer          a functional interface to process each row of the ResultSet
     * @param emptyConsumer     a functional interface that is invoked if there are no rows in the ResultSet
     * @param exceptionConsumer a functional interface to handle exceptions thrown during result set navigation
     *                          or consumer execution
     * @return the current SimpleResultSet instance for method chaining
     * @throws SQLException if an SQL error occurs during ResultSet navigation or consumer execution
     */
    public SimpleResultSet forEach(ResultSetConsumer consumer, EmptyResultSetConsumer emptyConsumer, ExceptionConsumer exceptionConsumer) throws SQLException {
        int count = 0;

        while (resultSet.next()) {
            try {
                consumer.accept(resultSet);
            } catch (Exception e) {
                exceptionConsumer.accept(e);
            }
            count++;
        }

        if (count == 0)
            emptyConsumer.accept();
        return this;
    }

    /**
     * Moves the underlying ResultSet to its initial position, before the first row.
     * If the ResultSet is already in a position before the first row, no action is taken.
     *
     * @return the current SimpleResultSet instance for method chaining
     * @throws SQLException if an SQL error occurs while repositioning the ResultSet
     */
    public SimpleResultSet toBeginning() throws SQLException {
        if (resultSet.isBeforeFirst())
            return this;
        resultSet.beforeFirst();
        return this;
    }

    /**
     * Determines if the underlying ResultSet is empty.
     * If the ResultSet is null or has no rows, this method returns true.
     * The method ensures that the ResultSet's cursor position is restored
     * to its original position after checking for emptiness.
     *
     * @return true if the ResultSet is empty or null; false otherwise
     * @throws SQLException if an SQL error occurs while navigating the ResultSet
     */
    public boolean isEmpty() throws SQLException {
        if (resultSet == null) {
            return true;
        }

        int currentRow = resultSet.getRow();
        boolean wasBeforeFirst = resultSet.isBeforeFirst();
        boolean wasAfterLast = resultSet.isAfterLast();

        boolean isEmpty = !resultSet.first();

        if (isEmpty) {
            resultSet.beforeFirst();
        } else {
            if (wasBeforeFirst) {
                resultSet.beforeFirst();
            } else if (wasAfterLast) {
                resultSet.afterLast();
            } else if (currentRow > 0) {
                resultSet.absolute(currentRow);
            } else {
                resultSet.beforeFirst();
            }
        }

        return isEmpty;
    }

    /**
     * A functional interface used to define an action that is executed when an underlying ResultSet
     * contains no rows. This is commonly used as a handler for empty ResultSets in database operations.
     * <p>
     * This interface provides a single abstract method, {@link #accept()}, which can be implemented
     * to define custom logic to execute when the ResultSet does not provide any rows for processing.
     * <p>
     * This interface is typically employed in conjunction with APIs that iterate over database
     * ResultSets, allowing for the specification of behavior in cases where the ResultSet is empty.
     */
    public interface EmptyResultSetConsumer {
        /**
         * Defines the action to be executed when a ResultSet is empty.
         *
         * The accept method is designed to handle the scenario where a database query yields no results.
         * Implementations of this method can provide custom logic to handle such cases, ensuring appropriate
         * behavior when there are no rows available in the underlying ResultSet.
         *
         * @throws SQLException if a database access error occurs
         */
        void accept() throws SQLException;
    }

    /**
     * Functional interface to handle exceptions occurring during the execution of
     * operations on a ResultSet.
     * <p>
     * This interface is typically used to provide custom exception handling logic
     * when processing rows in a result set, such as logging the error or performing
     * custom recovery actions. Implementations of this interface are usually passed
     * to methods of the SimpleResultSet class that support exception handling.
     */
    public interface ExceptionConsumer {
        /**
         * Performs an operation on the given throwable, typically involving exception handling logic.
         *
         * @param e the throwable to be processed, representing the exception that occurred during execution
         */
        void accept(Throwable e);
    }

    /**
     * A functional interface intended for processing a {@link ResultSet}.
     * This interface is designed to allow operations on the current row of a {@link ResultSet}.
     * Implementations of this interface should define the behavior to process a {@link ResultSet},
     * especially when used in iteration or navigation operations.
     * <p>
     * It is often used in conjunction with methods that advance or iterate over rows in
     * a {@link ResultSet} and need to perform operations on each row.
     * <p>
     * Methods that use this interface may throw {@link SQLException} if an SQL-related
     * error occurs during result set handling.
     */
    public interface ResultSetConsumer {
        /**
         * Processes the given {@link ResultSet}, typically for performing operations
         * on the current row of the result set. Implementations of this method are
         * responsible for defining how the {@link ResultSet} is handled.
         *
         * @param resultSet the {@link ResultSet} to be processed; must not be null
         * @throws SQLException if an SQL error occurs during result set processing
         */
        void accept(ResultSet resultSet) throws SQLException;
    }
}
