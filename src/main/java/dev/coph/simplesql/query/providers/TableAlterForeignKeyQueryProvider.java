package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.ActionType;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * A query provider class for constructing SQL statements to modify foreign key constraints
 * in database tables. This includes adding and dropping foreign key constraints.
 * The class supports multiple database driver types and adapts the query generation
 * logic accordingly.
 * <p>
 * It provides fluent methods to define the foreign key constraint details, such as
 * columns involved, referenced table and columns, constraint actions, and deferrable
 * options.
 * <p>
 * This class also allows specifying an action to be executed after the query operation.
 */
public class TableAlterForeignKeyQueryProvider extends TableAlterQueryProvider {

    private final List<String> columns = new ArrayList<>();
    private final List<String> referencedColumns = new ArrayList<>();
    private ActionType action;
    private String constraintName;
    private String referencedTable;
    private ReferentialAction onDelete;
    private ReferentialAction onUpdate;
    private DeferrableType deferrable;
    private InitiallyDeferrable initiallyDeferred;
    private RunnableAction<Boolean> actionAfterQuery;

    @Override
    public String getAlterTableString(Query query) {
        Check.ifNull(action, "action");
        return switch (action) {
            case ADD -> buildAddForeignKey(query);
            case DROP -> buildDropForeignKey(query);
            default -> throw new UnsupportedOperationException("Unsupported action: " + action);
        };
    }

    /**
     * Builds the SQL query string to add a foreign key constraint to a table.
     * <p>
     * This method constructs the appropriate SQL command based on the provided
     * {@link Query} instance and database driver type. The resulting SQL string
     * is based on the driver-specific syntax for MySQL, MariaDB, and PostgreSQL.
     * <p>
     * The method validates that the list of columns in the target table and the
     * referenced table are non-empty and have matching sizes. If there is a mismatch,
     * it throws an {@link IllegalArgumentException}. Additionally, driver-specific
     * constraints like deferrable options or referential actions are included where valid.
     *
     * @param query the {@link Query} object containing database-specific configurations
     *              and adapter details used to format and construct the SQL statement
     * @return the constructed SQL string for adding a foreign key constraint
     * @throws IllegalArgumentException     if the number of columns does not match
     *                                      the number of referenced columns
     * @throws FeatureNotSupportedException if the database driver type is unsupported or
     *                                      specific features are not available for a driver
     */
    private String buildAddForeignKey(Query query) {
        Check.ifNullOrEmptyMap(table, "table name");
        Check.ifNullOrEmptyMap(columns, "columns");
        Check.ifNullOrEmptyMap(referencedTable, "referencedTable");
        Check.ifNullOrEmptyMap(referencedColumns, "referencedColumns");

        if (columns.size() != referencedColumns.size()) {
            throw new IllegalArgumentException("columns and referencedColumns must have same size");
        }

        DriverType driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;


        DatabaseCheck.missingDriver(driver);

        StringBuilder sb = new StringBuilder();

        switch (driver) {
            case MYSQL, MARIADB -> {
                sb.append("ADD ");
                if (constraintName != null && !constraintName.isBlank()) {
                    sb.append("CONSTRAINT ").append(constraintName).append(" ");
                }
                sb.append("FOREIGN KEY (").append(joinIdents(columns, query)).append(") ")
                        .append("REFERENCES ").append(referencedTable)
                        .append(" (").append(joinIdents(referencedColumns, query)).append(")");

                appendReferentialActions(sb, driver);

                if (deferrable != null || initiallyDeferred != null) {
                    throw new FeatureNotSupportedException(driver);
                }
                return sb.toString();
            }
            case POSTGRESQL -> {
                sb.append("ADD ");
                if (constraintName != null && !constraintName.isBlank()) {
                    sb.append("CONSTRAINT ").append(constraintName).append(" ");
                }
                sb.append("FOREIGN KEY (").append(joinIdents(columns, query)).append(") ")
                        .append("REFERENCES ").append(referencedTable)
                        .append(" (").append(joinIdents(referencedColumns, query)).append(")");

                appendReferentialActions(sb, driver);

                if (deferrable != null && deferrable != DeferrableType.NO) {
                    sb.append(deferrable.name().replaceAll("_", " "));
                }
                if (initiallyDeferred != null && initiallyDeferred != InitiallyDeferrable.NO) {
                    sb.append(initiallyDeferred.name().replaceAll("_", " "));
                }
                return sb.toString();
            }
            default -> throw new FeatureNotSupportedException(driver);
        }
    }

