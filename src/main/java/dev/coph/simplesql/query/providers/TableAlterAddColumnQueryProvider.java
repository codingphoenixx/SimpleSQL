package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.Column;
import dev.coph.simplesql.database.attributes.ColumnPosition;
import dev.coph.simplesql.database.attributes.ColumnType;
import dev.coph.simplesql.database.attributes.CreateMethod;
import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simplesql.utils.QueryResult;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

/**
 * Provides functionality for constructing SQL ALTER TABLE ADD COLUMN queries.
 * This class extends the {@code TableAlterQueryProvider} and allows specifying
 * attributes and parameters to add a column to an existing table in a database.
 * <p>
 * It supports various configurations to define the column's position, creation
 * method, and column properties. Additionally, driver-specific compatibility
 * checks and customization are handled within the implementation.
 * <p>
 * Features and capabilities include:
 * - Setting the column to be added, including its key, data type, attributes,
 * and constraints, such as NOT NULL.
 * - Defining the position of the column (e.g., FIRST, AFTER another column, DEFAULT).
 * - Supporting conditional column creation using {@code CreateMethod.IF_NOT_EXISTS}.
 * - Generating database-specific SQL ALTER TABLE commands based on the database driver type.
 * - Configuring an action to be executed after the query is performed.
 * <p>
 * This class ensures compatibility with supported database drivers and enforces
 * validations wherever applicable, such as requiring a valid driver type and non-null column definitions.
 */
public class TableAlterAddColumnQueryProvider extends TableAlterQueryProvider {

    private ColumnPosition postion = ColumnPosition.DEFAULT;
    private String afterColumnName;

    private CreateMethod createMethod = CreateMethod.DEFAULT;
    private Column column;

    private RunnableAction<QueryResult<TableAlterAddColumnQueryProvider>> actionAfterQuery;

    @Override
    public String getAlterTableString(Query query) {
        Check.ifNull(column, "column");

        DriverType driver =
                query.databaseAdapter() != null ? query.databaseAdapter().driverType() : null;

        DatabaseCheck.missingDriver(driver);

        boolean ifNotExists = (createMethod == CreateMethod.IF_NOT_EXISTS);

        StringBuilder sb = new StringBuilder("ADD COLUMN ");

        if (ifNotExists) {
            switch (driver) {
                case MYSQL, MARIADB, POSTGRESQL -> sb.append("IF NOT EXISTS ");
                default -> throw new FeatureNotSupportedException(driver);
            }
        }

        sb.append(column.toString(query));

        switch (postion) {
            case DEFAULT -> {
            }
            case FIRST -> {
                switch (driver) {
                    case MYSQL, MARIADB -> sb.append(" FIRST");
                    default -> throw new FeatureNotSupportedException(driver);
                }
            }
            case AFTER -> {
                Check.ifNullOrEmptyMap(afterColumnName, "afterColumnName");
                switch (driver) {
                    case MYSQL, MARIADB -> sb.append(" AFTER ").append(afterColumnName);
                    default -> throw new FeatureNotSupportedException(driver);
                }
            }
        }

        return sb.toString();
    }

