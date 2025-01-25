package dev.coph.simplesql.query.providers;

import de.codingphoenix.phoenixbase.check.Checks;
import de.codingphoenix.phoenixbase.database.DataType;
import de.codingphoenix.phoenixbase.database.DatabaseEntry;
import de.codingphoenix.phoenixbase.database.request.tablealter.TableAlterRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Action for the {@link TableAlterRequest} that will add a column.
 */
@Setter
@Getter
@Accessors(fluent = true)
public class TableAlterAddColumnAction implements TableAlterRequest.AlterRequestAction {
    /**
     * The name of the column the attribute should be added.
     */
    private String columnName;
    /**
     * The Type of the data.
     */
    private DataType dataType;
    /**
     * The position of the column.
     */
    private Postion postion = Postion.DEFAULT;
    /**
     * If {@link Postion} is set to {@linkplain Postion.AFTER} the name of the column the new will be added after.
     */
    private String afterColumnName;

    /**
     * The default value of the column.
     */
    private Object defaultValue;
    /**
     * Sets if the action should be "ignored" when the column already exits.
     */
    private boolean ifNotExists = false;

    @Override
    public StringBuilder generateSQL() {
        Checks.checkIfNullOrEmptyMap(columnName, "columnName");
        Checks.checkIfNullOrEmptyMap(dataType, "dataType");
        Checks.checkIfStringOnlyHasAllowedCharacters(columnName, "columnName");

//TODO: Add default value to column
        StringBuilder stringBuilder = new StringBuilder("ADD COLUMN ").append((ifNotExists ? "IF NOT EXISTS " : null)).append(columnName).append(" ").append(dataType).append((defaultValue == null ? null : " DEFAULT " + DatabaseEntry.parseSQLValue(defaultValue)));

        if (postion == Postion.DEFAULT) {
            return stringBuilder;
        } else if (postion == Postion.FIRST) {
            stringBuilder.append(" FIRST");
        } else if (postion == Postion.AFTER) {
            stringBuilder.append(" AFTER ").append(afterColumnName);
        }

        return stringBuilder;
    }

    /**
     * The type of position where the column will be added.
     */
    public enum Postion {
        DEFAULT, FIRST, AFTER
    }

}