    /**
     * Builds the SQL query string to drop a foreign key constraint from a table.
     * <p>
     * This method constructs the appropriate SQL command based on the provided
     * {@link Query} instance and the database driver type. The resulting SQL string
     * is constructed according to the driver-specific syntax for MySQL, MariaDB, and PostgreSQL.
     * If the driver type is unsupported or missing, an exception is raised.
     *
     * @param query the {@link Query} object containing database-specific configurations
     *              and adapter details used to format and construct the SQL statement
     * @return the constructed SQL string for dropping a foreign key constraint
     * @throws FeatureNotSupportedException if the database driver type is unsupported
     * @throws IllegalArgumentException     if the foreign key constraint name is null or empty
     */
    private String buildDropForeignKey(Query query) {
        DriverType driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        DatabaseCheck.missingDriver(driver);

        switch (driver) {
            case MYSQL, MARIADB -> {
                Check.ifNullOrEmptyMap(constraintName, "constraintName");
                return "DROP FOREIGN KEY " + constraintName;
            }
            case POSTGRESQL -> {
                Check.ifNullOrEmptyMap(constraintName, "constraintName");
                return "DROP CONSTRAINT " + constraintName;
            }
            default -> throw new FeatureNotSupportedException(driver);
        }
    }

    /**
     * Appends referential actions (ON DELETE and ON UPDATE) to the provided SQL string builder
     * based on the specified referential constraints and database driver type.
     *
     * @param sb     the {@code StringBuilder} object to which the referential action SQL clauses
     *               are appended
     * @param driver the {@code DriverType} representing the type of database driver,
     *               which determines the syntax used for referential actions
     */
    private void appendReferentialActions(StringBuilder sb, DriverType driver) {
        if (onDelete != null) {
            sb.append(" ON DELETE ").append(mapAction(onDelete, driver));
        }
        if (onUpdate != null) {
            sb.append(" ON UPDATE ").append(mapAction(onUpdate, driver));
        }
    }

    /**
     * Maps the given referential action to its corresponding string representation
     * by replacing underscores with spaces in the name of the action.
     *
     * @param action the {@link ReferentialAction} enum representing the referential action
     *               type (e.g., CASCADE, SET_NULL, etc.)
     * @param driver the {@link DriverType} representing the type of database driver; unused
     *               in the logic but may provide context for custom implementations
     * @return a {@code String} containing the formatted name of the referential action
     */
    private String mapAction(ReferentialAction action, DriverType driver) {
        return action.name().replaceAll("_", " ");
    }

    /**
     * Joins a list of identifiers into a single comma-separated string.
     * <p>
     * This method validates that each identifier in the provided list is neither
     * null nor empty, throwing an exception if invalid identifiers are detected.
     * It uses a {@code StringJoiner} to produce a properly formatted and delimited
     * string suitable for use in SQL queries or similar contexts.
     *
     * @param idents the list of string identifiers to be joined
     * @param query  the {@link Query} object that may provide additional context relevant
     *               to the operation
     * @return a comma-separated string of identifiers
     * @throws IllegalArgumentException if any identifier in the list is null or empty
     */
    private String joinIdents(List<String> idents, Query query) {
        StringJoiner j = new StringJoiner(", ");
        for (String s : idents) {
            Check.ifNullOrEmptyMap(s, "identifier");
            j.add(s);
        }
        return j.toString();
    }

    /**
     * Sets the action type for the foreign key operation.
     * The action can represent either an addition or removal of a foreign key constraint.
     *
     * @param action the {@link ActionType} representing the type of action
     *               to be performed, such as {@code ADD} or {@code DROP}
     * @return the current instance of {@code TableAlterForeignKeyQueryProvider},
     * allowing method chaining for further modifications
     */
    public TableAlterForeignKeyQueryProvider action(ActionType action) {
        this.action = action;
        return this;
    }

