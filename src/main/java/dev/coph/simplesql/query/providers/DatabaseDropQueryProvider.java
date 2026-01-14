package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.DeleteMethode;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simplesql.utils.QueryResult;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

/**
 * A provider class that generates SQL queries for dropping databases. This class
 * implements the {@link QueryProvider} interface and provides functionality for
 * constructing {@code DROP DATABASE} SQL statements. It supports configuring
 * deletion methods and executing actions after the query execution.
 */
public class DatabaseDropQueryProvider implements QueryProvider {

    private String database;

    private DeleteMethode deleteMethode = DeleteMethode.DEFAULT;

    private RunnableAction<QueryResult<DatabaseDropQueryProvider>> actionAfterQuery;


    @Override
    public DriverCompatibility compatibility() {
        return driverType -> driverType != DriverType.SQLITE;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNull(database, "database name");

        DriverType driver =
                query.databaseAdapter() != null ? query.databaseAdapter().driverType() : null;

        DatabaseCheck.missingDriver(driver);
        DatabaseCheck.unsupportedDriver(driver, DriverType.SQLITE);


        StringBuilder sql = new StringBuilder();

        sql.append("DROP DATABASE ");
        if (deleteMethode == DeleteMethode.IF_EXISTS) {
            sql.append("IF EXISTS ");
        }
        sql.append(database);
        sql.append(";");


        return sql.toString();
    }


    @Override
    public RunnableAction<QueryResult<DatabaseDropQueryProvider>> actionAfterQuery() {
        return actionAfterQuery;
    }

    /**
     * Sets the action to be executed after the query is run.
     *
     * @param actionAfterQuery the {@code RunnableAction<Boolean>} to be executed post-query,
     *                         where the Boolean parameter represents the success or failure of the query
     * @return the current instance of {@code DatabaseDropQueryProvider} for method chaining
     */
    public DatabaseDropQueryProvider actionAfterQuery(RunnableAction<QueryResult<DatabaseDropQueryProvider>> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    /**
     * Sets the name of the database to be dropped.
     *
     * @param database the name of the database to drop
     * @return the current instance of {@code DatabaseDropQueryProvider} for method chaining
     */
    public DatabaseDropQueryProvider database(String database) {
        this.database = database;
        return this;
    }

    /**
     * Configures the deletion method to be used when generating SQL queries for dropping databases.
     * If the provided {@code deleteMethode} parameter is null, the default deletion method
     * ({@code DeleteMethode.DEFAULT}) is used.
     *
     * @param deleteMethode the deletion method to be used. Accepts an instance of {@code DeleteMethode}:
     *                      {@code IF_EXISTS} for conditional deletion if the database exists, or
     *                      {@code DEFAULT} for unguarded deletion.
     * @return the current instance of {@code DatabaseDropQueryProvider} for method chaining
     */
    public DatabaseDropQueryProvider deleteMethode(DeleteMethode deleteMethode) {
        this.deleteMethode = deleteMethode != null ? deleteMethode : DeleteMethode.DEFAULT;
        return this;
    }

    /**
     * Retrieves the name of the database associated with this query provider.
     *
     * @return the name of the database as a {@code String}
     */
    public String database() {
        return this.database;
    }

    /**
     * Retrieves the current deletion method configured in the DatabaseDropQueryProvider.
     * The deletion method defines the behavior or conditions to be applied during
     * database delete operations.
     *
     * @return the configured {@code DeleteMethode} for this instance, which may be
     * one of the predefined strategies such as {@code DEFAULT} or {@code IF_EXISTS}.
     */
    public DeleteMethode deleteMethode() {
        return this.deleteMethode;
    }

}
