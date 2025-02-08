package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.DeleteMethode;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.check.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The DatabaseDropQueryProvider class is responsible for generating SQL strings for
 * dropping a database. It implements the QueryProvider interface and provides
 * a mechanism to construct SQL DROP DATABASE queries.
 *
 * This class allows customization of the drop operation using the `DeleteMethode`
 * enum, which determines whether or not to include the "IF EXISTS" condition.
 * It also ensures that a database name is provided before generating the SQL string.
 * Note that SQLite does not support dropping databases; attempting to do so
 * will throw an UnsupportedOperationException.
 */
@Getter
@Accessors(fluent = true, chain = true)
public class DatabaseDropQueryProvider implements QueryProvider {

    /**
     * Represents the name of the database to be dropped.
     * This field is required when constructing SQL `DROP DATABASE` queries.
     * It must be set to a non-null, non-empty value before generating the SQL query.
     */
    @Setter
    private String database;

    /**
     * Specifies the deletion method to be used when performing database operations
     * that involve removing entities or schemas.
     *
     * This field utilizes the {@link DeleteMethode} enum, which provides strategies
     * such as {@code DEFAULT} for unguarded deletions or {@code IF_EXISTS} to ensure
     * the entity exists before deletion. The choice of deletion method can influence
     * the generated SQL query behavior and provides flexibility in handling database
     * deletions.
     *
     * The default value is {@code DeleteMethode.DEFAULT}, ensuring straightforward
     * deletion without additional existence checks.
     */
    @Setter
    private DeleteMethode deleteMethode = DeleteMethode.DEFAULT;


    @Override
    public String generateSQLString(Query query) {
        if (query.databaseAdapter() != null && query.databaseAdapter().driverType() == DatabaseAdapter.DriverType.SQLITE) {
            try {
                throw new UnsupportedOperationException("SQLite does not support different Databases. Ignoring attribute...");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        Check.ifNull(database, "database name");
        return "DROP DATABASE %s%s".formatted((deleteMethode == DeleteMethode.IF_EXISTS ? "IF EXISTS " : ""), database);
    }
}
