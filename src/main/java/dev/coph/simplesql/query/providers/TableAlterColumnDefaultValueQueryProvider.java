package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.ActionType;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.query.Query;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

/**
 * Provides functionality for generating SQL queries to alter a column's default value
 * in a database table. This class extends the abstract {@code TableAlterQueryProvider}
 * and implements specific logic to handle adding or removing default values for a column.
 * <p>
 * The class supports two actions:
 * - Adding a default value to a column.
 * - Dropping the default value from a column.
 * <p>
 * Instances of this class require specifying a column name, the desired action,
 * and optionally a default value for the add action.
 * An exception will be thrown for unsupported or invalid actions.
 */
public class TableAlterColumnDefaultValueQueryProvider extends TableAlterQueryProvider {
    /**
     * The name of the database column that will be altered.
     * This field is required for specifying the column on which the default value
     * mutation (adding or dropping) operation will be performed.
     */
    private String columnName;

    /**
     * Specifies the action to be performed on a database column's default value.
     * The action defines whether the column's default value should be added or dropped.
     * <p>
     * Possible actions include:
     * - {@code ADD}: Sets a new default value for the column.
     * - {@code DROP}: Removes the existing default value for the column.
     * <p>
     * This field is mandatory for determining the operation to be carried out
     * when altering a column's default value.
     */
    private ActionType action;
    /**
     * Represents the default value to be set or removed for a database column.
     * This field is used when the action is set to {@code ADD_ACTION} to specify
     * the value that should be applied as the default for the column.
     * <p>
     * The value must be defined and not null when performing an add action.
     * It can represent various data types, such as Strings, numbers, or other
     * objects, depending on the database column's allowed default value type.
     */
    private Object defaultValue;
    private RunnableAction<Boolean> actionAfterQuery;

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public String getAlterTableString(Query query) {
        Check.ifNullOrEmptyMap(columnName, "columnName");
        Check.ifNull(action, "action");

        if (action == ActionType.ADD) {
            Check.ifNullOrEmptyMap(defaultValue, "defaultValue");
            return "ALTER COLUMN " + columnName + " SET DEFAULT '" + defaultValue + "'";
        } else if (action == ActionType.DROP) {
            return "ALTER COLUMN " + columnName + " DROP DEFAULT '";
        }
        throw new UnsupportedOperationException("Action not found.");
    }

    /**
     * Retrieves the name of the column for which the operation is being performed.
     *
     * @return the name of the column
     */
    public String columnName() {
        return this.columnName;
    }

    /**
     * Retrieves the action type associated with the current operation.
     *
     * @return the action type represented by an {@link ActionType} enum, indicating
     * whether the operation is an addition (ADD) or a removal (DROP).
     */
    public ActionType action() {
        return this.action;
    }

    /**
     * Retrieves the default value associated with the column for which an operation
     * is being performed in the context of this query provider.
     *
     * @return the default value of the column, which could be null if no default value is set.
     */
    public Object defaultValue() {
        return this.defaultValue;
    }

    /**
     * Sets the name of the column for which the operation is being performed.
     *
     * @param columnName the name of the column to be set
     * @return the current instance of {@link TableAlterColumnDefaultValueQueryProvider} for method chaining
     */
    public TableAlterColumnDefaultValueQueryProvider columnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    /**
     * Sets the action type associated with the current operation.
     *
     * @param action the action type, represented as an {@link ActionType} enum, indicating
     *               whether the operation is an addition (ADD) or a removal (DROP)
     * @return the current instance of {@link TableAlterColumnDefaultValueQueryProvider} for method chaining
     */
    public TableAlterColumnDefaultValueQueryProvider action(ActionType action) {
        this.action = action;
        return this;
    }

    /**
     * Sets the default value for the column being altered.
     *
     * @param defaultValue the default value to be set for the column, which can be of any object type
     * @return the current instance of {@code TableAlterColumnDefaultValueQueryProvider} for method chaining
     */
    public TableAlterColumnDefaultValueQueryProvider defaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public TableAlterColumnDefaultValueQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }
}
