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

/**
 * A class that provides functionality for generating SQL `TRUNCATE` statements
 * for various database systems. This class implements the {@link QueryProvider}
 * interface, enabling it to define and customize `TRUNCATE TABLE` operations
 * with additional options such as identity modes and drop behavior.
 */
public class TruncateQueryProvider implements QueryProvider {

    private String table;
    private RunnableAction<QueryResult<TruncateQueryProvider>> actionAfterQuery;
    private IdentityMode identityMode;
    private DropBehaviour behaviour = DropBehaviour.NONE;

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> driverType != DriverType.SQLITE;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNull(table, "table name");

        DriverType driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        DatabaseCheck.missingDriver(driver);
        DatabaseCheck.unsupportedDriver(driver, DriverType.SQLITE);

        StringBuilder sql = new StringBuilder();

        switch (driver) {
            case MYSQL, MARIADB -> {
                if (identityMode != null || behaviour != DropBehaviour.NONE) {
                    throw new FeatureNotSupportedException(driver);
                }
                sql.append("TRUNCATE TABLE ").append(table).append(";");
            }
            case POSTGRESQL -> {
                sql.append("TRUNCATE TABLE ").append(table);
                if (identityMode == IdentityMode.RESTART) {
                    sql.append(" RESTART IDENTITY");
                } else if (identityMode == IdentityMode.CONTINUE) {
                    sql.append(" CONTINUE IDENTITY");
                }
                if (behaviour != null) {
                    sql.append(behaviour.name());
                }
                sql.append(";");
            }
            default -> {
                throw new FeatureNotSupportedException(driver);
            }
        }

        return sql.toString();
    }

    /**
     * Sets the name of the table to be used by the TruncateQueryProvider.
     *
     * @param table the name of the table as a String
     * @return the current instance of TruncateQueryProvider for method chaining
     */
    public TruncateQueryProvider table(String table) {
        this.table = table;
        return this;
    }

    /**
     * Configures the identity mode for the truncate operation. The identity mode determines
     * how identity columns in the database table behave after the table is truncated.
     *
     * @param mode the {@code IdentityMode} to set, either {@code RESTART} to reset the identity
     *             value to its seed or {@code CONTINUE} to maintain the current identity value
     *             without resetting.
     * @return the current instance of {@code TruncateQueryProvider} for method chaining
     */
    public TruncateQueryProvider identityMode(IdentityMode mode) {
        this.identityMode = mode;
        return this;
    }

    /**
     * Configures the drop behaviour for the truncate operation. The drop behaviour determines
     * how dependent records are handled when a record in the database is removed, such as
     * applying cascading deletions, restricting the operation, or taking no action.
     *
     * @param behaviour the {@code DropBehaviour} to set, defining the handling of dependent records.
     *                  Possible values include {@code NONE} for no action, {@code CASCADE} for
     *                  cascading deletions, and {@code RESTRICT} to enforce referential integrity.
     * @return the current instance of {@code TruncateQueryProvider} for method chaining.
     */
    public TruncateQueryProvider dropBehaviour(DropBehaviour behaviour) {
        this.behaviour = behaviour;
        return this;
    }

    /**
     * Sets the action to be executed after the query is run. The action is defined
     * as a {@code RunnableAction<Boolean>}, where the Boolean parameter represents
     * the success or failure of the query execution.
     *
     * @param actionAfterQuery the action to execute after the query, represented as
     *                         a {@code RunnableAction<Boolean>}
     * @return the current instance of {@code TruncateQueryProvider} for method chaining
     */
    public TruncateQueryProvider actionAfterQuery(RunnableAction<QueryResult<TruncateQueryProvider>> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<QueryResult<TruncateQueryProvider>> actionAfterQuery() {
        return actionAfterQuery;
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
     * Retrieves the current identity mode configured for this instance.
     *
     * @return the IdentityMode enumeration representing the configured identity mode,
     *         either {@code RESTART} or {@code CONTINUE}.
     */
    public IdentityMode identityMode() {
        return this.identityMode;
    }

    /**
     * Retrieves the drop behaviour configuration for this instance. The drop behaviour
     * defines how dependent records should be handled when a parent record is removed,
     * such as performing no action, cascading deletion, or restricting the operation
     * based on existing constraints.
     *
     * @return the {@code DropBehaviour} enumeration representing the configured drop behaviour
     */
    public DropBehaviour behaviour() {
        return this.behaviour;
    }

    /**
     * The IdentityMode enum represents the behavior of an identity column in a database
     * table when a truncate operation is performed. Identity columns are typically used
     * to auto-generate unique values for each row.
     * <p>
     * This enum defines two modes:
     * - RESTART: Resets the identity value to its original seed upon truncation.
     * - CONTINUE: Keeps the current identity value without resetting it after truncation.
     * <p>
     * These modes allow for fine-grained control over how identity columns behave
     * in conjunction with truncate operations, depending on the database's capabilities
     * and the application's requirements.
     */
    public enum IdentityMode {
        /**
         * Represents the mode where the identity value of a database column is
         * reset to its original seed upon a truncate operation.
         * <p>
         * This behavior is useful for scenarios where the application requires
         * a fresh set of identity values, starting from the initial seed after
         * truncating the table's content.
         */
        RESTART,
        /**
         * Represents the mode where the identity value of a database column continues
         * incrementing from its current value, without being reset, after a truncate
         * operation is performed on the table.
         * <p>
         * This behavior is useful for scenarios where the application requires the identity
         * values to remain consistent and avoid restarting, preserving the sequence of
         * auto-generated values.
         */
        CONTINUE
    }

}
