package dev.coph.simplesql.query;

import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simpleutilities.action.RunnableAction;

/**
 * Represents a provider for generating SQL query strings. Classes implementing
 * this interface should define how to construct and provide specific types of
 * SQL queries.
 */
public interface QueryProvider {

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


    RunnableAction<Boolean> actionAfterQuery();
}
