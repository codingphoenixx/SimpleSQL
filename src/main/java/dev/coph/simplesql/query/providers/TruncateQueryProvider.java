package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;


/**
 * A query provider implementation designed to generate SQL statements
 * that truncate database tables.
 * <p>
 * The {@code TRUNCATE TABLE} command quickly deletes all rows from a table, resets auto-increment values, and retains the table structure.
 * Not working with {@code DriverType.SQLITE}
 */
public class TruncateQueryProvider implements QueryProvider {
    /**
     * The name of the database table to be truncated.
     * This property is expected to be set before executing the SQL query generation.
     */
    private String table;
    private RunnableAction<Boolean> actionAfterQuery;

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> driverType != DriverType.SQLITE;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNull(table, "table name");
        return "TRUNCATE TABLE %s;".formatted(table);
    }

    /**
     * Retrieves the name of the database table to be truncated.
     *
     * @return the name of the table as a {@code String}
     */
    public String table() {
        return this.table;
    }

    /**
     * Sets the name of the database table to be truncated.
     *
     * @param table the name of the table to be truncated
     * @return the current instance of {@code TruncateQueryProvider} for method chaining
     */
    public TruncateQueryProvider table(String table) {
        this.table = table;
        return this;
    }

    public TruncateQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }
}
