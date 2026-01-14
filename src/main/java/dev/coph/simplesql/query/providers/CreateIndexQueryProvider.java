package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.Condition;
import dev.coph.simplesql.database.attributes.Operator;
import dev.coph.simplesql.database.attributes.Order;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simplesql.utils.QueryResult;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.*;

/**
 * Class responsible for generating SQL queries for creating indexes in a database.
 * Implements the {@code QueryProvider} interface to generate database-specific
 * SQL strings and manage query parameters.
 * <p>
 * This class supports building indexes with various configurations, including index type,
 * concurrent creation, specified columns, optional schema, filtering conditions, and inclusion
 * of additional indexed columns. It ensures compatibility and validation checks for the
 * target database driver.
 */
public class CreateIndexQueryProvider implements QueryProvider {

    private final List<IndexColumn> columns = new ArrayList<>();
    private final LinkedHashSet<Condition> where = new LinkedHashSet<>();
    private String table;
    private String indexName;
    private String tableSchema;
    private IndexType indexType = IndexType.NORMAL;
    private Method method;
    private boolean concurrently;
    private List<String> includeColumns;
    private RunnableAction<QueryResult<CreateIndexQueryProvider>> actionAfterQuery;
    private List<Object> boundParams = List.of();

    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(table, "table");
        Check.ifNullOrEmptyMap(indexName, "indexName");
        Check.ifNullOrEmptyMap(columns, "columns");

        DriverType driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        DatabaseCheck.missingDriver(driver);

        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("CREATE ");

        switch (indexType) {
            case NORMAL -> {
            }
            case UNIQUE -> sql.append("UNIQUE ");
            case FULLTEXT -> {
                if (driver != DriverType.MYSQL && driver != DriverType.MARIADB) {
                    throw new FeatureNotSupportedException(driver);
                }
                sql.append("FULLTEXT ");
            }
            case SPATIAL -> {
                if (driver != DriverType.MYSQL && driver != DriverType.MARIADB) {
                    throw new FeatureNotSupportedException(driver);
                }
                sql.append("SPATIAL ");
            }
        }

        if (concurrently) {
            if (driver == DriverType.POSTGRESQL) {
                sql.append("INDEX CONCURRENTLY ");
            } else {
                throw new FeatureNotSupportedException(driver);
            }
        } else {
            sql.append("INDEX ");
        }

        sql.append(indexName).append(" ON ");

        if (tableSchema != null && !tableSchema.isBlank()) {
            sql.append(tableSchema).append(".").append(table);
        } else {
            sql.append(table);
        }

        if (method != null) {
            switch (driver) {
                case POSTGRESQL -> sql.append(" USING ").append(method.name().toLowerCase());
                case MYSQL, MARIADB -> {
                    if (method == Method.BTREE || method == Method.HASH) {
                        sql.append(" USING ").append(method.name());
                    } else {
                        throw new FeatureNotSupportedException(driver);
                    }
                }
                case SQLITE -> {
                    throw new FeatureNotSupportedException(driver);
                }
                default -> throw new FeatureNotSupportedException(driver);
            }
        }

        sql.append(" (");
        boolean first = true;
        for (IndexColumn c : columns) {
            if (!first) sql.append(", ");
            sql.append(c.expressionOrColumn());
            if (c.length() != null) {
                if (driver == DriverType.MYSQL || driver == DriverType.MARIADB) {
                    sql.append("(").append(c.length()).append(")");
                } else {
                    throw new FeatureNotSupportedException(driver);
                }
            }
            if (c.direction() != null) {
                sql.append(c.direction().operator());
            }
            first = false;
        }
        sql.append(")");

        if (includeColumns != null && !includeColumns.isEmpty()) {
            if (driver == DriverType.POSTGRESQL) {
                sql.append(" INCLUDE (");
                for (int i = 0; i < includeColumns.size(); i++) {
                    if (i > 0) sql.append(", ");
                    sql.append(includeColumns.get(i));
                }
                sql.append(")");
            } else {
                throw new FeatureNotSupportedException(driver);
            }
        }

        if (!where.isEmpty()) {
            if (driver == DriverType.POSTGRESQL || driver == DriverType.SQLITE) {
                sql.append(" WHERE ").append(buildConditions(where.iterator(), params));
            } else {
                throw new FeatureNotSupportedException(driver);
            }
        }

