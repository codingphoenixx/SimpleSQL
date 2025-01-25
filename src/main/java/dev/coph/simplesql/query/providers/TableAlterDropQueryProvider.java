package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.utils.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Action for the {@link TableAlterQueryProvider} that will drop a table.
 */
@Setter
@Getter
@Accessors(fluent = true)
public class TableAlterDropQueryProvider extends TableAlterQueryProvider {

    /**
     * The type of Action.
     */
    private int dropType = Integer.MIN_VALUE;

    /**
     * The named attribute. Only required if {@linkplain dropType} is set to INDEX_DROP_TYPE or COLUMN_DROP_TYPE
     */
    private String dropObjectName;


    /**
     * Action will drop the named column.
     */
    public static final int COLUMN_DROP_TYPE = 1;
    /**
     * Action will drop the column with the named index.
     */
    public static final int INDEX_DROP_TYPE = 2;
    /**
     * Action will drop the primary key attribute and its provided features.
     */
    public static final int PRIMARY_KEY_DROP_TYPE = 3;

    @Override
    public String getAlterTableString(Query query) {
        Check.ifIntMinValue(dropType, "dropType");
        if (dropType == PRIMARY_KEY_DROP_TYPE) {
            return "DROP PRIMARY KEY";
        }
        Check.ifNullOrEmptyMap(dropObjectName, "dropObjectName");
        if (dropType != 1 && dropType != 2) {
            throw new IllegalArgumentException("Drop type not found.");
        }
        return new StringBuilder("DROP ").append((dropType == COLUMN_DROP_TYPE ? "COLUMN " : "INDEX ")).append(dropObjectName).toString();
    }


//    ALTER TABLE tabellenname DROP COLUMN spaltenname;
//    ALTER TABLE tabellenname DROP INDEX indexname;
//    ALTER TABLE tabellenname DROP PRIMARY KEY;


}
