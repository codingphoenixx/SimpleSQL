package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.query.Query;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

/**
 * Provides functionality to generate SQL "ALTER TABLE" queries for dropping columns, indexes, or primary keys
 * from a database table. It extends the {@link TableAlterQueryProvider} to reuse shared table alteration logic.
 * <p>
 * This class allows the specification of a drop type, indicating whether the action is for a column, index,
 * or primary key. For column and index drop operations, the name of the target column or index must also
 * be provided.
 */
public class TableAlterDropColumnQueryProvider extends TableAlterQueryProvider {

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
    /**
     * Represents the type of drop operation to be performed in an "ALTER TABLE" SQL query.
     * The value of this variable determines whether the operation involves dropping a column,
     * an index, or a primary key. It is used to construct the appropriate SQL statement based
     * on the specified action.
     * <p>
     * Valid values for this variable are:
     * - {@link #COLUMN_DROP_TYPE} (1): Indicates the action will drop a column.
     * - {@link #INDEX_DROP_TYPE} (2): Indicates the action will drop an index.
     * - {@link #PRIMARY_KEY_DROP_TYPE} (3): Indicates the action will drop the primary key.
     * <p>
     * The variable must be set to one of the predefined valid constants before query execution.
     * If it is not set or holds an invalid value, the query construction may fail or throw
     * an appropriate exception.
     * <p>
     * Default value is {@link Integer#MIN_VALUE}, which signifies that no drop type
     * has been specified.
     */
    private int dropType = Integer.MIN_VALUE;
    /**
     * Represents the name of the database object (e.g., column or index) to be dropped during
     * the execution of an "ALTER TABLE" SQL query. The value of this variable is expected
     * to be set when the operation involves dropping a column or index, as determined
     * by the configured drop type.
     * <p>
     * This variable works alongside {@code dropType} to determine the context of the drop
     * operation, specifying the exact object to be removed when the drop type is either
     * {@link #COLUMN_DROP_TYPE} or {@link #INDEX_DROP_TYPE}.
     * <p>
     * It must be provided and not left empty or null for valid execution of the SQL query
     * in such cases.
     */
    private String dropObjectName;
    private RunnableAction<Boolean> actionAfterQuery;

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

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
        return "DROP " + (dropType == COLUMN_DROP_TYPE ? "COLUMN " : "INDEX ") + dropObjectName;
    }

    /**
     * Retrieves the type of drop operation to be performed.
     * This method returns an integer representing the type of drop action,
     * which may relate to columns, indexes, or primary keys based on the implementation.
     *
     * @return an integer representing the drop type.
     */
    public int dropType() {
        return this.dropType;
    }

    /**
     * Retrieves the name of the object to be dropped.
     *
     * @return the name of the object to be dropped as a string
     */
    public String dropObjectName() {
        return this.dropObjectName;
    }

    /**
     * Sets the type of the drop operation to be performed.
     * The drop type determines the specific type of object to be dropped,
     * such as a column, an index, or a primary key.
     *
     * @param dropType an integer representing the drop type
     * @return the current instance of {@code TableAlterDropColumnQueryProvider} for method chaining
     */
    public TableAlterDropColumnQueryProvider dropType(int dropType) {
        this.dropType = dropType;
        return this;
    }

    /**
     * Specifies the name of the object to be dropped in the alter table query.
     *
     * @param dropObjectName the name of the object to be dropped
     * @return the current instance of {@code TableAlterDropColumnQueryProvider} for method chaining
     */
    public TableAlterDropColumnQueryProvider dropObjectName(String dropObjectName) {
        this.dropObjectName = dropObjectName;
        return this;
    }

    public TableAlterDropColumnQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }
}
