package dev.coph.simplesql;

import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;

public class Main {
    public static void main(String[] args) {
        QueryProvider provider = Query.select();

        new Query(null).queries(provider).execute();

    }

}
