package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.*;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.query.SimpleResultSet;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simplesql.utils.QueryResult;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.*;

/**
 * Provides functionality for defining, constructing, and executing
 * SQL SELECT queries. This class allows the user to specify
 * various components of a SELECT query, including table names,
 * conditions, column selections, joins, ordering, grouping,
 * limits, locking mechanisms, and post-query actions.
 * <p>
 * The class also supports generating SQL strings and managing
 * query parameters.
 */
public class SelectQueryProvider implements QueryProvider {

    private final List<Join> joins = new ArrayList<>();
    private final LinkedHashSet<Condition> whereConditions = new LinkedHashSet<>();
    private String table;
    private SelectFunction function = SelectFunction.NORMAL;
    private SelectType selectType = SelectType.NORMAL;
    private String columnAlias;
    private Order order;
    private List<String> columnKeys = new ArrayList<>();
    private Limit limit;
    private LockMode lockMode;
    private boolean skipLocked;
    private boolean noWait;

    private Group group;
    private RunnableAction<SimpleResultSet> resultActionAfterQuery;
    private RunnableAction<QueryResult<SelectQueryProvider>> actionAfterQuery;
    private SimpleResultSet simpleResultSet;
    private List<Object> boundParams = List.of();

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public String generateSQLString(Query query) {
        if (columnKeys.isEmpty()) columnKeys.add("*");
        Check.ifNullOrEmptyMap(table, "table name");

        DriverType driver =
                query.databaseAdapter() != null ? query.databaseAdapter().driverType() : null;

        if (driver == null) throw new IllegalStateException("Database adapter is null");

        StringBuilder sql = new StringBuilder("SELECT ");
        List<Object> params = new ArrayList<>();

        if (selectType == SelectType.DISTINCT) {
            sql.append("DISTINCT ");
        }

        if (function != null && function != SelectFunction.NORMAL) {
            String col = columnKeys.get(0);
            sql.append(function.name()).append("(").append(col).append(")");
        } else {
            sql.append(parseColumnNames());
        }

        sql.append(" FROM ").append(table);

        if (columnAlias != null && !columnAlias.isBlank()) {
            sql.append(" AS ").append(columnAlias);
        }


        if (!joins.isEmpty()) {
            for (Join j : joins) {
                renderJoin(sql, driver, j, params);
            }
        }

        if (!whereConditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(buildConditions(whereConditions.iterator(), params));
        }

        if (group != null && group.keys() != null && !group.keys().isEmpty()) {
            sql.append(" ").append(buildGroupBy(group));
        }

        if (group != null && group.conditions() != null && !group.conditions().isEmpty()) {
            sql.append(" HAVING ");
            sql.append(buildConditions(group.conditions().iterator(), params));
        }

        if (order != null && order.orderRules() != null && !order.orderRules().isEmpty()) {
            sql.append(order.toString(query));
        }

        int lim = (limit != null) ? limit.limit() : 0;
        int off = (limit != null) ? limit.offset() : 0;
        if (lim > 0 || off > 0) {
            appendLimitOffset(sql, driver, lim, off);
        }

        if (lockMode != null || skipLocked || noWait) {
            appendLocking(sql, driver);
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

    @Override
    public List<Object> parameters() {
        return boundParams != null ? boundParams : List.of();
    }

    @Override
    public RunnableAction<QueryResult<SelectQueryProvider>> actionAfterQuery() {
        return actionAfterQuery;
    }

    /**
     * Generates a comma-separated string of column names based on the internal state of the columnKeys field.
     * If columnKeys is null or empty, returns "*". If there is only one column, returns the column name directly.
     * For multiple columns, concatenates them into a single string separated by ", ".
     *
     * @return A string representing column names for an SQL query. Returns "*" if columnKeys is null or empty,
     * a single column name if only one exists, or a comma-separated list of column names if there are multiple.
     */
    private String parseColumnNames() {
        if (columnKeys == null || columnKeys.isEmpty()) return "*";
        if (columnKeys.size() == 1) return columnKeys.get(0);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String c : columnKeys) {
            if (!first) sb.append(", ");
            sb.append(c);
            first = false;
        }
        return sb.toString();
    }

    /**
     * Builds the SQL "GROUP BY" clause from the specified Group object.
     * The method generates a comma-separated list of column names provided by the Group object.
     * If no keys are provided, the method assumes no "GROUP BY" clause is required.
     *
     * @param group the Group object containing the keys to include in the "GROUP BY" clause.
     *              If the Group object contains no keys, the resulting string will not append
     *              any column names to the "GROUP BY" keyword.
     * @return the constructed "GROUP BY" clause as a string.
     */
    private String buildGroupBy(Group group) {
        StringBuilder sb = new StringBuilder("GROUP BY ");
        boolean first = true;
        for (String k : group.keys()) {
            if (!first) sb.append(", ");
            sb.append(k);
            first = false;
        }
        return sb.toString();
    }

    /**
     * Constructs a SQL WHERE clause string based on the provided conditions.
     * Iterates through the given conditions and builds a SQL fragment using the column names,
     * operators, and values. The parameters for the query are appended to the provided list.
     *
     * @param it     An iterator over {@link Condition} objects representing each condition to be included
     *               in the SQL WHERE clause.
     * @param params A list to which parameters for the SQL query (e.g., values for placeholders) will
     *               be added during processing.
     * @return A string representing the SQL WHERE clause, including conditions combined with AND/OR
     * operators, appropriate NOT clauses, and parameter placeholders.
     */
    private String buildConditions(Iterator<Condition> it, List<Object> params) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        while (it.hasNext()) {
            Condition c = it.next();
            if (!first) {
                sb.append(c.type() == Condition.Type.AND ? " AND " : " OR ");
            }
            appendCondition(sb, c, params);
            first = false;
        }
        return sb.toString();
    }

