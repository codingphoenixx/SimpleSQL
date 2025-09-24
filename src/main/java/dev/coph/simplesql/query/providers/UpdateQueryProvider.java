package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.Condition;
import dev.coph.simplesql.database.attributes.Limit;
import dev.coph.simplesql.database.attributes.Operator;
import dev.coph.simplesql.database.attributes.UpdatePriority;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryEntry;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * The UpdateQueryProvider class facilitates the creation and configuration of SQL
 * "UPDATE" queries. It allows developers to define parameters, conditions, and
 * constraints required to construct a valid update query for a database.
 * This class provides a fluent API for chaining method calls to build complex
 * SQL update statements.
 * <p>
 * The generated SQL update query supports the following:
 * - Defining the target table for the update operation.
 * - Specifying priority levels for the execution of the query.
 * - Adding "IGNORE" directives to handle errors during update.
 * - Incorporating multiple conditions in the "WHERE" clause for targeted updates.
 * - Setting column-value pairs to modify specific records.
 * - Applying "LIMIT" clauses for constraining the number of affected rows.
 * <p>
 * This implementation adheres to the QueryProvider interface, ensuring
 * compatibility with a broader query management system.
 */
public class UpdateQueryProvider implements QueryProvider {
    /**
     * Represents the name of the table to be updated in the SQL query.
     * This variable specifies the target table where the update operation
     * will be performed.
     */
    private String table;
    /**
     * Specifies the priority level for executing update operations.
     * The priority can influence the order or urgency of the update execution.
     * <p>
     * This variable is initialized to {@link UpdatePriority#NORMAL} by default.
     * Possible values are defined in the {@link UpdatePriority} enum, which include:
     * - {@code LOW}: Represents a low-priority update operation.
     * - {@code NORMAL}: Represents a standard-priority update operation.
     * <p>
     * This field is mutable and can be modified to suit the specific requirements
     * of the query execution context.
     */
    private UpdatePriority updatePriority = UpdatePriority.NORMAL;

    /**
     * Indicates whether to ignore errors while updating rows in the SQL query.
     * If set to true, the SQL update operation may skip specific rows or conditions when an error occurs.
     */
    private boolean updateIgnore = false;

    /**
     * Represents a list of entries that define the columns and their corresponding values
     * for the update query. Each entry in the list corresponds to a specific column and
     * the value that it should be updated to in the update operation.
     */
    private List<QueryEntry> entries;

    /**
     * A collection of {@link Condition} objects representing the constraints or filters
     * that must be applied in the SQL update query. These conditions define the criteria
     * for selecting rows to be updated.
     * <p>
     * This field is used to build the "WHERE" clause of the update query. Conditions
     * typically include comparisons using column names, values, and operators such as
     * EQUALS, GREATER_THAN, LESS_THAN, etc.
     */
    private Set<Condition> conditions;

    /**
     * Represents the {@link Limit} clause used to restrict the number of rows affected
     * by an update operation. The limit defines the maximum number of rows processed,
     * with an optional offset to specify a starting point.
     */
    private Limit limit;
    private RunnableAction<Boolean> actionAfterQuery;

    /**
     * Sets the maximum number of rows to be affected by the update query using a LIMIT clause.
     * This method allows chaining to facilitate further modifications to the query.
     *
     * @param limit The maximum number of rows to be affected. Must be a non-negative integer.
     *              A value of 0 or negative may indicate no limitation, depending on the SQL dialect used.
     * @return {@link UpdateQueryProvider} for chaining, allowing further query modifications.
     */
    public UpdateQueryProvider limit(int limit) {
        if (this.limit == null) {
            this.limit = new Limit();
        }
        this.limit.limit(limit);
        return this;
    }

    /**
     * Sets the limit and offset for the update query, restricting the number of rows to be affected
     * and specifying the starting point for the operation.
     *
     * @param limit  The maximum number of rows to be affected by the query.
     * @param offset The starting position of the rows to be affected.
     * @return {@link UpdateQueryProvider} for chaining, allowing further query modifications.
     */
    public UpdateQueryProvider limit(int limit, int offset) {
        if (this.limit == null) {
            this.limit = new Limit();
        }
        this.limit.limit(limit);
        this.limit.offset(offset);
        return this;
    }

    /**
     * Adds a condition that must be satisfied for the update operation to apply.
     * This method allows chaining to build a complex query with multiple conditions.
     *
     * @param condition The condition to be added. It specifies the criteria that must
     *                  be met for a row to be updated.
     * @return {@link UpdateQueryProvider} for chaining, enabling further query modifications.
     */
    public UpdateQueryProvider condition(Condition condition) {
        if (conditions == null) {
            conditions = new HashSet<>();
        }
        conditions.add(condition);
        return this;
    }

    /**
     * Adds a condition to the update query based on a specified column and value.
     * The condition specifies that rows must match the given column's value to be updated.
     *
     * @param column The name of the column for which the condition is applied.
     * @param value  The value to be matched for the specified column.
     * @return {@link UpdateQueryProvider} for chaining, enabling further query modifications.
     */
    public UpdateQueryProvider condition(String column, Object value) {
        if (conditions == null) {
            conditions = new HashSet<>();
        }
        conditions.add(new Condition(column, value));
        return this;
    }

    /**
     * Adds a condition to the update query based on a specified column, operator, and value.
     * The condition specifies the criteria that must be met for rows to be updated.
     *
     * @param column   The name of the column for which the condition is applied.
     * @param operator The comparison operator (e.g., equals, greater than) to be used in the condition.
     * @param value    The value to be matched against the column using the specified operator.
     * @return {@link UpdateQueryProvider} for chaining, enabling further query modifications.
     */
    public UpdateQueryProvider condition(String column, Operator operator, Object value) {
        if (conditions == null) {
            conditions = new HashSet<>();
        }
        conditions.add(new Condition(column, operator, value));
        return this;
    }

