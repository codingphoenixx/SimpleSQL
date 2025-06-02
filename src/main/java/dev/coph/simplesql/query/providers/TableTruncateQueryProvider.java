package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.check.Check;
import lombok.experimental.Accessors;


/**
 * A query provider implementation designed to generate SQL statements
 * that truncate database tables.
 * <p>
 * The {@code TRUNCATE TABLE} command quickly deletes all rows from a table, resets auto-increment values, and retains the table structure.
 * Not working with {@code DriverType.SQLITE}
 */
public class TableTruncateQueryProvider implements QueryProvider {
    /**
     * The name of the database table to be truncated.
     * This property is expected to be set before executing the SQL query generation.
     */
    private String table;

    @Override
    public String generateSQLString(Query query) {
        if (query.databaseAdapter() != null && query.databaseAdapter().driverType() == DatabaseAdapter.DriverType.SQLITE) {
            try {
                throw new UnsupportedOperationException("SQLite does not support truncate. Ignoring attribute...");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
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
     * @return the current instance of {@code TableTruncateQueryProvider} for method chaining
     */
    public TableTruncateQueryProvider table(String table) {
        this.table = table;
        return this;
    }
}
