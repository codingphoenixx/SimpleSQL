package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.ActionType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.ArrayList;
import java.util.List;

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

        this.boundParams = List.copyOf(params);
        return sql;
    }

    @Override
    public List<Object> parameters() {
        return boundParams != null ? boundParams : List.of();
    }


    public String columnName() {
        return this.columnName;
    }

    public ActionType action() {
        return this.action;
    }

    public Object defaultValue() {
        return this.defaultValue;
    }


    public TableAlterColumnDefaultValueQueryProvider columnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public TableAlterColumnDefaultValueQueryProvider action(ActionType action) {
        this.action = action;
        return this;
    }

    public TableAlterColumnDefaultValueQueryProvider defaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

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