        sql.append(";");
        
        for (int i = 0, paramsSize = params.size(); i < paramsSize; i++) {
            Object p = params.get(i);
            if (p == null) {
                throw new IllegalArgumentException("Parameter list contains null value at slot %s".formatted(i + 1));
            }
        }
        
        this.boundParams = List.copyOf(params);
        return sql.toString();
    }

    /**
     * Constructs a SQL condition clause from an iterator of conditions. This method iterates through
     * the provided conditions to build a SQL-compliant WHERE clause, incorporating logical operators
     * (AND/OR), NOT modifiers, and managing parameterized values for the SQL query.
     *
     * @param it     an iterator over {@code Condition} objects used to build the SQL condition clause
     * @param params a list to which parameterized values for the query are added during the processing
     *               of conditions
     * @return a SQL condition clause string assembled based on the provided conditions
     */
    private String buildConditions(Iterator<Condition> it, List<Object> params) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        while (it.hasNext()) {
            Condition c = it.next();
            if (!first) {
                sb.append(c.type() == Condition.Type.AND ? " AND " : " OR ");
            }
            if (c.not()) sb.append("NOT ");

            String col = c.key();
            Operator op = c.operator();
            Object value = c.value();

            switch (op) {
                case IS_NULL -> sb.append(col).append(" IS NULL");
                case IS_NOT_NULL -> sb.append(col).append(" IS NOT NULL");

                default -> {
                    sb.append(col).append(" ").append(toSqlOperator(op)).append(" ?");
                    params.add(value);
                }
            }
            first = false;
        }
        return sb.toString();
    }

    /**
     * Converts the given {@code Operator} enum value into its corresponding SQL operator string representation.
     * This method facilitates the translation of logical and comparison operators used within the application
     * into valid SQL operators for query generation.
     *
     * @param op the {@code Operator} enum instance to be converted, representing a logical or comparison operation.
     *           Must not be null.
     * @return a string representation of the SQL operator corresponding to the provided {@code Operator}.
     * For instance, {@code EQUALS} maps to {@code "="}, {@code NOT_EQUALS} maps to {@code "<>"}.
     * @throws IllegalStateException if the provided operator is {@code IS_NULL} or {@code IS_NOT_NULL},
     *                               as these are handled separately and do not map to a standard SQL operator string.
     */
    private String toSqlOperator(Operator op) {
        return switch (op) {
            case EQUALS -> "=";
            case NOT_EQUALS -> "<>";
            case GREATER_THAN -> ">";
            case GREATER_EQUALS -> ">=";
            case LESS_THAN -> "<";
            case LESS_EQUALS -> "<=";
            case IN -> "IN";
            case NOT_IN -> "NOT IN";
            case BETWEEN -> "BETWEEN";
            case LIKE -> "LIKE";
            case IS_NULL, IS_NOT_NULL -> throw new IllegalStateException("NULL handled separately");
        };
    }

    @Override
    public List<Object> parameters() {
        return boundParams != null ? boundParams : List.of();
    }

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public RunnableAction<QueryResult<CreateIndexQueryProvider>> actionAfterQuery() {
        return actionAfterQuery;
    }

    /**
     * Sets the action to be executed after the query is processed. The provided action should
     * be a custom implementation that operates on a Boolean value, enabling post-query behavior.
     *
     * @param actionAfterQuery a {@code RunnableAction<Boolean>} representing the action to be executed
     *                         after the query. This could include tasks such as logging, result
     *                         validation, or custom processing logic.
     * @return the current {@code CreateIndexQueryProvider} instance to allow method chaining.
     */
    public CreateIndexQueryProvider actionAfterQuery(RunnableAction<QueryResult<CreateIndexQueryProvider>> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    /**
     * Sets the name of the table to be used in the creation of the index.
     * This specifies the table on which the index will be built.
     *
     * @param table the name of the table on which the index will be created.
     *              Must not be null or empty.
     * @return the current {@code CreateIndexQueryProvider} instance to allow method chaining.
     */
    public CreateIndexQueryProvider table(String table) {
        this.table = table;
        return this;
    }

    /**
     * Sets the schema for the table on which the index will be created.
     * The schema defines the namespace for the table in database systems that support schemas.
     *
     * @param schema the name of the schema to be used. Must not be null or empty.
     * @return the current {@code CreateIndexQueryProvider} instance to allow method chaining.
     */
    public CreateIndexQueryProvider schema(String schema) {
        this.tableSchema = schema;
        return this;
    }

    /**
     * Sets the name of the index to be created or modified. This determines the identifier
     * for the index in the database.
     *
     * @param indexName the name of the index. Must not be null or empty.
     * @return the current {@code CreateIndexQueryProvider} instance to allow method chaining.
     */
    public CreateIndexQueryProvider indexName(String indexName) {
        this.indexName = indexName;
        return this;
    }

    /**
     * Sets the type of the index to be created. If the provided type is null,
     * the default type {@code IndexType.NORMAL} is used.
     *
     * @param type the {@code IndexType} of the index to be set. This can be one
     *             of {@code IndexType.NORMAL}, {@code IndexType.UNIQUE},
     *             {@code IndexType.FULLTEXT}, or {@code IndexType.SPATIAL}.
     *             If null, the default type {@code IndexType.NORMAL} is used.
     * @return the current {@code CreateIndexQueryProvider} instance to allow method chaining.
     */
    public CreateIndexQueryProvider indexType(IndexType type) {
        this.indexType = type != null ? type : IndexType.NORMAL;
        return this;
    }

    /**
     * Sets the method to be used for index creation. The method determines the storage
     * or access method for the index in the database (e.g., BTREE, HASH, GIN, GIST, BRIN).
     *
     * @param method the {@code Method} enum instance specifying the index creation method.
     *               Must not be null. Supported methods include BTREE, HASH, GIN, GIST, and BRIN.
     * @return the current {@code CreateIndexQueryProvider} instance to allow method chaining.
     */
    public CreateIndexQueryProvider method(Method method) {
        this.method = method;
        return this;
    }

    /**
     * Adds a column or expression to the list of columns for the index being created.
     * The column or expression specified will be included in the index definition.
     *
     * @param exprOrColumn a string representing a column name or an expression to be included in the index.
     *                     Must not be null.
     * @return the current {@code CreateIndexQueryProvider} instance to allow method chaining.
     */
    public CreateIndexQueryProvider addColumn(String exprOrColumn) {
        this.columns.add(new IndexColumn(exprOrColumn, null, null));
        return this;
    }

    /**
     * Adds a column or an expression to the list of columns for the index being created, with an optional
     * sort direction and maximum length. The column or expression will be included in the index definition.
     *
     * @param exprOrColumn a string representing a column name or an expression to be included in the index.
     *                     Must not be null.
     * @param direction    the sorting direction for the column, represented by {@code Order.Direction}.
     *                     Can be null, in which case the default sort order is applied.
     * @param length       an optional integer specifying the maximum length for indexing the column.
     *                     Can be null, indicating no length restriction.
     * @return the current {@code CreateIndexQueryProvider} instance to allow method chaining.
     */
    public CreateIndexQueryProvider addColumn(String exprOrColumn, Order.Direction direction, Integer length) {
        this.columns.add(new IndexColumn(exprOrColumn, direction, length));
        return this;
    }

    /**
     * Sets the columns to be included in the index being created. The provided list of columns
     * replaces any existing column configuration for the index.
     *
     * @param cols a list of column names to be included in the index. If the input is null, the column
     *             configuration will be reset (set to null).
     * @return the current {@code CreateIndexQueryProvider} instance to allow method chaining.
     */
    public CreateIndexQueryProvider includeColumns(List<String> cols) {
        this.includeColumns = cols != null ? new ArrayList<>(cols) : null;
        return this;
    }

    /**
     * Adds the specified condition to the WHERE clause of the index query.
     * The provided condition is appended to the current collection of WHERE conditions
     * to allow filtering or specific constraints during index creation.
     *
     * @param condition the {@code Condition} object representing a filtering or logical condition
     *                  to be applied in the WHERE clause. If null, the condition will be ignored.
     * @return the current {@code CreateIndexQueryProvider} instance, enabling method chaining.
     */
    public CreateIndexQueryProvider where(Condition condition) {
        if (condition != null) this.where.add(condition);
        return this;
    }

    /**
     * Adds the specified collection of conditions to the current "where" clause of the query.
     *
     * @param conditions a collection of conditions to be added. If the collection is null, no conditions are added.
     * @return the updated CreateIndexQueryProvider instance with the added conditions.
     */
    public CreateIndexQueryProvider where(Collection<Condition> conditions) {
        if (conditions != null) this.where.addAll(conditions);
        return this;
    }

    /**
     * Sets whether the index should be created concurrently.
     *
     * @param concurrently a boolean indicating if the index creation should occur concurrently
     * @return the current instance of {@code CreateIndexQueryProvider} with the updated setting
     */
    public CreateIndexQueryProvider concurrently(boolean concurrently) {
        this.concurrently = concurrently;
        return this;
    }

    /**
     * Represents the types of indexes that can be applied to database columns.
     * <p>
     * This enum is typically used to specify the nature of an index in database
     * schemas or queries. Each value defines a specific kind of index behavior
     * and constraints:
     * <p>
     * - NORMAL: Represents a standard index without any uniqueness or special constraints.
     * - UNIQUE: Ensures that all values in the indexed column(s) are unique.
     * - FULLTEXT: Used for full-text searching capabilities, often applied to text fields.
     * - SPATIAL: Designed for spatial data indexing, commonly used for Geographic Information Systems (GIS).
     */
    public enum IndexType {
        /**
         * Represents a standard index type without any uniqueness or special constraints.
         * <p>
         * This index type is commonly used for regular indexing purposes in database
         * schemas to enhance query performance without imposing additional constraints
         * such as uniqueness or full-text search capabilities.
         */
        NORMAL,
        /**
         * Represents an index type that ensures all values in the indexed column(s) are unique.
         * <p>
         * This index type is used to enforce uniqueness constraints in database schemas,
         * ensuring that no two rows can have the same value in the specified column(s).
         * Commonly applied to columns such as primary keys or other fields requiring unique identifiers.
         */
        UNIQUE,
        /**
         * Represents an index type used for full-text searching capabilities in database schemas.
         * <p>
         * This index type is specifically designed for indexing text fields to enable efficient
         * and optimized full-text search operations. It is commonly applied to columns containing
         * large text data, such as descriptions or document contents, allowing for complex
         * queries involving pattern matching, relevance ranking, and tokenized searching.
         */
        FULLTEXT,
        /**
         * Represents an index type specifically designed for spatial data indexing.
         * <p>
         * This index type is commonly utilized in Geographic Information Systems (GIS)
         * or databases that handle spatial data, such as coordinates or geometric
         * shapes. It enables efficient querying and indexing of spatial data by
         * optimizing operations like proximity searches, geometric filtering, and
         * spatial relationships between data points.
         */
        SPATIAL
    }

    /**
     * Enumeration representing different indexing methods.
     * <p>
     * This enum defines the types of indexing methods that can be used
     * in database management systems or similar contexts.
     */
    public enum Method {
        /**
         * Represents the B-tree indexing method.
         * <p>
         * The B-tree (balanced tree) indexing method is often used in database
         * management systems to store and retrieve data efficiently. It is
         * particularly suited for range queries and enables ordered traversal
         * of indexed data. This method ensures balanced trees where the path
         * from the root to any leaf has the same length, providing consistent
         * performance for insert, delete, and search operations.
         */
        BTREE,
        /**
         * Represents the HASH indexing method.
         * <p>
         * The HASH indexing method is optimized for equality lookups. It uses a hash
         * function to map keys to locations in the index, ensuring constant-time
         * performance for retrieval operations where the exact value of the key is
         * known. This method is not suitable for range queries or ordered traversal
         * since the data is stored in an unordered format.
         */
        HASH,
        /**
         * Represents the GIN (Generalized Inverted Index) indexing method.
         * <p>
         * The GIN indexing method is designed to handle data types such as arrays and
         * full-text search queries. It allows for efficient indexing and searching
         * of elements within composite data structures. GIN is particularly suitable
         * for scenarios where multiple values are associated with each key, providing
         * quick lookups and matching for elements contained within a data set. It is a
         * versatile choice for applications requiring advanced indexing capabilities.
         */
        GIN,
        /**
         * Represents the GIST (Generalized Search Tree) indexing method.
         * <p>
         * The GIST indexing method is a versatile and extensible indexing scheme
         * designed to support a variety of query types and data structures.
         * It is used in scenarios requiring multi-dimensional or hierarchical
         * data indexing. GIST allows for flexible customization and is commonly
         * applied in applications such as spatial data indexing, text search,
         * and other use cases that benefit from tree-structured indexing.
         */
        GIST,
        /**
         * Represents the BRIN (Block Range INdex) indexing method.
         * <p>
         * The BRIN indexing method is designed for large datasets where data is physically sorted or
         * clustered. It stores metadata about data blocks rather than individual row entries, making it
         * extremely space-efficient. Queries utilizing BRIN can quickly skip over irrelevant blocks
         * based on the stored metadata, providing optimized performance for certain types of workloads,
         * such as those involving range scans or sequentially organized data. It is ideal for use cases
         * requiring minimal index storage and efficient scanning of large, sorted datasets.
         */
        BRIN
    }

    /**
     * Represents a column definition for an index in a database schema.
     * <p>
     * The IndexColumn class specifies the details of a column used in an index,
     * including the column name or expression, the sort direction, and an optional
     * length for indexed column values.
     */
    public static final class IndexColumn {
        /**
         * Represents either a column name or an expression used for column definitions in an index.
         * <p>
         * This value specifies the column or expression that is part of the index being created,
         * allowing flexibility to define simple column references or more complex expressions.
         */
        private final String expressionOrColumn;
        /**
         * Specifies the sort direction of the column or expression in an index.
         * <p>
         * This variable determines if the column or expression used in the index is sorted
         * in ascending or descending order. It is represented by the {@link Order.Direction}
         * enum, which provides options for ASCENDING or DESCENDING directions.
         */
        private final Order.Direction ascending;
        /**
         * Specifies an optional length limit for the indexed column values.
         * <p>
         * This field defines the maximum number of characters (for string-based columns)
         * or bytes (for binary data) to be used for indexing. It is particularly useful
         * for databases that support partial indexing, allowing only the first 'n' characters
         * or bytes of a column's value to be indexed. If null, no limit is applied, and the
         * entire column value is considered for indexing.
         */
        private final Integer length;

        /**
         * Constructs an IndexColumn instance with the specified expression or column name, sort direction,
         * and optional length for indexing.
         *
         * @param expressionOrColumn a string representing either the column name or an expression used in the index specification.
         *                           This is a required parameter and cannot be null.
         * @param ascending          the sort direction for the column or expression in the index, specified
         *                           as an {@link Order.Direction} value. It can be either {@link Order.Direction#ASCENDING}
         *                           or {@link Order.Direction#DESCENDING}.
         * @param length             an optional integer specifying the length of the indexed value.
         *                           If null, the entire value is indexed without any length restriction.
         */
        public IndexColumn(String expressionOrColumn, Order.Direction ascending, Integer length) {
            this.expressionOrColumn = Objects.requireNonNull(expressionOrColumn);
            this.ascending = ascending;
            this.length = length;
        }

        /**
         * Retrieves the value of the expression or column name used in the definition of an index column.
         *
         * @return a string representing either a column name or an expression that forms part of the index.
         */
        public String expressionOrColumn() {
            return expressionOrColumn;
        }

        /**
         * Retrieves the sort direction of the column or expression used in an index.
         * <p>
         * The direction indicates whether the column or expression is sorted in ascending
         * or descending order, as specified by the {@link Order.Direction} enum.
         *
         * @return the sort direction for the index column, represented as an {@link Order.Direction} enum value.
         */
        public Order.Direction direction() {
            return ascending;
        }

        /**
         * Retrieves the optional length for the indexed column values.
         * <p>
         * The length specifies the maximum number of characters or bytes
         * to be used for indexing the column's values. If null, no limit
         * is applied, and the entire column value is indexed.
         *
         * @return the length of the indexed column, or null if no length limit is defined.
         */
        public Integer length() {
            return length;
        }
    }
}
