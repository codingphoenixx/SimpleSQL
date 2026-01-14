package dev.coph.simplesql.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a container for a SQL query and its associated providers.
 * The class is used to store and manage a SQL query string and a collection
 * of {@code QueryProvider} instances responsible for query generation and execution logic.
 */
public class QueryBucket {

    final String sql;
    final List<QueryProvider> providers = new ArrayList<>();

    /**
     * Constructs a QueryBucket instance with the specified SQL query string.
     *
     * @param sql the SQL query string to be associated with this QueryBucket instance.
     */
    QueryBucket(String sql) {
        this.sql = sql;
    }

}
