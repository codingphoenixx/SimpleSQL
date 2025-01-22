package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.Condition;
import dev.coph.simplesql.database.attributes.Limit;
import dev.coph.simplesql.database.attributes.Operator;
import dev.coph.simplesql.database.attributes.Order;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;


/**
 * A {@link QueryProvider} that requests a deletion of a table
 */
@Getter
@Accessors(fluent = true)
public class DeleteQueryProvider implements QueryProvider {

    /**
     * The name of the table that should be deleted.
     */
    @Setter
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
            parsedCondition.append(condition.type().equals(Condition.Type.AND) ? " AND " : " OR ").append(condition);
        }
        return parsedCondition.toString();
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
     * @param key       The key of the column the order will be assigned on.
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
}