    /**
     * Sets the column to be added in the table alteration query.
     *
     * @param column the column to be added
     * @return the instance of TableAlterAddColumnQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider column(Column column) {
        this.column = column;
        return this;
    }

    /**
     * Sets the column to be added in the table alteration query.
     *
     * @param key      the name of the column to be added
     * @param dataType the data type of the column to be added
     * @return the instance of TableAlterAddColumnQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider column(String key, DataType dataType) {
        this.column = new Column(key, dataType);
        return this;
    }

    /**
     * Sets the column to be added in the table alteration query with the specified details.
     *
     * @param key                     the name of the column to be added
     * @param dataType                the data type of the column to be added
     * @param dataTypeParameterObject an optional parameter providing additional details about the column's data type
     * @return the instance of TableAlterAddColumnQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider column(String key, DataType dataType, Object dataTypeParameterObject) {
        this.column = new Column(key, dataType, dataTypeParameterObject);
        return this;
    }

    /**
     * Sets the column to be added to the table alteration query with the specified details.
     *
     * @param key      the name of the column to be added
     * @param dataType the data type of the column to be added
     * @param notNull  specifies whether the column should have a NOT NULL constraint
     * @return the instance of TableAlterAddColumnQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider column(String key, DataType dataType, boolean notNull) {
        this.column = new Column(key, dataType, notNull);
        return this;
    }

    /**
     * Sets the column to be added to the table alteration query with the specified details.
     *
     * @param key                     the name of the column to be added
     * @param dataType                the data type of the column to be added
     * @param dataTypeParameterObject an optional parameter providing additional details about the column's data type
     * @param notNull                 specifies whether the column should have a NOT NULL constraint
     * @return the instance of TableAlterAddColumnQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider column(
            String key, DataType dataType, Object dataTypeParameterObject, boolean notNull) {
        this.column = new Column(key, dataType, dataTypeParameterObject, notNull);
        return this;
    }

    /**
     * Sets the column to be added to the table alteration query with the specified details.
     *
     * @param key                     the name of the column to be added
     * @param dataType                the data type of the column to be added
     * @param dataTypeParameterObject an optional parameter providing additional details about the column's data type
     * @param columnType              the type of the column, specifying its characteristics (e.g., PRIMARY_KEY, INDEXED)
     * @return the instance of TableAlterAddColumnQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider column(
            String key, DataType dataType, Object dataTypeParameterObject, ColumnType columnType) {
        this.column = new Column(key, dataType, dataTypeParameterObject, columnType);
        return this;
    }

    /**
     * Sets the column to be added to the table alteration query with the specified details.
     *
     * @param key                     the name of the column to be added
     * @param dataType                the data type of the column to be added
     * @param dataTypeParameterObject an optional parameter providing additional details about the column's data type
     * @param columnType              the type of the column, specifying its characteristics (e.g., PRIMARY_KEY, INDEXED)
     * @param notNull                 specifies whether the column should have a NOT NULL constraint
     * @return the instance of TableAlterAddColumnQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider column(
            String key,
            DataType dataType,
            Object dataTypeParameterObject,
            ColumnType columnType,
            boolean notNull) {
        this.column = new Column(key, dataType, dataTypeParameterObject, columnType, notNull);
        return this;
    }

    /**
     * Sets the column to be added to the table alteration query with the specified details.
     *
     * @param key        the name of the column to be added
     * @param dataType   the data type of the column to be added
     * @param columnType the type of the column, specifying its characteristics (e.g., PRIMARY_KEY, INDEXED)
     * @return the instance of TableAlterAddColumnQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider column(String key, DataType dataType, ColumnType columnType) {
        this.column = new Column(key, dataType, columnType);
        return this;
    }

    /**
     * Retrieves the current column position setting.
     * The column position defines the placement of a column
     * (e.g., FIRST, AFTER, or DEFAULT) in the context of table alteration queries.
     *
     * @return the current {@link ColumnPosition} associated with the query
     */
    public ColumnPosition postion() {
        return this.postion;
    }

    /**
     * Retrieves the name of the column after which the new column should be added
     * in the table alteration query. This helps define the position of the new
     * column relative to existing columns in the table.
     *
     * @return the name of the column after which the new column should be added
     */
    public String afterColumnName() {
        return this.afterColumnName;
    }

    /**
     * Retrieves the currently configured {@link CreateMethod} for the table alteration query in the provider.
     * The {@link CreateMethod} determines the strategy to be used during the creation of database structures.
     *
     * @return the current {@link CreateMethod} associated with this query provider
     */
    public CreateMethod createMethode() {
        return this.createMethod;
    }

    /**
     * Retrieves the column that is currently set in the table alteration query context.
     * This represents the column being added or modified within the query.
     *
     * @return the current {@link Column} associated with the table alteration query
     */
    public Column column() {
        return this.column;
    }

    /**
     * Sets the position of the column to be added in the table alteration query.
     * The position can determine whether the column is added at the start, after a specific column,
     * or at the default position.
     *
     * @param postion the position of the column, represented by the {@link ColumnPosition} enumeration
     * @return the instance of TableAlterAddColumnQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider postion(ColumnPosition postion) {
        this.postion = postion;
        return this;
    }

    /**
     * Specifies the name of the column after which the new column should be added
     * in the table alteration query. This determines the relative position of
     * the new column within the table structure.
     *
     * @param afterColumnName the name of the column after which the new column
     *                        should be positioned
     * @return the instance of TableAlterAddColumnQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider afterColumnName(String afterColumnName) {
        this.afterColumnName = afterColumnName;
        return this;
    }

    /**
     * Sets the createMethod property and returns the updated instance of TableAlterAddColumnQueryProvider.
     *
     * @param createMethod the CreateMethod object to be set
     * @return the updated instance of TableAlterAddColumnQueryProvider
     */
    public TableAlterAddColumnQueryProvider createMethode(CreateMethod createMethod) {
        this.createMethod = createMethod;
        return this;
    }

    /**
     * Sets the action to be executed after the query processing is completed.
     *
     * @param actionAfterQuery the action to be executed, represented as a {@link RunnableAction}
     *                         that processes a {@code Boolean} result
     * @return the instance of {@code TableAlterAddColumnQueryProvider} for method chaining
     */
    public TableAlterAddColumnQueryProvider actionAfterQuery(RunnableAction<QueryResult<TableAlterAddColumnQueryProvider>> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<QueryResult<TableAlterAddColumnQueryProvider>> actionAfterQuery() {
        return actionAfterQuery;
    }
}
