package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.DeleteMethode;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.check.Check;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A provider for generating SQL queries to drop database tables.
 * This class constructs SQL "DROP TABLE" statements with configurable options
 * such as the set of targeted tables and the deletion method.
 */
public class TableDropQueryProvider implements QueryProvider {

    /**
     * A list of tables that will be targeted by the query operation.
     * <p>
     * This variable holds the relevant tables to be included in the SQL query execution
     * and is essential for specifying the scope of the operation. The configuration of
     * this field determines the tables that the query will interact with.
     */
    private ArrayList<String> tables;

    /**
     * Specifies the method or strategy that should be used to perform deletion
     * operations in database queries. The {@code deleteMethode} variable leverages
     * the {@link DeleteMethode} enum to define available deletion behaviors, such as
     * conditional deletion or default deletion execution.
     * <p>
     * This field allows customization of how deletions are carried out when constructing
     * SQL statements or executing database operations. The configuration of this field
     * directly impacts the generated SQL query and ensures adherence to specific deletion
     * requirements.
     */
    private DeleteMethode deleteMethode;


    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(tables, "tables");

        StringBuilder sql = new StringBuilder("DROP TABLE");

        if (deleteMethode.equals(DeleteMethode.IF_EXISTS))
            sql.append(" IF EXISTS");

        for (String table : tables) {
            sql.append(" ").append(table).append(",");
        }

        sql.deleteCharAt(sql.length() - 1);
        sql.append(";");
        return sql.toString();
    }

    /**
     * Adds one or more table names to the list of tables that will be targeted
     * for a "DROP TABLE" SQL operation.
     *
     * @param tables One or more table names to be included in the drop table operation.
     * @return The current instance of {@link TableDropQueryProvider}, allowing for method chaining.
     */
    public TableDropQueryProvider table(String... tables) {
        if (this.tables == null) {
            this.tables = new ArrayList<>();
        }


        this.tables.addAll(Arrays.asList(tables));
        return this;
    }

    /**
     * Retrieves the list of tables that are targeted for inclusion in the SQL query.
     *
     * @return An {@link ArrayList} containing the names of the tables affected by the query.
     */
    public ArrayList<String> tables() {
        return this.tables;
    }

    /**
     * Retrieves the current deletion strategy used by the TableDropQueryProvider.
     *
     * @return The current {@link DeleteMethode} that specifies the deletion strategy.
     */
    public DeleteMethode deleteMethode() {
        return this.deleteMethode;
    }

    /**
     * Sets the list of table names that are targeted for a "DROP TABLE" SQL operation.
     *
     * @param tables An ArrayList containing the names of the tables to be included in the drop operation.
     * @return The current instance of TableDropQueryProvider, allowing for method chaining.
     */
    public TableDropQueryProvider tables(ArrayList<String> tables) {
        this.tables = tables;
        return this;
    }

    /**
     * Sets the deletion method for the TableDropQueryProvider.
     * This method allows specifying a strategy for determining how
     * delete operations should be handled.
     *
     * @param deleteMethode The deletion strategy to be applied, as an instance of {@link DeleteMethode}.
     *                      Possible values include {@link DeleteMethode#DEFAULT} and {@link DeleteMethode#IF_EXISTS}.
     * @return The current instance of {@link TableDropQueryProvider}, allowing for method chaining.
     */
    public TableDropQueryProvider deleteMethode(DeleteMethode deleteMethode) {
        this.deleteMethode = deleteMethode;
        return this;
    }
}
