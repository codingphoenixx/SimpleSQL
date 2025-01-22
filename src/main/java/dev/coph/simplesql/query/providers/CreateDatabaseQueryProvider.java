package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;

public class CreateDatabaseQueryProvider implements QueryProvider {
    @Override
    public String generateSQLString(Query query) {
        if(query.databaseAdapter() != null && query.databaseAdapter().driverType() == DatabaseAdapter.DriverType.SQLITE){
            throw new UnsupportedOperationException("SQLite does not support different Databases.");
        }
        return "";
    }

    //TODO: NOT WORK
}