    /**
     * Adds a new entry to the update query. The entry specifies a column
     * and its corresponding value to be updated in the target table.
     *
     * @param column The name of the column to be updated.
     * @param value  The value to be set for the specified column.
     * @return {@link UpdateQueryProvider} for chaining, enabling further modifications to the query.
     */
    public UpdateQueryProvider entry(String column, Object value) {
        if (entries == null) {
            entries = new ArrayList<>();
        }
        entries.add(new QueryEntry(column, value));
        return this;
    }

    /**
     * Adds a new entry to the update query. The entry specifies a column
     * and its corresponding value to be updated in the target table.
     *
     * @param entry The finished generated entry
     * @return {@link UpdateQueryProvider} for chaining, enabling further modifications to the query.
     */
    public UpdateQueryProvider entry(QueryEntry entry) {
        if (entries == null) {
            entries = new ArrayList<>();
        }
        entries.add(entry);
        return this;
    }

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(table, "table name");

        StringBuilder sql = new StringBuilder("UPDATE");

        if (updatePriority == UpdatePriority.LOW) {
            if (query.databaseAdapter().driverType() == DriverType.SQLITE) {
                try {
                    throw new UnsupportedOperationException("SQLite does not support priorities when updating Databases. Ignoring attribute...");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sql.append(" LOW_PRIORITY");
            }
        }

        if (updateIgnore)
            sql.append(" IGNORE");

        sql.append(" ").append(table).append(" SET ");

        StringBuilder updateString = null;
        for (QueryEntry entry : entries) {
            if (updateString == null) {
                updateString = new StringBuilder(entry.columName()).append("=").append(entry.sqlValue());
            } else {
                updateString.append(", ").append(entry.columName()).append("=").append(entry.sqlValue());
            }
        }
        sql.append(" ").append(updateString);

        if (conditions != null && !conditions.isEmpty())
            sql.append(" WHERE ").append(parseCondition());

        if (limit != null)
            sql.append(limit);

        sql.append(";");
        return sql.toString();
    }

    /**
     * Parses and constructs a string representation of the conditions
     * included in the update query. The method combines all specified
     * conditions with the appropriate logical operator (AND/OR) and
     * applies a "NOT" prefix where applicable.
     * <p>
     * If no conditions are present, an empty string is returned.
     * If there is only one condition, it directly returns its string
     * representation.
     *
     * @return A string representation of the combined conditions for
     * the update query, or an empty string if no conditions
     * are specified.
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
     * Retrieves the name of the table targeted by the update query.
     *
     * @return The name of the table as a String.
     */
    public String table() {
        return this.table;
    }

    /**
     * Retrieves the current update priority of the query.
     *
     * @return The {@link UpdatePriority} indicating the priority level for the update operation.
     */
    public UpdatePriority updatePriority() {
        return this.updatePriority;
    }

    /**
     * Indicates whether the update operation should ignore duplicate key errors or conflicts.
     * This flag is typically used to control the behavior of updates when potential violations
     * of unique constraints or key conflicts occur.
     *
     * @return A boolean value. Returns true if the update should ignore conflicts or duplicate key errors;
     * false otherwise.
     */
    public boolean updateIgnore() {
        return this.updateIgnore;
    }

    /**
     * Retrieves a list of query entries representing the column-value pairs to be updated in the query.
     *
     * @return A list of {@link QueryEntry} objects, where each entry represents a column and its corresponding value
     * to be updated in the query.
     */
    public List<QueryEntry> entries() {
        return this.entries;
    }

    /**
     * Retrieves the set of conditions that define the criteria for the update query.
     * These conditions specify the rules that rows must satisfy to be included in the update operation.
     *
     * @return A Set of {@link Condition} objects representing the conditions for the update query.
     * If no conditions are specified, this returns an empty set.
     */
    public Set<Condition> conditions() {
        return this.conditions;
    }

    /**
     * Retrieves the {@link Limit} object representing the maximum number
     * of rows affected by the update query.
     *
     * @return The current {@link Limit} associated with the update query. If no limit has been set, it may return null or
     * a default {@link Limit}, depending on the implementation.
     */
    public Limit limit() {
        return this.limit;
    }

    /**
     * Sets the target table for the update query.
     * This method allows chaining to facilitate further modifications to the query.
     *
     * @param table The name of the table to be updated. Must not be null or empty.
     * @return {@link UpdateQueryProvider} for chaining, allowing further query modifications.
     */
    public UpdateQueryProvider table(String table) {
        this.table = table;
        return this;
    }

    /**
     * Sets the update priority for the query, which defines the level of priority (e.g., LOW or NORMAL)
     * to be associated with the update operation. This method allows chaining for further query modifications.
     *
     * @param updatePriority The {@link UpdatePriority} representing the desired priority level for the update operation.
     *                       Must not be null.
     * @return {@link UpdateQueryProvider} for chaining, enabling further modifications to the query.
     */
    public UpdateQueryProvider updatePriority(UpdatePriority updatePriority) {
        this.updatePriority = updatePriority;
        return this;
    }

    /**
     * Sets whether the update operation should ignore conflicts or duplicate key errors.
     * This method modifies the update query's behavior when such conflicts occur.
     *
     * @param updateIgnore A boolean value. Pass true to enable ignoring conflicts or
     *                     duplicate key errors; false to disable.
     * @return {@link UpdateQueryProvider} for chaining, enabling further modifications
     * to the update query.
     */
    public UpdateQueryProvider updateIgnore(boolean updateIgnore) {
        this.updateIgnore = updateIgnore;
        return this;
    }

    public UpdateQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }
}
