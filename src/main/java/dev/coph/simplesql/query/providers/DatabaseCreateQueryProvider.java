package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.CharacterSet;
import dev.coph.simplesql.database.attributes.CreateMethode;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.check.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The {@code DatabaseCreateQueryProvider} class provides functionality to construct
 * SQL CREATE DATABASE queries. It implements the {@link QueryProvider} interface,
 * enabling the generation of SQL strings based on specific parameters such as
 * database name, creation mode, and character set.
 *
 * This class supports customization of the following attributes:
 * - Database name: The name of the database to be created.
 * - CreateMethode: Specifies whether the query should include a condition to
 *   only create the database if it does not already exist.
 * - CharacterSet: Defines the character set for the database.
 *
 * The generated SQL query is tailored to the underlying database adapter, except
 * for certain limitations (e.g., SQLite does not support specifying databases).
 *
 * Note:
 * - If the database name is null, an exception will be thrown.
 * - For unsupported drivers or scenarios, an exception might be logged, and the
 *   resulting SQL string generation may return null.
 */
@Getter
@Accessors(fluent = true, chain = true)
public class DatabaseCreateQueryProvider implements QueryProvider {

    @Setter
    private String database;

    @Setter
    private CreateMethode createMethode = CreateMethode.DEFAULT;

    @Setter
    private CharacterSet characterSet = CharacterSet.UTF8MB4;

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
        return "CREATE DATABASE " + (createMethode == CreateMethode.IF_NOT_EXISTS ? "IF NOT EXITS " : "") + database + (characterSet != CharacterSet.UTF8MB4 ? " CHARACTER SET " + characterSet.name() : "");
    }
}
