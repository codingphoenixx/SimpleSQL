package dev.coph.simplesql.query.providers;

import de.codingphoenix.phoenixbase.check.Checks;
import de.codingphoenix.phoenixbase.database.request.tablealter.TableAlterRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Action for the {@link TableAlterRequest} that will set a default value for a column.
 */
@Setter
@Getter
@Accessors(fluent = true)
public class TableAlterColumnDefaultValueAction implements TableAlterRequest.AlterRequestAction {
    private String columnName;
    private int action = Integer.MIN_VALUE;
    private Object defaultValue;

    @Override
    public StringBuilder generateSQL() {
        Checks.checkIfNullOrEmptyMap(columnName, "columnName");
        Checks.checkIfIntMinValue(action, "action");

        if (action == ADD_ACTION) {
            Checks.checkIfNullOrEmptyMap(defaultValue, "defaultValue");
            return new StringBuilder("ALTER COLUMN ").append(columnName).append(" SET DEFAULT '").append(defaultValue).append("'");
        } else if (action == DROP_ACTION) {
            return new StringBuilder("ALTER COLUMN ").append(columnName).append(" DROP DEFAULT '");
        }
        throw new UnsupportedOperationException("Action is out of range.");
    }


    /**
     * The action will add a default value.
     */
    public static final int ADD_ACTION = 1;
    /**
     * The action will remove the default value.
     */
    public static final int DROP_ACTION = 2;

//    ALTER TABLE tabellenname ALTER COLUMN spaltenname SET DEFAULT wert;
//    ALTER TABLE tabellenname ALTER COLUMN spaltenname DROP DEFAULT
}
