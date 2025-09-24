package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.check.Check;

/**
 * Abstract class representing a provider for generating SQL "ALTER TABLE" queries.
 * Subclasses are expected to implement the logic for constructing specific "ALTER TABLE"
 * operations such as adding a column, renaming a table, or modifying attributes.
 * This class provides a base structure with shared functionality for table alterations.
 */
public abstract class TableAlterQueryProvider implements QueryProvider {
    /**
     * Represents the name of the database table to be used in an "ALTER TABLE" query.
     * This variable is shared across the class and its subclasses, providing the base
     * target for SQL table alteration operations, such as adding columns, renaming tables,
     * or modifying attributes.
     * <p>
     * Subclasses must set this variable before generating the SQL string to ensure that
     * the correct table is targeted during the execution of the query.
     */
    protected String table;

    /**
     * Generates the SQL string for performing an "ALTER TABLE" operation based on the
     * given query. The method is abstract and must be implemented by subclasses to
     * provide specific alteration logic, such as adding columns, renaming tables,
     * or modifying table attributes.
     *
     * @param query the query object containing the necessary information to construct
     *              the "ALTER TABLE" operation, such as table details and alteration specifics
     * @return a string representing the SQL "ALTER TABLE" statement for the specified query
     */
    public abstract String getAlterTableString(Query query);

    @Override
    public String generateSQLString(Query query) {
        Check.ifNull(table, "table name");
        return "ALTER TABLE " + table + " " + getAlterTableString(query) + ";";
    }

    /**
     * Returns the name of the database table targeted for SQL "ALTER TABLE" operations.
     *
     * @return the name of the table as a string, which is the target for the "ALTER TABLE" query.
     */
    public String table() {
        return this.table;
    }

    /**
     * Sets the name of the database table to be used in an "ALTER TABLE" query.
     *
     * @param table the name of the table to be targeted for alteration
     * @return the {@link TableAlterQueryProvider} instance for method chaining
     */
    public TableAlterQueryProvider table(String table) {
        this.table = table;
        return this;
    }
}
