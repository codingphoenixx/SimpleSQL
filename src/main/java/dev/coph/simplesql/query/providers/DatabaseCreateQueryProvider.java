package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.CharacterSet;
import dev.coph.simplesql.database.attributes.CreateMethode;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
