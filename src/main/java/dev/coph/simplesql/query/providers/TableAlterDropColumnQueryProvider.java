package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

/**
 * A concrete implementation of {@link TableAlterQueryProvider} for generating SQL queries
 * to drop columns or primary keys from database tables. This class supports different database
 * drivers and ensures the compatibility of the generated SQL queries.
 * <p>
 * The main functionality includes:
 * - Dropping columns from a table.
 * - Dropping primary keys from a table.
 * - Driver-specific SQL syntax generation for the "ALTER TABLE" operation.
 * <p>
 * Note that this provider uses compatible drivers as per the implementation and throws exceptions
 * for unsupported drivers.
 */
public class TableAlterDropColumnQueryProvider extends TableAlterQueryProvider {

    private DropType dropType;
    private String dropObjectName;
    private String constraintName;
    private RunnableAction<Boolean> actionAfterQuery;

    @Override
    public String getAlterTableString(Query query) {
        Check.ifNull(dropType, "dropType");

        DriverType driver =
                query.databaseAdapter() != null ? query.databaseAdapter().driverType() : null;

        DatabaseCheck.missingDriver(driver);
        DatabaseCheck.unsupportedDriver(driver, DriverType.SQLITE);

        return switch (dropType) {
            case PRIMARY_KEY -> dropPrimaryKey(query, driver);
            case COLUMN -> dropColumn(query, driver);
        };
    }

    /**
     * Generates the SQL command to drop a primary key depending on the database driver type.
     *
     * @param query  the query object containing additional context or details for the operation
     * @param driver the database driver type for which the SQL command is being generated
     * @return the SQL command as a string to drop the primary key
     * @throws FeatureNotSupportedException if the operation is not supported for the given driver type
     */
    private String dropPrimaryKey(Query query, DriverType driver) {
        return switch (driver) {
            case MYSQL, MARIADB -> "DROP PRIMARY KEY";
            case POSTGRESQL -> {
                if (constraintName == null || constraintName.isBlank()) {
                    throw new FeatureNotSupportedException(driver);
                }
                yield "DROP CONSTRAINT " + constraintName;
            }
            default -> throw new FeatureNotSupportedException(driver);
        };
    }

    /**
     * Generates the SQL command to drop a column depending on the database driver type.
     *
     * @param query  the query object containing additional context or details for the operation
     * @param driver the database driver type for which the SQL command is being generated
     * @return the SQL command as a string to drop the specified column
     * @throws FeatureNotSupportedException if the operation is not supported for the given driver type
     */
    private String dropColumn(Query query, DriverType driver) {
        Check.ifNullOrEmptyMap(dropObjectName, "dropObjectName");
        return switch (driver) {
            case MYSQL, MARIADB, POSTGRESQL -> "DROP COLUMN " + dropObjectName;
            default -> throw new FeatureNotSupportedException(driver);
        };
    }

    /**
     * Retrieves the type of drop operation to be performed.
     *
     * @return the {@code DropType} indicating the type of drop operation, such as
     * COLUMN or PRIMARY_KEY.
     */
    public DropType dropType() {
        return this.dropType;
    }

    /**
     * Retrieves the name of the object to be dropped in the SQL operation, such as a column
     * or a constraint. This value is used when constructing the corresponding SQL query.
     *
     * @return the name of the object to be dropped as a string
     */
    public String dropObjectName() {
        return this.dropObjectName;
    }

    /**
     * Retrieves the name of the constraint associated with the current operation.
     *
     * @return the name of the constraint as a string
     */
    public String constraintName() {
        return this.constraintName;
    }

    /**
     * Sets the type of drop operation to be performed using this provider instance.
     *
     * @param dropType the {@code DropType} representing the type of drop operation,
     *                 such as COLUMN or PRIMARY_KEY
     * @return the current instance of {@code TableAlterDropColumnQueryProvider}
     * for method chaining
     */
    public TableAlterDropColumnQueryProvider dropType(DropType dropType) {
        this.dropType = dropType;
        return this;
    }

    /**
     * Sets the name of the object to be dropped in the SQL operation. This could represent
     * a column name, constraint name, or other database object relevant to the alter table query.
     *
     * @param dropObjectName the name of the object to be dropped as a string
     * @return the current instance of {@code TableAlterDropColumnQueryProvider} for method chaining
     */
    public TableAlterDropColumnQueryProvider dropObjectName(String dropObjectName) {
        this.dropObjectName = dropObjectName;
        return this;
    }

    /**
     * Sets the name of the constraint associated with the current operation. This value
     * will be used when constructing the corresponding SQL query.
     *
     * @param constraintName the name of the constraint as a string
     * @return the current instance of {@code TableAlterDropColumnQueryProvider} for method chaining
     */
    public TableAlterDropColumnQueryProvider constraintName(String constraintName) {
        this.constraintName = constraintName;
        return this;
    }

    /**
     * Sets the action to be executed after the query is run.
     *
     * @param actionAfterQuery the {@code RunnableAction<Boolean>} to be executed
     *                         post-query, where the Boolean parameter represents
     *                         the success or failure of the query
     * @return the current instance of {@code TableAlterDropColumnQueryProvider}
     * for method chaining
     */
    public TableAlterDropColumnQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }

    /**
     * Represents the types of drop operations that can be performed
     * in an SQL alter table query.
     * <p>
     * This enum provides two specific types:
     * - COLUMN: Indicates a column drop operation in the table.
     * - PRIMARY_KEY: Indicates a primary key drop operation in the table.
     * <p>
     * It is used within the context of constructing SQL queries, allowing
     * differentiation between dropping table columns and dropping primary keys.
     */
    public enum DropType {
        /**
         * Represents a drop operation for a column in an SQL alter table query.
         * <p>
         * The COLUMN constant is used to specify that a particular drop operation
         * is targeting a column within a table. It is a member of the {@link DropType}
         * enum, which differentiates between column-specific and primary key-specific
         * drop operations in SQL queries.
         */
        COLUMN,
        /**
         * Represents a drop operation for a primary key in an SQL alter table query.
         * <p>
         * The PRIMARY_KEY constant is part of the {@link DropType} enum and is used
         * to specify that a particular drop operation is targeting the primary key of
         * a table. This helps differentiate primary key-specific operations from other
         * types of drop operations, such as dropping columns.
         */
        PRIMARY_KEY
    }
}
