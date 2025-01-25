package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryEntry;
import dev.coph.simplesql.utils.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Action for the {@link TableAlterQueryProvider} that will add a column.
 */
@Setter
@Getter
@Accessors(fluent = true)
public class TableAlterAddColumnQueryProvider extends TableAlterQueryProvider {
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
     * If {@link Postion} is set to {@code  Postion.AFTER} the name of the column the new will be added after.
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
    public String getAlterTableString(Query query) {
        Check.ifNullOrEmptyMap(columnName, "columnName");
        Check.ifNullOrEmptyMap(dataType, "dataType");
        Check.ifStringOnlyHasAllowedCharacters(columnName, "columnName");

        StringBuilder stringBuilder = new StringBuilder("ADD COLUMN ").append((ifNotExists ? "IF NOT EXISTS " : null)).append(columnName).append(" ").append(dataType).append((defaultValue == null ? null : " DEFAULT " + QueryEntry.parseSQLValue(defaultValue)));

        if (postion == Postion.DEFAULT) {
            return stringBuilder.toString();
        } else if (postion == Postion.FIRST) {
            stringBuilder.append(" FIRST");
        } else if (postion == Postion.AFTER) {
            stringBuilder.append(" AFTER ").append(afterColumnName);
        }

        return stringBuilder.toString();
    }

    /**
     * The type of position where the column will be added.
     */
    public enum Postion {
        DEFAULT, FIRST, AFTER
    }

}
