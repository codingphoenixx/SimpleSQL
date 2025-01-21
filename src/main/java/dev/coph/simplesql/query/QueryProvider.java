package dev.coph.simplesql.query;

/**
 * Represents a provider for generating SQL query strings. Classes implementing
 * this interface should define how to construct and provide specific types of
 * SQL queries.
 */
public interface QueryProvider {

    String generateSQLString();

}
