package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.ActionType;
import dev.coph.simplesql.query.Query;
import dev.coph.simpleutilities.check.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Provides functionality for generating SQL queries to alter a column's default value
 * in a database table. This class extends the abstract {@code TableAlterQueryProvider}
 * and implements specific logic to handle adding or removing default values for a column.
 *
 * The class supports two actions:
 * - Adding a default value to a column.
 * - Dropping the default value from a column.
 *
 * Instances of this class require specifying a column name, the desired action,
 * and optionally a default value for the add action.
 * An exception will be thrown for unsupported or invalid actions.
 */
@Setter
@Getter
@Accessors(fluent = true)
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
     *
     * Possible actions include:
     * - {@code ADD}: Sets a new default value for the column.
     * - {@code DROP}: Removes the existing default value for the column.
     *
     * This field is mandatory for determining the operation to be carried out
     * when altering a column's default value.
     */
    private ActionType action;
    /**
     * Represents the default value to be set or removed for a database column.
     * This field is used when the action is set to {@code ADD_ACTION} to specify
     * the value that should be applied as the default for the column.
     *
     * The value must be defined and not null when performing an add action.
     * It can represent various data types, such as Strings, numbers, or other
     * objects, depending on the database column's allowed default value type.
     */
    private Object defaultValue;


    @Override
    public String getAlterTableString(Query query) {
        Check.ifNullOrEmptyMap(columnName, "columnName");
        Check.ifNull(action, "action");

        if (action == ActionType.ADD) {
            Check.ifNullOrEmptyMap(defaultValue, "defaultValue");
            return new StringBuilder("ALTER COLUMN ").append(columnName).append(" SET DEFAULT '").append(defaultValue).append("'").toString();
        } else if (action == ActionType.DROP) {
            return new StringBuilder("ALTER COLUMN ").append(columnName).append(" DROP DEFAULT '").toString();
        }
        throw new UnsupportedOperationException("Action not found.");
    }
}
