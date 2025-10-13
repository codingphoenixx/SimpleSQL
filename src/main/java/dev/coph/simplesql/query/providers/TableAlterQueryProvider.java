package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.check.Check;

public abstract class TableAlterQueryProvider implements QueryProvider {

    protected String table;


    public abstract String getAlterTableString(Query query);

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNull(table, "table name");
        return "ALTER TABLE " + table + " " + getAlterTableString(query) + ";";
    }


    public String table() {
        return this.table;
    }

    public TableAlterQueryProvider table(String table) {
        this.table = table;
        return this;
    }
}