    /**
     * Sets the name of the foreign key constraint.
     *
     * @param name the name to be assigned to the foreign key constraint
     * @return the current instance of {@code TableAlterForeignKeyQueryProvider},
     * allowing method chaining for further modifications
     */
    public TableAlterForeignKeyQueryProvider constraintName(String name) {
        this.constraintName = name;
        return this;
    }

    /**
     * Adds a column to the list of columns for the foreign key constraint.
     *
     * @param column the name of the column to be added to the foreign key constraint
     * @return the current instance of {@code TableAlterForeignKeyQueryProvider},
     * allowing method chaining for further modifications
     */
    public TableAlterForeignKeyQueryProvider column(String column) {
        this.columns.add(column);
        return this;
    }

    /**
     * Sets the list of columns for the foreign key constraint.
     * Adds all the specified column names to the existing columns list.
     *
     * @param columns the list of column names to be included in the foreign key constraint;
     *                if null, no action will be performed
     * @return the current instance of {@code TableAlterForeignKeyQueryProvider},
     * allowing method chaining for further modifications
     */
    public TableAlterForeignKeyQueryProvider columns(List<String> columns) {
        if (columns != null) this.columns.addAll(columns);
        return this;
    }

    /**
     * Sets the name of the referenced table for the foreign key constraint.
     *
     * @param table the name of the table that is referenced by the foreign key
     * @return the current instance of {@code TableAlterForeignKeyQueryProvider},
     * allowing method chaining for further modifications
     */
    public TableAlterForeignKeyQueryProvider referencedTable(String table) {
        this.referencedTable = table;
        return this;
    }

    /**
     * Adds a referenced column to the list of columns for the foreign key constraint.
     *
     * @param column the name of the column in the referenced table to be added to the foreign key constraint
     * @return the current instance of {@code TableAlterForeignKeyQueryProvider},
     * allowing method chaining for further modifications
     */
    public TableAlterForeignKeyQueryProvider referencedColumn(String column) {
        this.referencedColumns.add(column);
        return this;
    }

    /**
     * Sets the list of referenced columns for the foreign key.
     *
     * @param columns a list of column names that are referenced in the foreign key.
     *                If the provided list is null, no columns are added.
     * @return the current instance of TableAlterForeignKeyQueryProvider for method chaining.
     */
    public TableAlterForeignKeyQueryProvider referencedColumns(List<String> columns) {
        if (columns != null) this.referencedColumns.addAll(columns);
        return this;
    }

    /**
     * Sets the referential action to be performed when a foreign key is deleted.
     *
     * @param action the {@link ReferentialAction} to specify the behavior on delete
     * @return the current instance of {@link TableAlterForeignKeyQueryProvider} for method chaining
     */
    public TableAlterForeignKeyQueryProvider onDelete(ReferentialAction action) {
        this.onDelete = action;
        return this;
    }

    /**
     * Specifies the referential action to be applied on update.
     *
     * @param action the ReferentialAction to be applied when the referenced
     *               record is updated
     * @return the current instance of TableAlterForeignKeyQueryProvider
     * for method chaining
     */
    public TableAlterForeignKeyQueryProvider onUpdate(ReferentialAction action) {
        this.onUpdate = action;
        return this;
    }

    /**
     * Sets the deferrable type for the foreign key constraint.
     *
     * @param deferrable the deferrable type specifying if the foreign key constraint is deferrable
     * @return the updated instance of TableAlterForeignKeyQueryProvider
     */
    public TableAlterForeignKeyQueryProvider deferrable(DeferrableType deferrable) {
        this.deferrable = deferrable;
        return this;
    }

    /**
     * Specifies whether the foreign key constraint should be initially deferred.
     * A deferred constraint allows the foreign key check to be postponed until
     * the end of the transaction.
     *
     * @param initiallyDeferred the deferrable state of the foreign key constraint
     * @return the current instance of TableAlterForeignKeyQueryProvider with the
     * updated deferrable state
     */
    public TableAlterForeignKeyQueryProvider initiallyDeferred(InitiallyDeferrable initiallyDeferred) {
        this.initiallyDeferred = initiallyDeferred;
        return this;
    }

