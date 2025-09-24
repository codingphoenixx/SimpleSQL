package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.Condition;
import dev.coph.simplesql.database.attributes.Limit;
import dev.coph.simplesql.database.attributes.Operator;
import dev.coph.simplesql.database.attributes.Order;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.HashSet;
import java.util.Set;


/**
 * Provides functionality to generate SQL DELETE queries. This class allows you to specify
 * the table to delete from, apply conditions for deletion, set limits on the number of rows
 * to be deleted, and define the order of rows to be affected.
 * <p>
 * This class implements the {@link QueryProvider} interface to generate SQL strings
 * for a DELETE query operation.
 */
public class DeleteQueryProvider implements QueryProvider {

    /**
     * The name of the table that should be deleted.
     */
    private String table;
    /**
     * The {@link Order} of the table when the deleting will occur.
     */
    private Order order;
    /**
     * A list of conditions that must be matched in order to delete a row.
     */
    private Set<Condition> conditions;
    /**
     * The maximum of rows deleted by this request.
     */
    private Limit limit;

    private RunnableAction<Boolean> actionAfterQuery;

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(table, "table name");

        StringBuilder sql = new StringBuilder("DELETE FROM ").append(table);

        if (conditions != null && !conditions.isEmpty())
            sql.append(" WHERE ").append(parseCondition());

        if (order != null)
            sql.append(order);

        if (limit != null)
            sql.append(limit);

        sql.append(";");

        return sql.toString();
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }


    /**
     * Parsed the conditions to a string for executing
     *
     * @return the sql string
     */
    private String parseCondition() {
        if (conditions.isEmpty())
            return "";
        if (conditions.size() == 1)
            return conditions.toArray(new Condition[]{})[0].toString();
        StringBuilder parsedCondition = null;
        for (Condition condition : conditions) {
            if (parsedCondition == null) {
                parsedCondition = new StringBuilder(condition.not() ? " NOT " : "").append(condition);
                continue;
            }
            parsedCondition.append(condition.type().equals(Condition.Type.AND) ? " AND " : " OR ").append(condition.not() ? " NOT " : "").append(condition);
        }
        return parsedCondition.toString();
    }


    public DeleteQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    /**
     * Sets the limit of rows that should be selected by this request.
     *
     * @param limit The maximum rows that can be deleted.
     * @return {@link DeleteQueryProvider} for chaining.
     */
    public DeleteQueryProvider limit(int limit) {
        if (this.limit == null) {
            this.limit = new Limit();
        }
        this.limit.limit(limit);
        return this;
    }

    /**
     * Sets the limit and offset for the query, restricting the number of rows returned
     * and specifying the starting point.
     *
     * @param limit  The maximum number of rows to include in the result.
     * @param offset The starting position of the rows to be returned.
     * @return {@link DeleteQueryProvider} for chaining.
     */
    public DeleteQueryProvider limit(int limit, int offset) {
        if (this.limit == null) {
            this.limit = new Limit();
        }
        this.limit.limit(limit);
        this.limit.offset(offset);
        return this;
    }


    /**
     * Adds and condition that a row must match in order to get selected.
     *
     * @param condition The condition
     * @return {@link DeleteQueryProvider} for chaining.
     */
    public DeleteQueryProvider condition(Condition condition) {
        if (conditions == null) {
            conditions = new HashSet<>();
        }
        conditions.add(condition);
        return this;
    }

    /**
     * Adds and condition that a row must match in order to get selected. By column key and value.
     *
     * @param key   The key of the column for the condition
     * @param value The value of the row that must match with the given row key
     * @return {@link DeleteQueryProvider} for chaining.
     */
    public DeleteQueryProvider condition(String key, Object value) {
        if (conditions == null) {
            conditions = new HashSet<>();
        }
        conditions.add(new Condition(key, value));
        return this;
    }

    /**
     * Adds a condition to the query specifying that a row must match based on the given key, value, and operator.
     * This method allows for the inclusion of complex conditions by combining a key, value, and a specific operator.
     *
     * @param key      The name of the column that the condition will apply to.
     * @param value    The value that the column's data will be compared against.
     * @param operator The operator used for comparison.
     * @return {@link DeleteQueryProvider} for chaining, enabling further configuration of the query.
     */
    public DeleteQueryProvider condition(String key, Operator operator, Object value) {
        if (conditions == null) {
            conditions = new HashSet<>();
        }
        conditions.add(new Condition(key, operator, value));
        return this;
    }


    /**
     * Sets the {@link Order} of the table when the selection will occur
     *
     * @param key The key of the column the order will be assigned on.
     * @return {@link DeleteQueryProvider} for chaining.
     */
    public DeleteQueryProvider orderBy(String key) {
        if (order == null)
            order = new Order();
        order.rule(key, Order.Direction.ASCENDING);
        return this;
    }

    /**
     * Sets the {@link Order} of the table when the selection will occur
     *
     * @param key       The key of the column the order will be assigned on.
     * @param direction The direction of the sorting.
     * @return {@link DeleteQueryProvider} for chaining.
     */
    public DeleteQueryProvider orderBy(String key, Order.Direction direction) {
        if (order == null)
            order = new Order();
        order.rule(key, direction);
        return this;
    }

    /**
     * Retrieves the name of the table associated with this query provider.
     *
     * @return the name of the table
     */
    public String table() {
        return this.table;
    }

    /**
     * Retrieves the {@link Order} object associated with this query provider.
     * The {@link Order} defines sorting rules for query results, including
     * the fields to sort by and their respective sorting directions.
     *
     * @return the {@link Order} object defining the sorting rules for the query,
     * or {@code null} if no sorting rules have been set.
     */
    public Order order() {
        return this.order;
    }

    /**
     * Retrieves the set of conditions associated with this query provider.
     * These conditions define the criteria that must be met for a row to be affected
     * by the query.
     *
     * @return a set of {@link Condition} objects representing the query's conditions
     */
    public Set<Condition> conditions() {
        return this.conditions;
    }

    /**
     * Retrieves the {@link Limit} object associated with this query provider.
     * The {@link Limit} object defines the criteria for limiting the number of rows
     * returned or affected by the query and may include an optional offset value.
     *
     * @return the {@link Limit} object representing the limit criteria for the query.
     */
    public Limit limit() {
        return this.limit;
    }

    /**
     * Sets the table name for the DELETE query.
     *
     * @param table The name of the table to perform the DELETE operation on.
     * @return {@link DeleteQueryProvider} for chaining, allowing further configuration of the query.
     */
    public DeleteQueryProvider table(String table) {
        this.table = table;
        return this;
    }
}
