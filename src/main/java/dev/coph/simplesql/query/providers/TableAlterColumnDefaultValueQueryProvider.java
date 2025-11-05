package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.ActionType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the SQL generation and parameter handling for altering a column's
 * default value in a database table. It supports operations to add or drop default values
 * for the column, depending on the specified action type. It is compatible with specific
 * database drivers and ensures the validity of input and driver support before generating
 * the SQL command.
 */
public class TableAlterColumnDefaultValueQueryProvider extends TableAlterQueryProvider {

    private String columnName;
    private ActionType action;
    private Object defaultValue;
    private RunnableAction<Boolean> actionAfterQuery;

    private List<Object> boundParams = List.of();

    @Override
    public String getAlterTableString(Query query) {
        Check.ifNullOrEmptyMap(columnName, "columnName");
        Check.ifNull(action, "action");

        var driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        DatabaseCheck.missingDriver(driver);

        List<Object> params = new ArrayList<>();
        String sql;

        switch (action) {
            case ADD -> {
                Check.ifNull(defaultValue, "defaultValue");

                switch (driver) {
                    case POSTGRESQL, MYSQL, MARIADB -> {
                        sql = "ALTER COLUMN " + columnName + " SET DEFAULT ?";
                        params.add(defaultValue);
                    }
                    default -> throw new FeatureNotSupportedException(driver);
                }
            }
            case DROP -> {
                switch (driver) {
                    case POSTGRESQL, MYSQL, MARIADB -> {
                        sql = "ALTER COLUMN " + columnName + " DROP DEFAULT";
                    }
                    default -> throw new FeatureNotSupportedException(driver);
                }
            }
            default -> throw new UnsupportedOperationException("Action not found.");
        }
        
        for (int i = 0, paramsSize = params.size(); i < paramsSize; i++) {
            Object p = params.get(i);
            if (p == null) {
                throw new IllegalArgumentException("Parameter list contains null value at slot %s".formatted(i + 1));
            }
        }
        
        this.boundParams = List.copyOf(params);
        return sql;
    }

    @Override
    public List<Object> parameters() {
        return boundParams != null ? boundParams : List.of();
    }

    /**
     * Retrieves the name of the column associated with the query provider.
     *
     * @return the name of the column as a {@code String}
     */
    public String columnName() {
        return this.columnName;
    }

    /**
     * Retrieves the action type associated with the query provider.
     *
     * @return the {@code ActionType} representing the type of action
     * (e.g., {@code ADD} or {@code DROP}) performed by this query provider
     */
    public ActionType action() {
        return this.action;
    }

    /**
     * Retrieves the default value associated with the query provider.
     *
     * @return the default value as an {@code Object}
     */
    public Object defaultValue() {
        return this.defaultValue;
    }

    /**
     * Sets the name of the column for the query provider.
     *
     * @param columnName the name of the column as a {@code String}
     * @return the current instance of {@code TableAlterColumnDefaultValueQueryProvider}
     * to allow for method chaining
     */
    public TableAlterColumnDefaultValueQueryProvider columnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    /**
     * Sets the action type for the query provider.
     *
     * @param action the {@code ActionType} representing the type of action
     *               (e.g., {@code ADD} or {@code DROP}) to be performed
     * @return the current instance of {@code TableAlterColumnDefaultValueQueryProvider}
     * to allow for method chaining
     */
    public TableAlterColumnDefaultValueQueryProvider action(ActionType action) {
        this.action = action;
        return this;
    }

    /**
     * Sets the default value for the column modification query.
     *
     * @param defaultValue the default value to be set, provided as an {@code Object}
     * @return the current instance of {@code TableAlterColumnDefaultValueQueryProvider}
     * to allow for method chaining
     */
    public TableAlterColumnDefaultValueQueryProvider defaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * Sets the action to be executed after a query is run.
     *
     * @param actionAfterQuery the {@code RunnableAction<Boolean>} to be executed post-query,
     *                         where the Boolean parameter represents the success or failure of the query
     * @return the current instance of {@code TableAlterColumnDefaultValueQueryProvider}
     * to allow for method chaining
     */
    public TableAlterColumnDefaultValueQueryProvider actionAfterQuery(
            RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }
}
