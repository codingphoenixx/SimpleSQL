package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simplesql.utils.QueryResult;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Provides functionality to build and generate SQL queries for altering a table
 * by adding attributes such as unique constraints or primary keys.
 * This class extends the abstract {@code TableAlterQueryProvider} to implement
 * attribute-specific query generation.
 * <p>
 * This implementation is compatible with certain database drivers and includes
 * specific logic to handle variations in SQL syntax for different databases.
 */
public class TableAlterAddAttributeQueryProvider extends TableAlterQueryProvider {

    private String columnName;
    private List<String> columns;

    private AttributeType attributeType;

    private String constraintName;
    private String indexName;

    private RunnableAction<QueryResult<TableAlterAddAttributeQueryProvider>> actionAfterQuery;

    @Override
    public String getAlterTableString(Query query) {
        Check.ifNull(attributeType, "attributeType");

        var driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        DatabaseCheck.missingDriver(driver);

        List<String> cols = new ArrayList<>();
        if (columns != null && !columns.isEmpty()) {
            cols.addAll(columns);
        } else {
            Check.ifNullOrEmptyMap(columnName, "columnName");
            cols.add(columnName);
        }

        String colList = renderColumnList(cols);

        return switch (attributeType) {
            case PRIMARY_KEY -> renderAddPrimaryKey(driver, colList);
            case UNIQUE -> renderAddUnique(driver, colList);
        };
    }

    /**
     * Generates an SQL statement for adding a primary key constraint based on the provided
     * database driver type and column list. Different SQL syntax is generated depending on
     * the driver type. If the driver type is not supported, a {@code FeatureNotSupportedException}
     * is thrown.
     *
     * @param driver  the database driver type used to determine the appropriate SQL syntax
     * @param colList a comma-separated list of column names to include in the primary key
     * @return the SQL string for adding a primary key constraint
     * @throws FeatureNotSupportedException if the provided driver type is not supported
     */
    private String renderAddPrimaryKey(DriverType driver, String colList) {
        return switch (driver) {
            case MYSQL, MARIADB -> "ADD PRIMARY KEY " + "(" + colList + ")";
            case POSTGRESQL -> {
                if (constraintName != null && !constraintName.isBlank()) {
                    yield "ADD CONSTRAINT " + constraintName + " PRIMARY KEY (" + colList + ")";
                } else {
                    yield "ADD PRIMARY KEY (" + colList + ")";
                }
            }
            default -> throw new FeatureNotSupportedException(driver);
        };
    }

    /**
     * Generates an SQL statement for adding a unique constraint or unique index
     * tailored to the specified database driver type and provided column list.
     * The SQL syntax differs based on the driver type. If the driver type is not supported,
     * a {@code FeatureNotSupportedException} is thrown.
     *
     * @param driver  the database driver type used to determine the appropriate SQL syntax
     * @param colList a comma-separated list of column names to be included in the unique constraint or index
     * @return the SQL string for adding a unique constraint or unique index
     * @throws FeatureNotSupportedException if the provided driver type is not supported
     */
    private String renderAddUnique(DriverType driver, String colList) {
        return switch (driver) {
            case MYSQL, MARIADB -> {
                if (constraintName != null && !constraintName.isBlank()) {
                    yield "ADD CONSTRAINT " + constraintName + " UNIQUE (" + colList + ")";
                } else if (indexName != null && !indexName.isBlank()) {
                    yield "ADD UNIQUE INDEX " + indexName + " (" + colList + ")";
                } else {
                    yield "ADD UNIQUE (" + colList + ")";
                }
            }
            case POSTGRESQL -> {
                if (constraintName != null && !constraintName.isBlank()) {
                    yield "ADD CONSTRAINT " + constraintName + " UNIQUE (" + colList + ")";
                } else {
                    yield "ADD UNIQUE (" + colList + ")";
                }
            }
            default -> throw new FeatureNotSupportedException(driver);
        };
    }

    /**
     * Constructs a comma-separated string from the provided list of column names.
     * Checks that each column name is not null or empty before including it in the result.
     *
     * @param cols a list of column names to be joined into a comma-separated string
     * @return a comma-separated string representation of the column names
     * @throws IllegalArgumentException if any column name is null or empty
     */
    private String renderColumnList(List<String> cols) {
        StringJoiner joiner = new StringJoiner(", ");
        for (String c : cols) {
            Check.ifNullOrEmptyMap(c, "column in columns");
            joiner.add(c);
        }
        return joiner.toString();
    }