    private void appendCondition(StringBuilder sb, Condition c, List<Object> params) {
        if (c.not()) sb.append("NOT ");

        if (c.isGroup()) {
            sb.append("(");
            List<Condition> children = c.children();
            Iterator<Condition> it = children.iterator();
            boolean first = true;
            while (it.hasNext()) {
                Condition child = it.next();
                if (!first) {
                    sb.append(child.type() == Condition.Type.AND ? " AND " : " OR ");
                }
                appendCondition(sb, child, params);
                first = false;
            }
            sb.append(")");
            return;
        }

        String column = c.key();
        Operator op = c.operator();
        Object value = c.value();

        switch (op) {
            case IS_NULL -> sb.append(column).append(" IS NULL");
            case IS_NOT_NULL -> sb.append(column).append(" IS NOT NULL");
            case IN, NOT_IN -> {
                if (!(value instanceof Collection<?> col) || col.isEmpty()) {
                    throw new IllegalArgumentException(op + " requires non-empty Collection");
                }
                sb.append(column).append(" ").append(op.operator()).append(" (");
                boolean first = true;
                for (Object v : col) {
                    if (!first) sb.append(", ");
                    sb.append("?");
                    params.add(v);
                    first = false;
                }
                sb.append(")");
            }
            case LIKE -> {
                if (value == null) {
                    throw new IllegalArgumentException("LIKE requires a non-null value");
                }
                sb.append(column).append(" LIKE ?");
                params.add(value);
            }
            case BETWEEN -> {
                if (!(value instanceof List<?> list) || list.size() != 2) {
                    throw new IllegalArgumentException("BETWEEN requires List of size 2");
                }
                sb.append(column).append(" BETWEEN ? AND ?");
                params.add(list.get(0));
                params.add(list.get(1));
            }
            default -> {
                sb.append(column).append(" ").append(op.operator()).append(" ?");
                params.add(value);
            }
        }
    }

