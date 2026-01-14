package dev.coph.simplesql.utils;

import dev.coph.simplesql.query.QueryProvider;

/**
 * Represents the result of executing a database query, containing the query provider
 * responsible for generating or managing the query and a flag indicating the success
 * of the operation.
 *
 * @param <T> the type of the query provider, which must extend {@link QueryProvider}
 * @param queryProvider the query provider associated with this result
 * @param success a boolean indicating whether the query execution was successful
 */
public record QueryResult<T extends QueryProvider>(T queryProvider, boolean success) {

}
