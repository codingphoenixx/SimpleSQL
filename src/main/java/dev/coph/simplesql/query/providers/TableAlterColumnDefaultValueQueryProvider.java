package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.utils.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Action for the {@link TableAlterQueryProvider} that will set a default value for a column.
 */
@Setter
@Getter
@Accessors(fluent = true)
public class TableAlterColumnDefaultValueQueryProvider extends TableAlterQueryProvider {
    private String columnName;
    private int action = Integer.MIN_VALUE;
    private Object defaultValue;



    /**
     * The action will add a default value.
     */
    public static final int ADD_ACTION = 1;
    /**
     * The action will remove the default value.
     */
    public static final int DROP_ACTION = 2;

    @Override
    public String getAlterTableString(Query query) {
        Check.ifNullOrEmptyMap(columnName, "columnName");
        Check.ifIntMinValue(action, "action");

        if (action == ADD_ACTION) {
            Check.ifNullOrEmptyMap(defaultValue, "defaultValue");
            return new StringBuilder("ALTER COLUMN ").append(columnName).append(" SET DEFAULT '").append(defaultValue).append("'").toString();
        } else if (action == DROP_ACTION) {
            return new StringBuilder("ALTER COLUMN ").append(columnName).append(" DROP DEFAULT '").toString();
        }
        throw new UnsupportedOperationException("Action not found.");
    }
}