    /**
     * Generates the SQL fragment for a SQL JOIN clause based on the specified parameters and appends it to the given {@code StringBuilder}.
     * This method considers the join type (e.g., INNER, LEFT, RIGHT, FULL), validates compatibility with the provided database driver,
     * and processes the join table, alias, and ON conditions.
     *
     * @param sql    the {@code StringBuilder} to which the JOIN clause will be appended
     * @param driver the {@link DriverType} indicating the type of the database driver being used
     * @param join   the {@link Join} object representing the details of the JOIN clause, including type, table name, alias, and ON conditions
     * @param params the {@code List<Object>} to which query parameter values will be appended as part of the ON conditions processing
     * @throws IllegalArgumentException     if no ON conditions are provided in the {@code join} object
     * @throws FeatureNotSupportedException if the specified JOIN type is not supported by the provided {@code driver}
     */
    private void renderJoin(StringBuilder sql, DriverType driver, Join join, List<Object> params) {
        switch (join.type()) {
            case INNER -> sql.append(" INNER JOIN ");
            case LEFT -> sql.append(" LEFT JOIN ");
            case RIGHT -> {
                DatabaseCheck.unsupportedDriver(driver, DriverType.SQLITE);
                if (driver == DriverType.SQLITE) throw new FeatureNotSupportedException(driver);
                sql.append(" RIGHT JOIN ");
            }
            case FULL -> {
                DatabaseCheck.requireDriver(driver, DriverType.POSTGRESQL);
                sql.append(" FULL OUTER JOIN ");
            }
        }
        sql.append(join.table());
        if (join.alias() != null && !join.alias().isBlank()) {
            sql.append(" AS ").append(join.alias());
        }
        if (join.onConditions() != null && !join.onConditions().isEmpty()) {
            sql.append(" ON ");
            sql.append(buildConditions(join.onConditions().iterator(), params));
        } else {
            throw new IllegalArgumentException("JOIN requires ON conditions");
        }
    }

    /**
     * Appends SQL limit and offset clauses to the provided SQL query string based on the specified
     * limit and offset values. The clauses are formatted based on the syntax supported by the given
     * database driver.
     *
     * @param sql    the {@code StringBuilder} containing the SQL statement to which the LIMIT and
     *               OFFSET clauses will be appended
     * @param driver the {@code DriverType} representing the type of the database driver being used
     * @param limit  the maximum number of rows to return; must be greater than 0
     * @param offset the number of rows to skip before beginning to return rows; must be 0 or greater
     * @throws FeatureNotSupportedException if the LIMIT clause is not supported by the specified driver
     */
    private void appendLimitOffset(StringBuilder sql, DriverType driver, int limit, int offset) {
        if (limit > 0) {
            sql.append(" LIMIT ").append(limit);
            if (offset > 0) {
                sql.append(" OFFSET ").append(offset);
            }
        } else {
            throw new FeatureNotSupportedException(driver);
        }
    }

    /**
     * Appends the SQL locking clause to the provided SQL query string based on the specified database driver
     * and the locking options (such as lock modes, SKIP LOCKED, NOWAIT). The behavior and syntax of the
     * appended clauses depend on the type of the database driver.
     *
     * @param sql    the {@code StringBuilder} to which the locking clause will be appended
     * @param driver the {@code DriverType} indicating the type of the database driver being used
     * @throws FeatureNotSupportedException if the specified locking mode or options are not supported by the given {@code driver}
     */
    private void appendLocking(StringBuilder sql, DriverType driver) {
        switch (driver) {
            case MYSQL, MARIADB -> {
                if (lockMode == LockMode.FOR_UPDATE) {
                    sql.append(" FOR UPDATE");
                } else if (lockMode == LockMode.FOR_SHARE) {
                    sql.append(" FOR SHARE");
                } else if (lockMode == LockMode.FOR_NO_KEY_UPDATE || lockMode == LockMode.FOR_KEY_SHARE) {
                    throw new FeatureNotSupportedException(driver);
                }
                if (skipLocked) {
                    throw new FeatureNotSupportedException(driver);
                }
                if (noWait) {
                    throw new FeatureNotSupportedException(driver);
                }
            }
            case POSTGRESQL -> {
                if (lockMode == null) return;
                switch (lockMode) {
                    case FOR_UPDATE -> sql.append(" FOR UPDATE");
                    case FOR_SHARE -> sql.append(" FOR SHARE");
                    case FOR_NO_KEY_UPDATE -> sql.append(" FOR NO KEY UPDATE");
                    case FOR_KEY_SHARE -> sql.append(" FOR KEY SHARE");
                }
                if (skipLocked) sql.append(" SKIP LOCKED");
                if (noWait) sql.append(" NOWAIT");
            }
            case SQLITE -> {
                throw new FeatureNotSupportedException(driver);
            }
            default -> throw new FeatureNotSupportedException(driver);
        }
    }

