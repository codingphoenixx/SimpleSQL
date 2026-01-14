package dev.coph.simplesql.utils;

import dev.coph.simplesql.query.QueryProvider;

public class QueryResult<T extends QueryProvider> {

    private final T queryProvider;
    private final boolean success;


    public QueryResult(T queryProvider, boolean success) {
        this.queryProvider = queryProvider;
        this.success = success;
    }



    public boolean success() {
        return success;
    }

    public T queryProvider() {
        return queryProvider;
    }
}
