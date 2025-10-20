package dev.coph.simplesql.database.attributes;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a database join operation, including the type of join and the
 * conditions under which the join is executed.
 */
public class Join {
    private final JoinType type;
    private final String table;
    private final String alias;
    private final LinkedHashSet<Condition> onConditions = new LinkedHashSet<>();

    /**
     * Constructs a Join object, representing a database join operation
     * with the specified join type, target table, and optional alias.
     *
     * @param type  the type of join to be executed (e.g., INNER, LEFT, RIGHT, FULL)
     * @param table the name of the database table involved in the join
     * @param alias an optional alias for the table, or null if no alias is used
     */
    public Join(JoinType type, String table, String alias) {
        this.type = type;
        this.table = table;
        this.alias = alias;
    }

    /**
     * Adds a condition to the join operation, specifying the criteria under which
     * the join should occur.
     *
     * @param condition the condition to be added to the join operation; must specify
     *                  valid join criteria and will only be added if not null
     * @return the current Join instance, allowing for method chaining
     */
    public Join on(Condition condition) {
        if (condition != null) onConditions.add(condition);
        return this;
    }

    /**
     * Adds a condition to the join operation based on the specified column and value.
     * This method creates a condition internally using the given column and value,
     * then adds it to the join's condition set.
     *
     * @param column the name of the column to be used in the join condition
     * @param value  the value to be matched against the specified column
     * @return the current Join instance, allowing for method chaining
     */
    public Join on(String column, Object value) {
        return on(new Condition(column, value));
    }

    /**
     * Adds a condition to the join operation based on the specified column, operator, and value.
     * This method creates a condition internally using the given column, operator, and value,
     * then adds it to the join's condition set.
     *
     * @param column   the name of the column to be used in the join condition
     * @param operator the operator to be applied when evaluating the condition (e.g., EQUALS, LESS_THAN)
     * @param value    the value to be matched or compared against the specified column
     * @return the current Join instance, allowing for method chaining
     */
    public Join on(String column, Operator operator, Object value) {
        return on(new Condition(column, operator, value));
    }

    /**
     * Retrieves the join type associated with this Join instance.
     * The join type defines the specific nature of the database join
     * (e.g., INNER, LEFT, RIGHT, FULL).
     *
     * @return the type of join represented by this instance
     */
    public JoinType type() {
        return type;
    }

    /**
     * Retrieves the name of the table associated with this Join instance.
     *
     * @return the name of the table involved in the join
     */
    public String table() {
        return table;
    }

    /**
     * Retrieves the alias associated with this Join instance if one is defined.
     * The alias serves as an optional alternative name for the table in the join operation.
     *
     * @return the alias of the table, or null if no alias is set
     */
    public String alias() {
        return alias;
    }

    /**
     * Retrieves the set of conditions associated with this join operation.
     * The conditions define the criteria under which the join should occur.
     *
     * @return a set of {@code Condition} objects that represent the join conditions
     */
    public Set<Condition> onConditions() {
        return onConditions;
    }

    /**
     * Specifies the types of joins that can be performed in a database query.
     * <p>
     * The join type defines how rows from two tables are matched and included
     * in the result set of a query. The possible types of joins are:
     * <p>
     * - INNER: Includes only matching rows from both tables.
     * - LEFT: Includes all rows from the left table and matching rows from the right table,
     * with non-matching rows from the right table being filled with nulls.
     * - RIGHT: Includes all rows from the right table and matching rows from the left table,
     * with non-matching rows from the left table being filled with nulls.
     * - FULL: Includes all rows from both tables, with non-matching rows from either table
     * being filled with nulls.
     */
    public enum JoinType {
        /**
         * Represents the INNER join type in a database query.
         * <p>
         * An INNER join combines rows from two or more tables based on a related column
         * between them. It includes only the rows where there is a match in all the joined tables.
         */
        INNER,
        /**
         * Represents the LEFT join type in a database query.
         * <p>
         * A LEFT join retrieves all rows from the left table and the matching rows
         * from the right table. If there are no matching rows in the right table,
         * the result will include nulls for the columns from the right table.
         */
        LEFT,
        /**
         * Represents the RIGHT join type in a database query.
         * <p>
         * A RIGHT join retrieves all rows from the right table and the matching rows
         * from the left table. If there are no matching rows in the left table,
         * the result will include nulls for the columns from the left table.
         */
        RIGHT,
        /**
         * Represents the FULL join type in a database query.
         * <p>
         * A FULL join includes all rows from both tables. For rows that do not have a match
         * in the other table, the result will include nulls for the columns from the
         * table without a match.
         */
        FULL
    }
}
