package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.check.Check;

/**
 * Abstract class representing the structure for constructing SQL ALTER TABLE queries.
 * Provides a base implementation for generating SQL strings to modify table structures
 * and compatibility assessment for the database driver.
 */
public abstract class TableAlterQueryProvider implements QueryProvider {

    protected String table;

    /**
     * Constructs the SQL string for altering a table based on the provided query details.
     *
     * @param query the Query object containing the details for the table alteration.
     * @return the SQL string representing the table alteration command.
     */
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
