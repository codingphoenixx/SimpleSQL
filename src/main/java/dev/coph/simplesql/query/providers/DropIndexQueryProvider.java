package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.DropBehaviour;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simplesql.utils.QueryResult;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Provides functionality for generating SQL queries to drop database indexes.
 * This class implements the QueryProvider interface and supports multiple
 * database drivers such as MySQL, MariaDB, PostgreSQL, and SQLite.
 * <p>
 * The DropIndexQueryProvider allows configuration of the SQL drop index query
 * with options such as specifying the index names, table, schema, conditional
 * dropping (if exists), concurrent operation for PostgreSQL, and cascading
 * behavior.
 */
public class DropIndexQueryProvider implements QueryProvider {

    private final List<String> indexNames = new ArrayList<>();
    private String table;
    private String schema;
    private boolean ifExists;
    private boolean concurrently;
    private DropBehaviour behaviour;

    private RunnableAction<QueryResult<DropIndexQueryProvider>> actionAfterQuery;

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(indexNames, "indexNames");
        DriverType driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        DatabaseCheck.missingDriver(driver);

        StringBuilder sql = new StringBuilder();

        switch (driver) {
            case MYSQL, MARIADB -> {
                Check.ifNullOrEmptyMap(table, "table (required for DROP INDEX in MySQL/MariaDB)");
                if (indexNames.size() != 1) {
                    throw new IllegalArgumentException("MySQL/MariaDB can drop one index per statement");
                }
                sql.append("DROP INDEX ");
                if (ifExists) {
                    sql.append("IF EXISTS ");
                }
                sql.append(indexNames.get(0)).append(" ON ").append(table).append(";");
            }
            case POSTGRESQL -> {
                sql.append("DROP INDEX ");
                if (concurrently) sql.append("CONCURRENTLY ");
                if (ifExists) sql.append("IF EXISTS ");

                StringJoiner joiner = new StringJoiner(", ");
                for (String idx : indexNames) {
                    String name = (schema != null && !schema.isBlank())
                            ? schema + "." + idx
                            : idx;
                    joiner.add(name);
                }
                sql.append(joiner);

                if (behaviour != null) {
                    sql.append(behaviour.name());
                }
                sql.append(";");
            }
            case SQLITE -> {
                if (indexNames.size() != 1) {
                    throw new IllegalArgumentException("SQLite can drop one index per statement");
                }
                sql.append("DROP INDEX ");
                if (ifExists) sql.append("IF EXISTS ");
                sql.append(indexNames.get(0)).append(";");
            }
            default -> throw new FeatureNotSupportedException(driver);
        }

        return sql.toString();
    }


    @Override
    public RunnableAction<QueryResult<DropIndexQueryProvider>> actionAfterQuery() {
        return actionAfterQuery;
    }

    /**
     * Sets the action to be executed after a query is run.
     *
     * @param actionAfterQuery the {@code RunnableAction<Boolean>} to be executed after the query,
     *                         where the {@code Boolean} parameter indicates the success or failure of the query
     * @return the {@code DropIndexQueryProvider} instance, allowing for method chaining
     */
    public DropIndexQueryProvider actionAfterQuery(RunnableAction<QueryResult<DropIndexQueryProvider>> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    /**
     * Adds the specified index name to the list of indexes to be dropped.
     * This method allows for method chaining.
     *
     * @param indexName the name of the index to be added to the drop list
     * @return the {@code DropIndexQueryProvider} instance, enabling method chaining
     */
    public DropIndexQueryProvider addIndex(String indexName) {
        this.indexNames.add(indexName);
        return this;
    }

    /**
     * Adds the specified list of index names to the list of indexes to be dropped.
     * This method allows for method chaining.
     *
     * @param indexNames the list of index names to be added to the drop list
     * @return the {@code DropIndexQueryProvider} instance, enabling method chaining
     */
    public DropIndexQueryProvider addIndexes(List<String> indexNames) {
        this.indexNames.addAll(indexNames);
        return this;
    }

    /**
     * Sets the name of the table associated with the index to be dropped.
     * This method allows for method chaining.
     *
     * @param table the name of the table associated with the index
     * @return the {@code DropIndexQueryProvider} instance, enabling method chaining
     */
    public DropIndexQueryProvider table(String table) {
        this.table = table;
        return this;
    }

    /**
     * Sets the schema associated with the index to be dropped.
     * This method allows for method chaining.
     *
     * @param schema the name of the schema associated with the index
     * @return the {@code DropIndexQueryProvider} instance, enabling method chaining
     */
    public DropIndexQueryProvider schema(String schema) {
        this.schema = schema;
        return this;
    }

    /**
     * Specifies whether the "IF EXISTS" clause should be included in the SQL DROP INDEX query.
     * This method allows for method chaining.
     *
     * @param ifExists a boolean value indicating whether the "IF EXISTS" clause should be included.
     *                 If {@code true}, the clause will be added; otherwise, it will be omitted.
     * @return the {@code DropIndexQueryProvider} instance, enabling method chaining.
     */
    public DropIndexQueryProvider ifExists(boolean ifExists) {
        this.ifExists = ifExists;
        return this;
    }

    /**
     * Specifies whether the "CONCURRENTLY" option should be included in the SQL DROP INDEX query.
     * This method allows for method chaining.
     *
     * @param concurrently a boolean value indicating whether the "CONCURRENTLY" option
     *                     should be included. If {@code true}, the option will be added;
     *                     otherwise, it will be omitted.
     * @return the {@code DropIndexQueryProvider} instance, enabling method chaining.
     */
    public DropIndexQueryProvider concurrently(boolean concurrently) {
        this.concurrently = concurrently;
        return this;
    }

    /**
     * Sets the drop behavior for handling dependent records when the index is dropped.
     * This method allows for method chaining.
     *
     * @param behaviour the {@code DropBehaviour} specifying how dependent records should be handled.
     *                  For example, {@code CASCADE} to delete dependent records, {@code RESTRICT} to
     *                  prevent deletion if there are dependents, or {@code NONE} for no action.
     * @return the {@code DropIndexQueryProvider} instance, enabling method chaining.
     */
    public DropIndexQueryProvider cascade(DropBehaviour behaviour) {
        this.behaviour = behaviour;
        return this;
    }

}
