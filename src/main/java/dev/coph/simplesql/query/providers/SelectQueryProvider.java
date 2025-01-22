package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.*;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.Check;
import dev.coph.simplesql.utils.RunnableAction;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@code SelectQueryProvider} class implements the {@link QueryProvider} interface
 * and is responsible for constructing and generating SQL SELECT queries.
 * <p>
 * This class provides additional functionality after executing a query by allowing
 * the assignment of a {@link RunnableAction} that processes the {@link ResultSet}.
 * It also includes mechanisms to store the retrieved {@link ResultSet}.
 */
@Getter
@Accessors(fluent = true, chain = true)
public class SelectQueryProvider implements QueryProvider {

    @Setter
    private String table;


    /**
     * The function for the select request. E.g. the amount of rows that will match the conditions.
     */
    @Setter
    private SelectFunction selectFunction = SelectFunction.NORMAL;

    /**
     * A collection of {@link Order} objects that define the sorting rules for a SQL SELECT query.
     * This set is used to store and manage multiple ordering criteria applied to the query results.
     * Each {@link Order} object represents a sorting rule with a specific column key and a sorting
     * direction (ascending or descending).
     */
    @Setter
    private Order order;

    /**
     * A list of conditions that must be matched in order to delete a row.
     */
    private Set<Condition> conditions = new HashSet<>();

    /**
     * A collection of column keys that are used to specify which columns
     * should be included in the query operation. This helps in defining
     * or restricting the specific columns to be utilized for data retrieval or manipulation.
     * If no value is present on execution the default value of {@code *} will be used.
     */
    private Set<String> columKey = new HashSet<>();

    /**
     * The maximum of rows deleted by this request.
     */
    private Limit limit;

    /**
     * A {@link Runnable} that will be executed after the query operation completes.
     * This can be used to perform additional processing or cleanup once the query
     * has been executed.
     */
    @Setter
    private RunnableAction<ResultSet> actionAfterQuery;


    /**
     * The ResultSet will be stored here after the request is executed.
     */
    @Setter
    private ResultSet resultSet;

    @Override
    public String generateSQLString(Query query) {
        if (columKey.isEmpty())
            columKey.add("*");

        Check.ifNullOrEmptyMap(table, "table name");

        StringBuilder sql = new StringBuilder("SELECT ");
        if (!selectFunction.equals(SelectFunction.NORMAL) && !isStarRequest()) {
            sql.append(selectFunction.function()).append("(").append(parseColumnName()).append(")");
        } else {
            sql.append(parseColumnName());
        }
        sql.append(" FROM ").append(table);

        if (conditions != null && !conditions.isEmpty())
            sql.append(" WHERE ").append(parseCondition());

        if (order != null)
            sql.append(order.toString(query));

        if (limit != null)
            sql.append(limit);

        sql.append(";");


        return sql.toString();
    }


    /**
     * Sets the limit of rows that should be selected by this request.
     *
     * @param limit The maximum rows that can be deleted.
     * @return {@link SelectQueryProvider} for chaining.
     */
    public SelectQueryProvider limit(int limit) {
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
     * @return {@link SelectQueryProvider} for chaining.
     */
    public SelectQueryProvider limit(int limit, int offset) {
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
     * @return {@link SelectQueryProvider} for chaining.
     */
    public SelectQueryProvider condition(Condition condition) {
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
     * @return {@link SelectQueryProvider} for chaining.
     */
    public SelectQueryProvider condition(String key, Object value) {
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
     * @return {@link SelectQueryProvider} for chaining, enabling further configuration of the query.
     */
    public SelectQueryProvider condition(String key, Operator operator, Object value) {
        if (conditions == null) {
            conditions = new HashSet<>();
        }
        conditions.add(new Condition(key, operator, value));
        return this;
    }


    /**
     * Adds a key for the values that you want from the table
     *
     * @param columKey The key of the column
     * @return {@link SelectQueryProvider} for chaining.
     */
    public SelectQueryProvider columKey(String columKey) {
        if (this.columKey == null) {
            this.columKey = new HashSet<>();
        }
        this.columKey.add(columKey);
        return this;
    }

    /**
     * Sets the {@link Order} of the table when the selection will occur
     *
     * @param key       The key of the column the order will be assigned on.
     * @return {@link SelectQueryProvider} for chaining.
     */
    public SelectQueryProvider orderBy(String key) {
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
     * @return {@link SelectQueryProvider} for chaining.
     */
    public SelectQueryProvider orderBy(String key, Order.Direction direction) {
        if (order == null)
            order = new Order();
        order.rule(key, direction);
        return this;
    }


    /**
     * Checks if the request is a star request
     *
     * @return the result of the check
     */
    private boolean isStarRequest() {
        if (columKey == null || columKey.size() != 1)
            return false;
        return columKey.toArray(new String[]{})[0].equals("*");
    }


    /**
     * Parsed the column names to a string for executing
     *
     * @return the sql string
     */
    private String parseColumnName() {
        if (columKey.isEmpty())
            return "";
        if (columKey.size() == 1)
            return columKey.toArray(new String[]{})[0];
        StringBuilder parsedName = null;
        for (String name : columKey) {
            if (parsedName == null) {
                parsedName = new StringBuilder(name);
                continue;
            }
            parsedName.append(", ").append(name);
        }
        return parsedName.toString();
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


}
