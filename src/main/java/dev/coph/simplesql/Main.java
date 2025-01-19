package dev.coph.simplesql;

import dev.coph.simplesql.query.Query;

public class Main {
    public static void main(String[] args) {

        Query query = new Query(null);

        query.queries(SELECT);
        query.execute();

    }

}
