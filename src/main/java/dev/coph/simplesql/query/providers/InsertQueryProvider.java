package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.InsertMethode;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryEntry;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * A class that provides functionality to build and generate SQL INSERT queries.
 * It implements the {@code QueryProvider} interface, allowing for customizable
 * and parameterized query generation based on different database adapter types.
 */
public class InsertQueryProvider implements QueryProvider {

    private String table;

    private List<QueryEntry> entries;

    private RunnableAction<Boolean> actionAfterQuery;

    private InsertMethode insertMethode = InsertMethode.INSERT;
    private List<String> conflictColumns;
    private List<Object> boundParams = List.of();

    /**
     * Adds a new column-value pair to the insert query.
     * This method allows chaining to add multiple entries.
     *
     * @param column the name of the column to which the value will be assigned
     * @param value  the value to be assigned to the specified column
     * @return the current {@code InsertQueryProvider} instance for method chaining
     */
    public InsertQueryProvider entry(String column, Object value) {
        if (entries == null) {
            entries = new ArrayList<>();
        }
        entries.add(new QueryEntry(column, value));
        return this;
    }

    /**
     * Sets the action to be executed after the query is run.
     *
     * @param actionAfterQuery the {@code RunnableAction<Boolean>} to be performed after the query execution,
     *                         where the Boolean parameter represents the success or failure of the query
     * @return the current {@code InsertQueryProvider} instance for method chaining
     */
    public InsertQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(entries, "entries");
        Check.ifNullOrEmptyMap(table, "tablename");

        List<Object> params = new ArrayList<>();
        DriverType driver = query.databaseAdapter() != null ? query.databaseAdapter().driverType() : null;

        StringBuilder sql = new StringBuilder();

        sql.append("INSERT ");

        if (insertMethode.equals(InsertMethode.INSERT_IGNORE)) {
            if (driver == DriverType.MYSQL || driver == DriverType.MARIADB) {
                sql.append("IGNORE ");
            } else if (driver == DriverType.SQLITE) {
                sql.append("OR IGNORE ");
            }
        }


        sql.append("INTO ").append(table);

        StringJoiner colJoin = new StringJoiner(", ");
        for (QueryEntry e : entries) {
            colJoin.add(e.columName());
        }
        sql.append(" (").append(colJoin).append(") ");

        StringJoiner phJoin = new StringJoiner(", ");
        for (QueryEntry e : entries) {
            phJoin.add("?");
            params.add(e.value());
        }
        sql.append("VALUES (").append(phJoin).append(")");


        if (insertMethode == InsertMethode.INSERT_OR_UPDATE) {
            if (driver == DriverType.MYSQL || driver == DriverType.MARIADB) {
                sql.append(" ON DUPLICATE KEY UPDATE ");
                StringJoiner updJoin = new StringJoiner(", ");
                for (QueryEntry e : entries) {
                    updJoin.add(e.columName() + " = VALUES(" + e.columName() + ")");
                }
                sql.append(updJoin);
            } else if (driver == DriverType.POSTGRESQL || driver == DriverType.SQLITE) {
                Check.ifNullOrEmptyMap(conflictColumns, "conflictColumns");
                sql.append(" ON CONFLICT (");
                sql.append(String.join(", ", conflictColumns));
                sql.append(") DO UPDATE SET ");
                StringJoiner updJoin = new StringJoiner(", ");
                for (QueryEntry e : entries) {
                    String excluded = (driver == DriverType.POSTGRESQL) ? "EXCLUDED" : "excluded";
                    updJoin.add(e.columName() + " = " + excluded + "." + e.columName());
                }
                sql.append(updJoin);
            }
        } else if (insertMethode == InsertMethode.INSERT_IGNORE) {
            if (driver == DriverType.POSTGRESQL && conflictColumns != null && !conflictColumns.isEmpty()) {
                sql.append(" ON CONFLICT (").append(String.join(", ", conflictColumns)).append(") DO NOTHING");
            }
        }

        sql.append(";");

        this.boundParams = List.copyOf(params);
        return sql.toString();
    }
//TODO: Update only columns
    @Override
    public List<Object> parameters() {
        return boundParams != null ? boundParams : List.of();
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }

    /**
     * Sets the columns to be used for conflict resolution in the insert query.
     *
     * @param cols a list of column names to be included for conflict resolution.
     *             If the provided list is null or empty, no conflict columns will be considered.
     * @return the current {@code InsertQueryProvider} instance for method chaining.
     */
    public InsertQueryProvider conflictColumns(List<String> cols) {
        if (cols == null || cols.isEmpty()) {
            this.conflictColumns = null;
        } else {
            this.conflictColumns = new ArrayList<>(cols);
        }
        return this;
    }

    /**
     * Retrieves the name of the table associated with this query provider.
     *
     * @return the table name as a String
     */
    public String table() {
        return this.table;
    }

    /**
     * Retrieves the list of {@code QueryEntry} objects associated with the current
     * {@code InsertQueryProvider}. These entries define the column-value pairs
     * to be used in the insert query.
     *
     * @return a {@code List} of {@code QueryEntry} objects representing the column-value pairs
     */
    public List<QueryEntry> entries() {
        return this.entries;
    }

    /**
     * Retrieves the current {@code InsertMethode} configuration.
     * This defines the strategy for the SQL INSERT operation, such as
     * standard insertion, insertion with conflict handling, or ignoring duplicates.
     *
     * @return the {@code InsertMethode} currently set for this query provider
     */
    public InsertMethode insertMethode() {
        return this.insertMethode;
    }

    /**
     * Sets the name of the table to be used in the query.
     *
     * @param table the name of the table as a String
     * @return the current {@code InsertQueryProvider} instance for method chaining
     */
    public InsertQueryProvider table(String table) {
        this.table = table;
        return this;
    }

    /**
     * Sets the {@code InsertMethode} to be used for this query provider.
     * The {@code InsertMethode} defines the strategy for the SQL INSERT operation,
     * such as standard insertion, insertion with conflict handling, or ignoring duplicates.
     *
     * @param insertMethode the {@code InsertMethode} to be set for this query provider
     * @return the current {@code InsertQueryProvider} instance for method chaining
     */
    public InsertQueryProvider insertMethode(InsertMethode insertMethode) {
        this.insertMethode = insertMethode;
        return this;
    }
}
