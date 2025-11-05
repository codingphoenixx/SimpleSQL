package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.Condition;
import dev.coph.simplesql.database.attributes.Limit;
import dev.coph.simplesql.database.attributes.Operator;
import dev.coph.simplesql.database.attributes.Order;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.*;

/**
 * The DeleteQueryProvider class is an implementation of the QueryProvider interface,
 * which is responsible for generating and configuring SQL DELETE queries.
 * It supports defining table names, conditions, ordering, and limits for the query.
 * <p>
 * The class allows building a DELETE query in a programmatic manner by chaining methods
 * to configure various aspects of the query. It also provides compatibility checks for
 * database drivers as well as support for generating parameterized SQL query strings.
 */
public class DeleteQueryProvider implements QueryProvider {

    private String table;
    private Order order;
    private LinkedHashSet<Condition> conditions;
    private Limit limit;

    private RunnableAction<Boolean> actionAfterQuery;

    private List<Object> boundParams = List.of();

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(table, "table name");

        DriverType driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        StringBuilder sql = new StringBuilder("DELETE FROM ").append(table);

        List<Object> params = new ArrayList<>();

        if (conditions != null && !conditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(buildConditions(conditions.iterator(), params));
        }

        if (order != null && order.orderRules() != null && !order.orderRules().isEmpty()) {
            DatabaseCheck.requireDriver(driver, DriverType.MYSQL, DriverType.MARIADB);
            sql.append(order.toString(query));
        }

        if (limit != null) {
            DatabaseCheck.requireDriver(driver, DriverType.MYSQL, DriverType.MARIADB);

            int lim = limit.limit();
            int off = limit.offset();

            if (off > 0) {
                if (lim <= 0) {
                    throw new FeatureNotSupportedException(driver);
                }
                if (order == null || order.orderRules() == null || order.orderRules().isEmpty()) {
                    throw new FeatureNotSupportedException(driver);
                }
                sql.append(" LIMIT ").append(off).append(", ").append(lim);
            } else if (lim > 0) {
                sql.append(" LIMIT ").append(lim);
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
     * Builds the SQL conditions string based on the provided conditions iterator.
     * Combines conditions using logical operators (AND, OR) and handles condition negation (NOT).
     * Adds parameters to the provided list for non-literal values in conditions.
     *
     * @param it     an iterator of {@link Condition} objects representing the conditions to be used
     * @param params a list to which the values of non-literal conditions will be added for parameterized queries
     * @return a string representing the SQL conditions to be appended in a WHERE clause
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

            String column = c.key();
            Operator op = c.operator();
            Object value = c.value();

            switch (op) {
                case IS_NULL -> sb.append(column).append(" IS NULL");
                case IS_NOT_NULL -> sb.append(column).append(" IS NOT NULL");
                default -> {
                    sb.append(column).append(" ").append(op.operator()).append(" ?");
                    params.add(value);
                }
            }

            first = false;
        }
        return sb.toString();
    }


    @Override
    public List<Object> parameters() {
        return boundParams != null ? boundParams : List.of();
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }

    /**
     * Sets the action to be executed after a query is executed.
     *
     * @param actionAfterQuery the {@code RunnableAction<Boolean>} to be executed post-query,
     *                         where the Boolean parameter represents the success or failure of the query
     * @return the current {@code DeleteQueryProvider} instance for method chaining
     */
    public DeleteQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    /**
     * Sets the table name to be used in the DELETE query.
     *
     * @param table the name of the table on which the DELETE query will operate
     * @return the current {@code DeleteQueryProvider} instance for method chaining
     */
    public DeleteQueryProvider table(String table) {
        this.table = table;
        return this;
    }

    /**
     * Sets the limit for the DELETE query to specify the maximum number of rows to be affected.
     * Allows method chaining by returning the current instance.
     *
     * @param limit the maximum number of rows to be affected by the DELETE query
     * @return the current {@code DeleteQueryProvider} instance for method chaining
     */
    public DeleteQueryProvider limit(int limit) {
        if (this.limit == null) this.limit = new Limit();
        this.limit.limit(limit);
        return this;
    }

