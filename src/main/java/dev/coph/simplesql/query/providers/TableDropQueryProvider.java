package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.DeleteMethode;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.check.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A provider for generating SQL queries to drop database tables.
 * This class constructs SQL "DROP TABLE" statements with configurable options
 * such as the set of targeted tables and the deletion method.
 */
@Getter
@Accessors(fluent = true, chain = true)
public class TableDropQueryProvider implements QueryProvider {

    /**
     * A list of tables that will be targeted by the query operation.
     * <p>
     * This variable holds the relevant tables to be included in the SQL query execution
     * and is essential for specifying the scope of the operation. The configuration of
     * this field determines the tables that the query will interact with.
     */
    @Setter
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
    @Setter
    private DeleteMethode deleteMethode;


    @Override
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(tables, "tables");

        StringBuilder sql = new StringBuilder("DROP TABLE");

        if(deleteMethode.equals(DeleteMethode.IF_EXISTS))
            sql.append(" IF EXISTS");

        for (String table : tables) {
            sql.append(" ").append(table).append(",");
        }

        sql.deleteCharAt(sql.length() - 1);
        sql.append(";");
        return sql.toString();
    }


    public TableDropQueryProvider table(String... tables) {
        if (this.tables == null) {
            this.tables = new ArrayList<>();
        }


        this.tables.addAll(Arrays.asList(tables));
        return this;
    }
}