    /**
     * Sets the action to be executed after the query is performed.
     *
     * @param actionAfterQuery a RunnableAction that returns a Boolean, representing
     *                         the action to execute after the query
     * @return the current instance of TableAlterForeignKeyQueryProvider for method chaining
     */
    public TableAlterForeignKeyQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }

    /**
     * Represents the referential actions that can be applied to a database row
     * in the context of foreign key constraints.
     */
    public enum ReferentialAction {
        /**
         * Represents a referential action in the context of database foreign key constraints
         * where no action is performed when a referenced row is deleted or updated.
         * <p>
         * It indicates that the database will not enforce any cascading changes or restrictions,
         * leaving the responsibility of maintaining referential integrity to the application.
         */
        NO_ACTION,
        /**
         * Represents a referential action in the context of database foreign key constraints
         * where DELETE or UPDATE operations on a referenced row are rejected if dependent
         * rows in referencing tables exist.
         * <p>
         * This action enforces stricter referential integrity by preventing operations
         * that would break foreign key dependencies. The operation will fail with an error
         * and the database will roll back the transaction.
         */
        RESTRICT,
        /**
         * Represents a referential action in the context of database foreign key constraints
         * where changes made to a referenced row are propagated to the dependent rows in
         * referencing tables.
         * <p>
         * This action enforces cascade behavior such that DELETE or UPDATE operations
         * on a referenced row trigger corresponding DELETE or UPDATE operations
         * on all dependent rows, ensuring referential integrity within the database.
         */
        CASCADE,
        /**
         * Represents a referential action in the context of database foreign key constraints
         * where the foreign key columns in dependent rows are set to NULL when the referenced
         * row is deleted or updated.
         * <p>
         * This action ensures that dependent rows remain in the database but their foreign key
         * references are set to null, effectively removing the dependency. It maintains referential
         * integrity by allowing the referenced row to be modified or removed without causing errors.
         */
        SET_NULL,
        /**
         * Represents a referential action in the context of database foreign key constraints
         * where the foreign key columns in dependent rows are set to their default values
         * when the referenced row is deleted or updated.
         * <p>
         * This action allows a referenced row to be modified or removed while maintaining
         * referential integrity by assigning predefined default values to the foreign key columns
         * in dependent rows. It ensures that the database enforces default fallback values
         * for affected references.
         */
        SET_DEFAULT
    }

    /**
     * Defines the possible deferrable types for operations or constraints.
     * The enum constants specify whether an operation or constraint can
     * be deferred or must be enforced immediately.
     */
    public enum DeferrableType {
        /**
         * Represents a non-deferrable type.
         * Used to indicate that the specified operation or constraint
         * cannot be deferred to a later point in time.
         */
        NO,
        /**
         * Represents a deferrable type.
         * Indicates that the specified operation or constraint
         * can be deferred to a later point in time.
         */
        DEFERRABLE,
        /**
         * Represents the "NOT DEFERRABLE" type in the context of database constraints or operations.
         * Indicates that the constraint or operation cannot be deferred and must be enforced immediately.
         */
        NOT_DEFERRABLE
    }

    /**
     * Represents the deferrable states of an entity, typically related to database transactions.
     * This enum defines three possible states:
     * <p>
     * NO - Indicates that the deferrable state is not applicable or unspecified.
     * INITIALLY_DEFERRED - Indicates that the entity is initially deferred, meaning constraints
     * are checked at the end of a transaction.
     * INITIALLY_IMMEDIATE - Indicates that the entity is initially immediate, meaning constraints
     * are checked immediately after each statement.
     */
    public enum InitiallyDeferrable {
        /**
         * Represents a state indicating that the deferrable condition is not applicable
         * or unspecified in the context of initial constraint checking within a transaction.
         */
        NO,
        /**
         * Indicates that the entity is initially deferred.
         * This means that constraints are deferred and checked at the
         * end of a transaction rather than immediately after each statement.
         */
        INITIALLY_DEFERRED,
        /**
         * Indicates that the entity is initially immediate.
         * This means that constraints are checked immediately after each statement
         * during a transaction, rather than being deferred to the end of the transaction.
         */
        INITIALLY_IMMEDIATE
    }
}
