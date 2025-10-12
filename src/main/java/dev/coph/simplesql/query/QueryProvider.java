package dev.coph.simplesql.query;

import dev.coph.simplesql.driver.DriverCompatibility;
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

    default List<Object> parameters() {
        return List.of();
    }

    default void bindParameters(PreparedStatement ps) throws SQLException {
        var params = parameters();
        for (int i = 0; i < params.size(); i++) {
            setParam(ps, i + 1, params.get(i));
        }
    }

    RunnableAction<Boolean> actionAfterQuery();
}
