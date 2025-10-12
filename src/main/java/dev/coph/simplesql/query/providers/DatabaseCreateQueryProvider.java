package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.CharacterSet;
import dev.coph.simplesql.database.attributes.CreateMethode;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

public class DatabaseCreateQueryProvider implements QueryProvider {

    private String database;

    private CreateMethode createMethode = CreateMethode.DEFAULT;

    private CharacterSet characterSet = CharacterSet.UTF8MB4;

    private String collate;
    private String lcCollate;
    private String lcCtype;

    private boolean quoteIdentifiers;

    private RunnableAction<Boolean> actionAfterQuery;

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> driverType != DriverType.SQLITE;
    }

    @Override
    public String generateSQLString(Query query) {
        Check.ifNull(database, "database name");

        DriverType driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        DatabaseCheck.missingDriver(driver);

        StringBuilder sql = new StringBuilder("CREATE DATABASE ");

        boolean ifNotExists = (createMethode == CreateMethode.IF_NOT_EXISTS);

        switch (driver) {
            case MYSQL, MARIADB -> {
                if (ifNotExists) sql.append("IF NOT EXISTS ");
                sql.append(database);

                if (characterSet != null) {
                    sql.append(" DEFAULT CHARACTER SET ")
                            .append(characterSet.toMySqlCharset());
                }

                if (collate != null && !collate.isBlank()) {
                    sql.append(" DEFAULT COLLATE ").append(collate);
                }

                sql.append(";");
            }
            case POSTGRESQL -> {
                if (ifNotExists) sql.append("IF NOT EXISTS ");
                sql.append(database);

                boolean hasWith = false;

                if (characterSet != null) {

                    String enc = characterSet.toPostgresEncodingOrThrow();
                    if (enc != null) {
                        sql.append(hasWith ? " " : " WITH ");
                        hasWith = true;
                        sql.append("ENCODING '").append(enc).append("'");
                    }
                }

                if (lcCollate != null && !lcCollate.isBlank()) {
                    sql.append(hasWith ? " " : " WITH ");
                    hasWith = true;
                    sql.append("LC_COLLATE '").append(lcCollate).append("'");
                }

                if (lcCtype != null && !lcCtype.isBlank()) {
                    sql.append(hasWith ? " " : " WITH ");
                    hasWith = true;
                    sql.append("LC_CTYPE '").append(lcCtype).append("'");
                }

                sql.append(";");
            }
            case SQLITE -> {
                throw new FeatureNotSupportedException(driver);
            }
            default -> throw new FeatureNotSupportedException(driver);
        }

        return sql.toString();
    }


    public DatabaseCreateQueryProvider database(String database) {
        this.database = database;
        return this;
    }

    public DatabaseCreateQueryProvider createMethode(CreateMethode createMethode) {
        this.createMethode = createMethode != null ? createMethode : CreateMethode.DEFAULT;
        return this;
    }

    public DatabaseCreateQueryProvider characterSet(CharacterSet characterSet) {
        this.characterSet = characterSet;
        return this;
    }

    public DatabaseCreateQueryProvider collate(String collate) {
        this.collate = collate;
        return this;
    }

    public DatabaseCreateQueryProvider lcCollate(String lcCollate) {
        this.lcCollate = lcCollate;
        return this;
    }

    public DatabaseCreateQueryProvider lcCtype(String lcCtype) {
        this.lcCtype = lcCtype;
        return this;
    }

    public DatabaseCreateQueryProvider quoteIdentifiers(boolean enable) {
        this.quoteIdentifiers = enable;
        return this;
    }

    public DatabaseCreateQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }


    public String database() {
        return this.database;
    }

    public CreateMethode createMethode() {
        return this.createMethode;
    }

    public CharacterSet characterSet() {
        return this.characterSet;
    }

    public String collate() {
        return this.collate;
    }

    public String lcCollate() {
        return this.lcCollate;
    }

    public String lcCtype() {
        return this.lcCtype;
    }

    public boolean quoteIdentifiers() {
        return this.quoteIdentifiers;
    }
}
