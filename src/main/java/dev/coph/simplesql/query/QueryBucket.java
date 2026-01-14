package dev.coph.simplesql.query;

import java.util.ArrayList;
import java.util.List;

public class QueryBucket {

    final String sql;
    final List<QueryProvider> providers = new ArrayList<>();

    QueryBucket(String sql) {
        this.sql = sql;
    }

}
