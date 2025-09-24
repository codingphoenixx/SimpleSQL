package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.*;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.query.SimpleResultSet;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;
import lombok.experimental.Accessors;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The {@code SelectQueryProvider} class implements the {@link QueryProvider} interface
 * and is responsible for constructing and generating SQL SELECT queries.
 * <p>
 * This class provides additional functionality after executing a query by allowing
 * the assignment of a {@link RunnableAction} that processes the {@link ResultSet}.
 * It also includes mechanisms to store the retrieved {@link ResultSet}.
 */
public class SelectQueryProvider implements QueryProvider {

    /**
     * Represents the name of the database table to be queried.
     * This field is used to specify the table from which data will be selected
     * in a SQL SELECT query.
     */
    private String table;


    /**
     * The function for the select request. E.g. the amount of rows that will match the conditions.
     */
    private SelectFunction function = null;

    /**
     * Specifies the type of selection to be used in the SQL query.
     * It determines whether the query selects all matching rows (`NORMAL`)
     * or only distinct rows (`DISTINCT`).
     * <p>
     * The default value is {@code SelectType.NORMAL}.
     */
    private SelectType selectType = SelectType.NORMAL;

    /**
     * A collection of {@link Order} objects that define the sorting rules for a SQL SELECT query.
     * This set is used to store and manage multiple ordering criteria applied to the query results.
     * Each {@link Order} object represents a sorting rule with a specific column key and a sorting
     * direction (ascending or descending).
     */
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
    private List<String> columKey = new ArrayList<>();

    /**
     * The maximum of rows deleted by this request.
     */
    private Limit limit;

    /**
     * Sets a query offset for ordered select requests
     */
    private Offset offset;

    /**
     * A {@link Runnable} that will be executed after the query operation completes.
     * This can be used to perform additional processing or cleanup once the query
     * has been executed.
     */
    private RunnableAction<ResultSet> actionAfterQuery;

    /**
     * Represents the grouping configuration for the SQL query.
     * This field specifies a {@link Group} object that determines
     * how the rows should be grouped together in the resultant dataset.
     * Additionally, it can include complex conditions to be applied
     * post-grouping using the HAVING clause.
     */
    private Group group;


    /**
     * The ResultSet will be stored here after the request is executed.
     */
    private ResultSet resultSet;

