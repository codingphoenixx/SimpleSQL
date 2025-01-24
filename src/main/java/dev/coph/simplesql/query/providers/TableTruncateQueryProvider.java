package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * A query provider implementation designed to generate SQL statements
 * that truncate database tables.
 *
 * The {@code TRUNCATE TABLE} command quickly deletes all rows from a table, resets auto-increment values, and retains the table structure.
 */
@Getter
@Accessors(fluent = true, chain = true)
public class TableTruncateQueryProvider implements QueryProvider {
    /**
     * The name of the database table to be truncated.
     * This property is expected to be set before executing the SQL query generation.
     */
    @Setter
    private String table;

    @Override
    public String generateSQLString(Query query) {
        if (query.databaseAdapter() != null && query.databaseAdapter().driverType() == DatabaseAdapter.DriverType.SQLITE) {
            try {
                throw new UnsupportedOperationException("SQLite does not support different Databases. Ignoring attribute...");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
        Check.ifNull(table, "table name");
        return "TRUNCATE TABLE %s;".formatted(table);
    }
}