    /**
     * Retrieves the name of the column associated with this query provider.
     *
     * @return the column name as a {@code String}
     */
    public String columnName() {
        return this.columnName;
    }

    /**
     * Retrieves the list of columns associated with this query provider.
     *
     * @return a list of column names as {@code List<String>}
     */
    public List<String> columns() {
        return this.columns;
    }

    /**
     * Retrieves the attribute type associated with this query provider.
     *
     * @return the attribute type as an {@code AttributeType}, such as UNIQUE or PRIMARY_KEY
     */
    public AttributeType attributeType() {
        return this.attributeType;
    }

    /**
     * Retrieves the name of the constraint associated with this query provider.
     *
     * @return the constraint name as a {@code String}
     */
    public String constraintName() {
        return this.constraintName;
    }

    /**
     * Retrieves the name of the index associated with this query provider.
     *
     * @return the index name as a {@code String}
     */
    public String indexName() {
        return this.indexName;
    }

    /**
     * Sets the name of the column associated with this query provider.
     *
     * @param columnName the name of the column to be set
     * @return the current instance of {@code TableAlterAddAttributeQueryProvider} for method chaining
     */
    public TableAlterAddAttributeQueryProvider columnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    /**
     * Sets the list of column names associated with this query provider.
     *
     * @param columns a list of column names to be set. If the provided list is null,
     *                the column names will be set to null.
     * @return the current instance of {@code TableAlterAddAttributeQueryProvider}
     * for method chaining.
     */
    public TableAlterAddAttributeQueryProvider columns(List<String> columns) {
        this.columns = columns != null ? new ArrayList<>(columns) : null;
        return this;
    }

    /**
     * Sets the attribute type for the query provider. The attribute type
     * specifies a characteristic such as UNIQUE or PRIMARY_KEY to be applied
     * to a table column.
     *
     * @param attributeType the type of attribute to be set, represented by {@code AttributeType}
     * @return the current instance of {@code TableAlterAddAttributeQueryProvider} for method chaining
     */
    public TableAlterAddAttributeQueryProvider attributeType(AttributeType attributeType) {
        this.attributeType = attributeType;
        return this;
    }

    /**
     * Sets the name of the constraint for the query provider.
     *
     * @param constraintName the name of the constraint to be set
     * @return the current instance of {@code TableAlterAddAttributeQueryProvider} for method chaining
     */
    public TableAlterAddAttributeQueryProvider constraintName(String constraintName) {
        this.constraintName = constraintName;
        return this;
    }

    /**
     * Sets the name of the index associated with this query provider.
     *
     * @param indexName the name of the index to be set
     * @return the current instance of {@code TableAlterAddAttributeQueryProvider} for method chaining
     */
    public TableAlterAddAttributeQueryProvider indexName(String indexName) {
        this.indexName = indexName;
        return this;
    }

    /**
     * Sets the action to be executed after a query is run.
     *
     * @param actionAfterQuery the {@code RunnableAction<Boolean>} to be executed post-query,
     *                         where the Boolean parameter represents the success or failure of the query
     * @return the current instance of {@code TableAlterAddAttributeQueryProvider} for method chaining
     */
    public TableAlterAddAttributeQueryProvider actionAfterQuery(
            RunnableAction<QueryResult<TableAlterAddAttributeQueryProvider>> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<QueryResult<TableAlterAddAttributeQueryProvider>> actionAfterQuery() {
        return actionAfterQuery;
    }

    /**
     * Represents the type of attribute that can be applied to a database column.
     * This enumeration is used to specify unique constraints or primary key constraints
     * when altering a table structure in a database.
     *
     * <ul>
     *   <li>UNIQUE - Denotes that the column values must be unique across all rows in the table.</li>
     *   <li>PRIMARY_KEY - Denotes that the column serves as the primary key, ensuring both uniqueness
     *       and non-null constraints.</li>
     * </ul>
     */
    public enum AttributeType {
        /**
         * Represents a unique constraint on a column in a database table.
         * This ensures that the values in the column are distinct across all rows.
         */
        UNIQUE,
        /**
         * Denotes that a database column serves as the primary key.
         * This implies the column ensures both uniqueness and non-null constraints.
         * It is used to identify each record in a table uniquely.
         */
        PRIMARY_KEY
    }
}