    @Override
    public String generateSQLString(Query query) {
        if (columKey.isEmpty())
            columKey.add("*");

        Check.ifNullOrEmptyMap(table, "table name");

        StringBuilder sql = new StringBuilder("SELECT ");

        if (selectType != null && selectType == SelectType.DISTINCT)
            sql.append("DISTINCT ");

        if (function != null && !function.equals(SelectFunction.NORMAL)) {
            sql.append(function).append("(").append(columKey.get(0)).append(")");
        } else {
            sql.append(parseColumnName());
        }
        sql.append(" FROM ").append(table);

        if (group != null) {
            sql.append(group);
        }

        if (conditions != null && !conditions.isEmpty())
            sql.append(" WHERE ").append(parseCondition());

        if (order != null)
            sql.append(order.toString(query));

        if (limit != null)
            sql.append(limit);

        if (order != null && offset != null)
            sql.append(offset.toString());

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
            this.columKey = new ArrayList<>();
        }
        this.columKey.add(columKey);
        return this;
    }

    /**
     * Sets the {@link Order} of the table when the selection will occur
     *
     * @param key The key of the column the order will be assigned on.
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
            parsedCondition.append(condition.type().equals(Condition.Type.AND) ? " AND " : " OR ").append(condition.not() ? " NOT " : "").append(condition);
        }
        return parsedCondition.toString();
    }

    /**
     * Returns the name of the table associated with the query.
     *
     * @return the name of the table as a {@code String}
     */
    public String table() {
        return this.table;
    }

    /**
     * Retrieves the current SQL aggregation or selection function applied to the query.
     *
     * @return the {@link SelectFunction} representing the SQL function being used in the query.
     */
    public SelectFunction function() {
        return this.function;
    }

    /**
     * Retrieves the current {@link SelectType} associated with the query.
     *
     * @return the {@link SelectType} indicating whether the query is set to return
     *         all matching rows (NORMAL) or only unique rows (DISTINCT).
     */
    public SelectType selectType() {
        return this.selectType;
    }

    /**
     * Retrieves the current {@link Order} associated with the query,
     * specifying the sorting configuration applied during selection.
     *
     * @return the {@link Order} instance representing the current sorting order.
     */
    public Order order() {
        return this.order;
    }

    /**
     * Retrieves the set of conditions currently applied to this query.
     *
     * @return a {@link Set} of {@link Condition} instances representing the conditions
     *         used to filter results in the query.
     */
    public Set<Condition> conditions() {
        return this.conditions;
    }

    /**
     * Retrieves the list of column keys currently set for the query.
     *
     * @return a {@link List} of {@link String} objects representing the column keys.
     */
    public List<String> columKey() {
        return this.columKey;
    }

    /**
     * Retrieves the {@link Limit} instance associated with the current query configuration.
     * This object represents the SQL LIMIT clause applied to the query, controlling the
     * maximum number of rows returned and specifying an optional offset.
     *
     * @return the {@link Limit} instance that defines the row limit and offset values
     *         for the query.
     */
    public Limit limit() {
        return this.limit;
    }

    /**
     * Retrieves the {@link Offset} object associated with the current query configuration.
     * The offset represents the number of rows to be skipped in the query's result set,
     * typically used in conjunction with a LIMIT clause to implement pagination or control
     * the starting point of the result set.
     *
     * @return the {@link Offset} instance representing the number of rows to bypass in the query results.
     */
    public Offset offset() {
        return this.offset;
    }


    /**
     * Retrieves the {@link RunnableAction} to be executed after the query is completed.
     * This action is typically used for performing operations on the {@link ResultSet}
     * returned by the query.
     *
     * @return the {@link RunnableAction} instance configured to execute after the query,
     *         providing access to the resulting {@link ResultSet}.
     */
    public RunnableAction<ResultSet> actionAfterQuery() {
        return this.actionAfterQuery;
    }

    /**
     * Retrieves the {@link Group} instance associated with the current query.
     * The group defines the grouping clause applied to the query, commonly used
     * to group rows based on specified column values for aggregation or grouping operations.
     *
     * @return the {@link Group} instance representing the grouping configuration of the query.
     */
    public Group group() {
        return this.group;
    }

    /**
     * Retrieves the {@link ResultSet} associated with the current query execution.
     * The {@link ResultSet} contains the results of the query, holding the data
     * returned from the database based on the query configuration.
     *
     * @return the {@link ResultSet} representing the outcome of the executed query.
     */
    public ResultSet resultSet() {
        return this.resultSet;
    }

    public SimpleResultSet simpleResultSet() {
        if(resultSet == null)
            throw new NullPointerException("ResultSet is null");
        return new SimpleResultSet(resultSet());
    }

    /**
     * Specifies the name of the table to query.
     *
     * @param table The name of the table to be used in the query.
     * @return {@link SelectQueryProvider} for chaining, allowing further query configuration.
     */
    public SelectQueryProvider table(String table) {
        this.table = table;
        return this;
    }

    /**
     * Sets the SQL aggregation or selection function for the query.
     * The {@link SelectFunction} specifies the function (e.g., MAX, MIN, COUNT)
     * to be applied during the query execution.
     *
     * @param function The {@link SelectFunction} to be applied to the query.
     * @return {@link SelectQueryProvider} for chaining, allowing further configuration of the query.
     */
    public SelectQueryProvider function(SelectFunction function) {
        this.function = function;
        return this;
    }

    /**
     * Sets the {@link SelectType} for the query, which determines whether the query
     * will return all matching rows (NORMAL) or only unique rows (DISTINCT).
     *
     * @param selectType The {@link SelectType} to apply to the query. Options include:
     *                   {@code NORMAL} for all matching rows or {@code DISTINCT} for unique rows.
     * @return {@link SelectQueryProvider} for chaining, allowing further query configuration.
     */
    public SelectQueryProvider selectType(SelectType selectType) {
        this.selectType = selectType;
        return this;
    }

    /**
     * Sets the {@link Order} for the query, which determines the sorting configuration
     * to be applied during the selection of results.
     *
     * @param order The {@link Order} instance specifying the sorting rules for the query.
     * @return {@link SelectQueryProvider} for chaining, enabling further configuration of the query.
     */
    public SelectQueryProvider order(Order order) {
        this.order = order;
        return this;
    }

    /**
     * Sets the offset value for the query and returns the updated SelectQueryProvider.
     *
     * @param offset the Offset object representing the offset value in the query
     * @return the updated SelectQueryProvider instance with the specified offset applied
     */
    public SelectQueryProvider offset(Offset offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Sets the action to be executed after the query is completed. This action is typically
     * used for processing the {@link ResultSet} returned by the query execution.
     *
     * @param actionAfterQuery the {@link RunnableAction} instance to execute after the query,
     *                         providing access to the resulting {@link ResultSet}.
     * @return {@link SelectQueryProvider} for chaining, allowing further query configuration.
     */
    public SelectQueryProvider actionAfterQuery(RunnableAction<ResultSet> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    /**
     * Sets the {@link Group} instance for the query. The group defines the grouping clause
     * used for aggregation or grouping operations, which group rows based on specified
     * column values.
     *
     * @param group The {@link Group} instance representing the grouping configuration to be applied.
     * @return {@link SelectQueryProvider} for chaining, allowing further query customization.
     */
    public SelectQueryProvider group(Group group) {
        this.group = group;
        return this;
    }

    /**
     * Sets the provided ResultSet to the SelectQueryProvider instance.
     *
     * @param resultSet the ResultSet to be used in the SelectQueryProvider
     * @return the current instance of SelectQueryProvider with the specified ResultSet
     */
    public SelectQueryProvider resultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
        return this;
    }
}