    /**
     * Sets a custom action to be executed after the query is completed. The specified action is
     * represented as a {@code RunnableAction<Boolean>} and can be used to define post-query logic.
     *
     * @param actionAfterQuery the action to perform after the query. This action takes a {@code Boolean}
     *                         parameter indicating the success or failure of the query execution.
     * @return the current {@code SelectQueryProvider} instance for method chaining.
     */
    public SelectQueryProvider actionAfterQuery(RunnableAction<QueryResult<SelectQueryProvider>> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    /**
     * Sets the table name for the SQL query.
     * This method specifies the table to be queried and updates the current
     * {@code SelectQueryProvider} instance for method chaining.
     *
     * @param table the name of the table to be queried
     * @return the current {@code SelectQueryProvider} instance for method chaining
     */
    public SelectQueryProvider table(String table) {
        this.table = table;
        return this;
    }

    /**
     * Sets the function to be used in the query. If the provided function is null,
     * it defaults to {@link SelectFunction#NORMAL}.
     *
     * @param function the {@link SelectFunction} to be used in the query;
     *                 if null, {@link SelectFunction#NORMAL} is set as the default
     * @return the current {@code SelectQueryProvider} instance for method chaining
     */
    public SelectQueryProvider function(SelectFunction function) {
        this.function = (function != null) ? function : SelectFunction.NORMAL;
        return this;
    }

    /**
     * Sets the type of selection for the query. If the provided selectType is null, a default value of SelectType.NORMAL is used.
     *
     * @param selectType the type of selection to be applied to the query
     * @return the current instance of SelectQueryProvider for method chaining
     */
    public SelectQueryProvider selectType(SelectType selectType) {
        this.selectType = (selectType != null) ? selectType : SelectType.NORMAL;
        return this;
    }

    /**
     * Modifies the current SelectQueryProvider by adding or merging the given order rules.
     *
     * @param order the Order object containing ordering rules to be applied.
     *              If null, no changes will be made.
     * @return the updated SelectQueryProvider instance.
     */
    public SelectQueryProvider order(Order order) {
        if (order == null) return this;
        if (this.order == null) this.order = order;
        else this.order.orderRules().putAll(order.orderRules());
        return this;
    }

    /**
     * Specifies the ordering rule for the query based on the provided key and direction.
     *
     * @param key       the field or column name to order by
     * @param direction the direction of the order (e.g., ascending or descending)
     * @return the current instance of SelectQueryProvider with the applied ordering rule
     */
    public SelectQueryProvider order(String key, Order.Direction direction) {
        if (this.order == null) this.order = new Order();
        this.order.rule(key, direction);
        return this;
    }

    /**
     * Adds a condition to the query with the specified column and value.
     *
     * @param column the name of the column to apply the condition to
     * @param value  the value to be matched for the given column
     * @return the current SelectQueryProvider instance to allow method chaining
     */
    public SelectQueryProvider condition(String column, Object value) {
        this.whereConditions.add(new Condition(column, value));
        return this;
    }

    /**
     * Adds a condition to the list of where conditions for the query.
     *
     * @param column   The name of the column to apply the condition on.
     * @param operator The operator to use for the condition (e.g., EQUALS, NOT_EQUALS, GREATER_THAN, etc.).
     * @param value    The value to be used in the condition.
     * @return The current {@code SelectQueryProvider} instance with the new condition added.
     */
    public SelectQueryProvider condition(String column, Operator operator, Object value) {
        this.whereConditions.add(new Condition(column, operator, value));
        return this;
    }

    /**
     * Adds a grouping of conditions to the query based on the specified type, negation, and conditions provided.
     *
     * @param type       the type of condition group (e.g., AND, OR) to be added.
     * @param not        whether the condition group should be negated.
     * @param conditions the conditions to include in the group.
     * @return the updated instance of {@code SelectQueryProvider}.
     */
    public SelectQueryProvider conditionGroup(Condition.Type type, boolean not, Condition... conditions) {
        condition(Condition.group(type, not, Arrays.asList(conditions)));
        return this;
    }

    /**
     * Adds a condition to the list of where conditions for the query.
     *
     * @param condition the condition to be added; if null, it will be ignored
     * @return the updated SelectQueryProvider instance
     */
    public SelectQueryProvider condition(Condition condition) {
        if (condition != null) this.whereConditions.add(condition);
        return this;
    }

    /**
     * Adds a group of conditions to the query using the specified condition type.
     *
     * @param type       the logical type of the condition group (e.g., AND, OR).
     * @param conditions the conditions to include in the group.
     * @return the current instance of {@code SelectQueryProvider} for method chaining.
     */
    public SelectQueryProvider conditionGroup(Condition.Type type, Condition... conditions) {
        condition(Condition.group(type, false, Arrays.asList(conditions)));
        return this;
    }

    /**
     * Adds a column key to the list of column keys for the query and returns the current instance.
     *
     * @param columnKey the column key to be added to the query
     * @return the current instance of SelectQueryProvider
     */
    public SelectQueryProvider columnKey(String columnKey) {
        if (this.columnKeys == null) this.columnKeys = new ArrayList<>();
        this.columnKeys.add(columnKey);
        return this;
    }

    /**
     * Specifies the columns to be included in the query by their keys.
     *
     * @param columnKeys the keys of the columns to be included in the query
     * @return the current instance of {@code SelectQueryProvider} for method chaining
     */
    public SelectQueryProvider columns(String... columnKeys) {
        if (this.columnKeys == null) this.columnKeys = new ArrayList<>();
        this.columnKeys.addAll(Arrays.asList(columnKeys));
        return this;
    }


    /**
     * Sets the maximum number of rows to be returned by the query.
     *
     * @param limit the maximum number of rows to return
     * @return the current instance of {@code SelectQueryProvider} with the limit applied
     */
    public SelectQueryProvider limit(int limit) {
        if (this.limit == null) this.limit = new Limit();
        this.limit.limit(limit);
        return this;
    }

    /**
     * Sets the limit and offset values for the query.
     *
     * @param limit  the maximum number of rows to retrieve
     * @param offset the starting position of the rows to retrieve
     * @return the updated SelectQueryProvider instance
     */
    public SelectQueryProvider limit(int limit, int offset) {
        if (this.limit == null) this.limit = new Limit();
        this.limit.limit(limit);
        this.limit.offset(offset);
        return this;
    }

    /**
     * Sets the group for the query.
     *
     * @param group the group to be applied to the query
     * @return the current instance of SelectQueryProvider for method chaining
     */
    public SelectQueryProvider group(Group group) {
        this.group = group;
        return this;
    }

    /**
     * Sets the action to be executed after the query has been processed.
     *
     * @param resultActionAfterQuery the action to perform with the {@code SimpleResultSet}
     *                               obtained from the query execution
     * @return the updated instance of {@code SelectQueryProvider}
     */
    public SelectQueryProvider resultActionAfterQuery(RunnableAction<SimpleResultSet> resultActionAfterQuery) {
        this.resultActionAfterQuery = resultActionAfterQuery;
        return this;
    }

    /**
     * Sets the ResultSet to be used by this SelectQueryProvider instance.
     *
     * @param resultSet the ResultSet object to be set
     * @return the current instance of SelectQueryProvider
     */
    public SelectQueryProvider simpleResultSet(SimpleResultSet resultSet) {
        this.simpleResultSet = resultSet;
        return this;
    }

    /**
     * Sets the alias for the specified column in the query.
     *
     * @param columnAlias the alias to be assigned to the column
     * @return the current instance of {@code SelectQueryProvider} for method chaining
     */
    public SelectQueryProvider columnAlias(String columnAlias) {
        this.columnAlias = columnAlias;
        return this;
    }

    /**
     * Sets the lock mode for the query and returns the updated query provider.
     *
     * @param mode the desired lock mode to be applied to the query
     * @return the updated SelectQueryProvider instance with the specified lock mode
     */
    public SelectQueryProvider lock(LockMode mode) {
        this.lockMode = mode;
        return this;
    }

    /**
     * Enables or disables the "skip locked" behavior for a query.
     * When enabled, locked rows are skipped rather than causing the query to wait.
     *
     * @param skip a boolean value; true to enable "skip locked", false to disable it.
     * @return the updated SelectQueryProvider instance with the modified "skip locked" setting.
     */
    public SelectQueryProvider skipLocked(boolean skip) {
        this.skipLocked = skip;
        return this;
    }

    /**
     * Sets the no-wait flag for the query.
     *
     * @param nw a boolean indicating whether the no-wait option should be enabled
     * @return the current instance of {@code SelectQueryProvider} for method chaining
     */
    public SelectQueryProvider noWait(boolean nw) {
        this.noWait = nw;
        return this;
    }

    /**
     * Retrieves the name of the table.
     *
     * @return the table name as a string
     */
    public String table() {
        return this.table;
    }

    /**
     * Retrieves the current SelectFunction instance associated with this object.
     *
     * @return the SelectFunction instance
     */
    public SelectFunction function() {
        return this.function;
    }

    /**
     * Returns the currently selected type.
     * This method provides the selected type for further processing or reference.
     *
     * @return the selected type as a SelectType enumeration or object.
     */
    public SelectType selectType() {
        return this.selectType;
    }

    /**
     * Returns the current order instance.
     *
     * @return the current order of type Order
     */
    public Order order() {
        return this.order;
    }

    /**
     * Retrieves the set of conditions currently stored in the object.
     *
     * @return a set of Condition objects representing the conditions.
     */
    public Set<Condition> conditions() {
        return this.whereConditions;
    }

    /**
     * Retrieves the list of column keys.
     *
     * @return a list of strings representing the column keys.
     */
    public List<String> columnKey() {
        return this.columnKeys;
    }

    /**
     * Retrieves the current limit.
     *
     * @return the limit associated with this object
     */
    public Limit limit() {
        return this.limit;
    }

    /**
     * Retrieves the action to be executed after a query.
     *
     * @return a RunnableAction of type SimpleResultSet representing the action to be performed.
     */
    public RunnableAction<SimpleResultSet> resultActionAfterQuery() {
        return this.resultActionAfterQuery;
    }

    /**
     * Retrieves the group associated with the current object.
     *
     * @return the group instance associated with this object
     */
    public Group group() {
        return this.group;
    }

    /**
     * Retrieves the current instance of the result set.
     *
     * @return the current ResultSet object.
     */
    public SimpleResultSet simpleResultSet() {
        return this.simpleResultSet;
    }

    /**
     * Defines the lock modes that can be used when interacting with a database
     * to control how data is locked during a transaction.
     * <p>
     * The lock modes determine the level of exclusivity or shared access
     * that is applied to the data rows affected by a query or operation.
     * <p>
     * This enumeration is commonly used in database-related operations where
     * concurrent access to data must be managed to ensure data consistency
     * and integrity.
     * <p>
     * Available lock modes:
     * - FOR_UPDATE: Locks the selected rows for updating.
     * - FOR_SHARE: Locks the selected rows for reading, allowing shared access.
     * - FOR_NO_KEY_UPDATE: Similar to FOR_UPDATE but avoids locking referenced keys.
     * - FOR_KEY_SHARE: Locks the selected rows, allowing shared access but keys cannot be updated.
     */
    public enum LockMode {
        /**
         * Represents the lock mode used to lock selected rows for updating.
         * <p>
         * This lock mode ensures that the rows returned by a query are locked
         * exclusively for the purpose of being updated. It prevents other transactions
         * from modifying or locking the same rows until the current transaction is completed.
         * <p>
         * Typically used in database transactions to enforce data consistency and avoid
         * conflicts during concurrent updates.
         */
        FOR_UPDATE,
        /**
         * Represents the lock mode used to lock selected rows for reading,
         * allowing shared access by multiple transactions.
         * <p>
         * This lock mode is commonly used in scenarios where multiple transactions
         * are allowed to read the same rows concurrently, but none are permitted
         * to modify the locked rows until the lock is released.
         * <p>
         * Typically applied to enforce consistency during read operations
         * without preventing other transactions from also reading the same data.
         */
        FOR_SHARE,
        /**
         * Represents a lock mode that locks selected rows to prevent modifications
         * to the rows themselves but allows updates that do not modify the referenced keys.
         * <p>
         * This lock mode is useful in scenarios where the primary intent is to avoid
         * conflicts on the data rows but still permit operations that alter related
         * foreign keys or associated data without directly updating the locked rows.
         * <p>
         * Typically used in database transactions to maintain consistency while
         * supporting limited concurrent updates in non-conflicting scenarios.
         */
        FOR_NO_KEY_UPDATE,
        /**
         * Represents a lock mode used to lock selected rows while allowing shared access,
         * with the restriction that the keys associated with the locked rows cannot be updated.
         * <p>
         * This lock mode is commonly used in scenarios where it is necessary to ensure
         * that multiple transactions can read the same rows concurrently, but any updates
         * to the keys of those rows are strictly prohibited until the lock is released.
         * <p>
         * Typically applied in database transactions to maintain key integrity while permitting
         * shared access for reading data among multiple transactions.
         */
        FOR_KEY_SHARE
    }
}

