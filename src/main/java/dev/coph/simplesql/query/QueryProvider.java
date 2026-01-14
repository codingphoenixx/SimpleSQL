package dev.coph.simplesql.query;

import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.utils.QueryResult;
import dev.coph.simpleutilities.action.RunnableAction;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.List;

/**
 * Represents a provider for generating SQL query strings. Classes implementing
 * this interface should define how to construct and provide specific types of
 * SQL queries.
 */
public interface QueryProvider {

    /**
     * Sets the value of the specified parameter at the given index in the {@link PreparedStatement}.
     * This method determines the type of the provided value and sets it accordingly using
     * the appropriate method in the {@code PreparedStatement}.
     *
     * @param ps    the {@code PreparedStatement} in which the parameter will be set
     * @param index the index of the parameter to be set, starting at 1
     * @param value the value to be bound to the specified parameter; can be of various types such as
     *              {@code String}, {@code Integer}, {@code Long}, {@code Boolean}, etc.
     * @throws SQLException if a database access error occurs or the parameter type is not supported
     */
    static void setParam(PreparedStatement ps, int index, Object value) throws SQLException {
        if (value == null) {
            ps.setObject(index, null);
            return;
        }
        if (value instanceof String s) ps.setString(index, s);
        else if (value instanceof Integer i) ps.setInt(index, i);
        else if (value instanceof Long l) ps.setLong(index, l);
        else if (value instanceof Boolean b) ps.setBoolean(index, b);
        else if (value instanceof Double d) ps.setDouble(index, d);
        else if (value instanceof Short s) ps.setShort(index, s);
        else if (value instanceof Float f) ps.setFloat(index, f);
        else if (value instanceof BigDecimal bd) ps.setBigDecimal(index, bd);
        else if (value instanceof Blob b) ps.setBlob(index, b);
        else if (value instanceof Date d) ps.setDate(index, d);
        else if (value instanceof Time t) ps.setTime(index, t);
        else if (value instanceof Timestamp ts) ps.setTimestamp(index, ts);
        else if (value instanceof URL u) ps.setURL(index, u);
        else if (value instanceof byte[] bytes) ps.setBytes(index, bytes);
        else {
            ps.setObject(index, value);
        }
    }

    /**
     * Retrieves the {@code DriverCompatibility} instance for this {@code QueryProvider}.
     * The returned instance can be used to check compatibility with specific database driver types.
     *
     * @return the {@code DriverCompatibility} instance associated with the current implementation
     */
    DriverCompatibility compatibility();

    /**
     * Constructs and returns a SQL query string based on the provided Query object.
     * This method is responsible for converting the query's parameters,
     * conditions, and structure into a valid SQL string representation.
     *
     * @param query the Query object containing details of the SQL query to be generated
     * @return the generated SQL query string that represents the provided query
     */
    String generateSQLString(Query query);

    /**
     * Retrieves a list of parameters to be utilized in the SQL query generation
     * or execution process. These parameters are intended to be bound to a
     * PreparedStatement or analyzed for query creation.
     *
     * @return an unmodifiable list of parameters for the query
     */
    default List<Object> parameters() {
        return List.of();
    }

    /**
     * Binds the parameters from the {@link #parameters()} method to the provided {@code PreparedStatement}.
     * Each parameter is set in the {@code PreparedStatement} sequentially, starting from index 1.
     *
     * @param ps the {@code PreparedStatement} to which the parameters will be bound
     * @throws SQLException if an error occurs while setting a parameter in the {@code PreparedStatement}
     */
    default void bindParameters(PreparedStatement ps) throws SQLException {
        var params = parameters();
        for (int i = 0; i < params.size(); i++) {
            setParam(ps, i + 1, params.get(i));
        }
    }

    /**
     * Creates and returns a {@link RunnableAction} that will be executed after a database query operation.
     * The action encapsulates processing logic related to the results of the query as represented by
     * a {@link QueryResult} instance.
     *
     * @param <T> the type of the {@link QueryProvider} associated with the query
     * @return a {@link RunnableAction} containing logic to process a {@link QueryResult} object
     */
    <T extends QueryProvider> RunnableAction<QueryResult<T>> actionAfterQuery();
}