    /**
     * Sets a limit and an offset for the DELETE query to specify the maximum number of rows
     * to be affected and the starting point from which rows are counted. If no limit exists,
     * a new {@code Limit} instance is created.
     *
     * @param limit  the maximum number of rows to be affected by the DELETE query
     * @param offset the starting point to begin counting rows
     * @return the current {@code DeleteQueryProvider} instance for method chaining
     */
    public DeleteQueryProvider limit(int limit, int offset) {
        if (this.limit == null) this.limit = new Limit();
        this.limit.limit(limit);
        this.limit.offset(offset);
        return this;
    }

    /**
     * Adds an ordering rule to the DELETE query, specifying the column to sort by
     * in ascending order. If no ordering rules exist, a new {@code Order} instance
     * is created to store the rule.
     *
     * @param key the name of the column to order by
     * @return the current {@code DeleteQueryProvider} instance for method chaining
     */
    public DeleteQueryProvider orderBy(String key) {
        if (order == null) order = new Order();
        order.rule(key, Order.Direction.ASCENDING);
        return this;
    }

    /**
     * Adds an ordering rule to the DELETE query, specifying the column to sort by
     * and the sorting direction (ascending or descending). If no ordering rules
     * exist, a new {@code Order} instance is created to store the rule.
     *
     * @param key       the name of the column to order by
     * @param direction the sorting direction, either {@code Order.Direction.ASCENDING} or {@code Order.Direction.DESCENDING}
     * @return the current {@code DeleteQueryProvider} instance for method chaining
     */
    public DeleteQueryProvider orderBy(String key, Order.Direction direction) {
        if (order == null) order = new Order();
        order.rule(key, direction);
        return this;
    }

    /**
     * Adds a condition to the DELETE query. If no conditions exist, a new
     * {@code LinkedHashSet} is initialized to store conditions.
     *
     * @param condition the condition to be added to the DELETE query
     * @return the current {@code DeleteQueryProvider} instance for method chaining
     */
    public DeleteQueryProvider condition(Condition condition) {
        if (conditions == null) conditions = new LinkedHashSet<>();
        conditions.add(condition);
        return this;
    }

    /**
     * Adds a condition to the DELETE query based on the specified key and value.
     * If no conditions exist, a new {@code LinkedHashSet} is initialized to store conditions.
     *
     * @param key   the name of the column or attribute for the condition
     * @param value the value to be compared in the condition
     * @return the current {@code DeleteQueryProvider} instance for method chaining
     */
    public DeleteQueryProvider condition(String key, Object value) {
        if (conditions == null) conditions = new LinkedHashSet<>();
        conditions.add(new Condition(key, value));
        return this;
    }

    /**
     * Adds a condition to the DELETE query based on the specified key, operator, and value.
     * If no conditions exist, a new {@code LinkedHashSet} is initialized to store conditions.
     *
     * @param key      the name of the column or attribute for the condition
     * @param operator the operator to be used for the condition (e.g., EQUALS, NOT_EQUALS, GREATER_THAN)
     * @param value    the value to be compared in the condition
     * @return the current {@code DeleteQueryProvider} instance for method chaining
     */
    public DeleteQueryProvider condition(String key, Operator operator, Object value) {
        if (conditions == null) conditions = new LinkedHashSet<>();
        conditions.add(new Condition(key, operator, value));
        return this;
    }

    /**
     * Retrieves the name of the table associated with the DELETE query.
     *
     * @return the table name as a {@code String}
     */
    public String table() {
        return this.table;
    }

    /**
     * Retrieves the {@code Order} configuration associated with the DELETE query.
     *
     * @return the {@code Order} instance defining the ordering rules for the query
     */
    public Order order() {
        return this.order;
    }

    /**
     * Retrieves the set of conditions associated with the DELETE query.
     *
     * @return a {@code Set<Condition>} representing the conditions that define the query's constraints
     */
    public Set<Condition> conditions() {
        return this.conditions;
    }

    /**
     * Retrieves the {@code Limit} configuration associated with the DELETE query.
     *
     * @return the {@code Limit} instance defining the row limit and offset for the query
     */
    public Limit limit() {
        return this.limit;
    }
}
