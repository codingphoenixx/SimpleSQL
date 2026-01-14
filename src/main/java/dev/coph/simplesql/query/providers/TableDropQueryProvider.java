package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.DeleteMethode;
import dev.coph.simplesql.database.attributes.DropBehaviour;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simplesql.utils.QueryResult;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Provides the functionality to generate SQL statements for dropping database tables.
 * This class implements {@link QueryProvider} and is responsible for constructing
 * DROP TABLE statements based on the specified tables and configurations.
 */
public class TableDropQueryProvider implements QueryProvider {

    private ArrayList<String> tables;

    private DeleteMethode deleteMethode = DeleteMethode.DEFAULT;

    private RunnableAction<QueryResult<TableDropQueryProvider>> actionAfterQuery;

    private boolean temporary;
    private DropBehaviour behaviour = DropBehaviour.NONE;

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(tables, "tables");

        DriverType driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        StringBuilder sql = new StringBuilder("DROP ");

        if (temporary && (driver != null && driver != DriverType.POSTGRESQL)) {
            DatabaseCheck.requireDriver(driver, DriverType.MYSQL, DriverType.MARIADB);
            sql.append("TEMPORARY ");
        }

        sql.append("TABLE");

        if (deleteMethode == DeleteMethode.IF_EXISTS) {
            sql.append(" IF EXISTS");
        }

        if (tables.size() > 1) {
            DatabaseCheck.unsupportedDriver(driver, DriverType.SQLITE);
        }
        StringJoiner tj = new StringJoiner(", ", " ", "");
        for (String t : tables) {
            Check.ifNull(t, "table");
            tj.add(t);
        }
        sql.append(tj);

        if (behaviour != DropBehaviour.NONE) {
            DatabaseCheck.requireDriver(driver, DriverType.POSTGRESQL);
            sql.append(behaviour.name());
        }

        sql.append(";");
        return sql.toString();
    }

    /**
     * Specifies one or more table names to be processed by the query provider.
     * The specified table names will be added to the current list of tables for this query provider.
     *
     * @param tables an array of table names to be added
     * @return the current instance of {@code TableDropQueryProvider}, allowing for method chaining
     */
    public TableDropQueryProvider table(String... tables) {
        if (this.tables == null) this.tables = new ArrayList<>();
        this.tables.addAll(Arrays.asList(tables));
        return this;
    }

    /**
     * Sets the list of table names to be processed by the query provider.
     * Replaces any existing list of tables with the specified tables.
     *
     * @param tables an {@code ArrayList} of table names to set for this query provider
     * @return the current instance of {@code TableDropQueryProvider}, allowing for method chaining
     */
    public TableDropQueryProvider tables(ArrayList<String> tables) {
        this.tables = tables;
        return this;
    }

    /**
     * Sets the deletion method to be used by this query provider.
     * If the specified deletion method is null, the default deletion method ({@code DeleteMethode.DEFAULT}) is used.
     *
     * @param deleteMethode the deletion method to set, or {@code null} to use the default method
     * @return the current instance of {@code TableDropQueryProvider}, allowing for method chaining
     */
    public TableDropQueryProvider deleteMethode(DeleteMethode deleteMethode) {
        this.deleteMethode = deleteMethode != null ? deleteMethode : DeleteMethode.DEFAULT;
        return this;
    }

    /**
     * Sets whether the query should operate on a temporary table.
     *
     * @param temporary a boolean indicating whether the table should be temporary
     * @return the current instance of {@code TableDropQueryProvider}, allowing for method chaining
     */
    public TableDropQueryProvider temporary(boolean temporary) {
        this.temporary = temporary;
        return this;
    }

    /**
     * Defines the behavior for handling dependent records when a table is dropped.
     * This method allows specifying how foreign key constraints or related records
     * are managed during the drop operation.
     *
     * @param behaviour the {@code DropBehaviour} enumeration value indicating the
     *                  desired drop behavior. Possible values include {@code NONE},
     *                  {@code CASCADE}, and {@code RESTRICT}.
     * @return the current instance of {@code TableDropQueryProvider}, allowing
     *         for method chaining.
     */
    public TableDropQueryProvider dropBehaviour(DropBehaviour behaviour) {
        this.behaviour = behaviour;
        return this;
    }

    /**
     * Sets the action to be executed after a query is executed.
     * The specified action will perform operations based on the success or failure
     * of the query and can be used to define custom post-query behavior.
     *
     * @param actionAfterQuery the {@code RunnableAction<Boolean>} to be executed after the query.
     *                         The Boolean parameter indicates whether the query was successful.
     * @return the current instance of {@code TableDropQueryProvider}, allowing for method chaining.
     */
    public TableDropQueryProvider actionAfterQuery(RunnableAction<QueryResult<TableDropQueryProvider>> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<QueryResult<TableDropQueryProvider>> actionAfterQuery() {
        return actionAfterQuery;
    }

    /**
     * Retrieves the list of table names currently associated with this query provider.
     *
     * @return an {@code ArrayList} of table names
     */
    public ArrayList<String> tables() {
        return this.tables;
    }

    /**
     * Retrieves the current deletion method associated with this query provider.
     * The deletion method defines the strategy or behavior applied when a delete operation is executed.
     *
     * @return the current {@code DeleteMethode} in use by this query provider
     */
    public DeleteMethode deleteMethode() {
        return this.deleteMethode;
    }
}
