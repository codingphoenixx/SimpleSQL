package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.Condition;
import dev.coph.simplesql.database.attributes.Limit;
import dev.coph.simplesql.database.attributes.Operator;
import dev.coph.simplesql.database.attributes.UpdatePriority;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryEntry;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.check.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
 *
 * The generated SQL update query supports the following:
 * - Defining the target table for the update operation.
 * - Specifying priority levels for the execution of the query.
 * - Adding "IGNORE" directives to handle errors during update.
 * - Incorporating multiple conditions in the "WHERE" clause for targeted updates.
 * - Setting column-value pairs to modify specific records.
 * - Applying "LIMIT" clauses for constraining the number of affected rows.
 *
 * This implementation adheres to the QueryProvider interface, ensuring
 * compatibility with a broader query management system.
 */
@Getter
@Accessors(fluent = true)
public class UpdateQueryProvider implements QueryProvider {
    /**
     * Represents the name of the table to be updated in the SQL query.
     * This variable specifies the target table where the update operation
     * will be performed.
     */
    @Setter
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
    @Setter
    private UpdatePriority updatePriority = UpdatePriority.NORMAL;

    /**
     * Indicates whether to ignore errors while updating rows in the SQL query.
     * If set to true, the SQL update operation may skip specific rows or conditions when an error occurs.
     */
    @Setter
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


    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(table, "table name");

        StringBuilder sql = new StringBuilder("UPDATE");

        if (updatePriority == UpdatePriority.LOW) {
            if (query.databaseAdapter().driverType() == DatabaseAdapter.DriverType.SQLITE) {
                try {
                    throw new UnsupportedOperationException("SQLite does not support priorities when updating Databases. Ignoring attribute...");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
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
            parsedCondition.append(condition.type().equals(Condition.Type.AND) ? " AND " : " OR ").append(condition);
        }
        return parsedCondition.toString();
    }
}
