package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.CharacterSet;
import dev.coph.simplesql.database.attributes.CreateMethode;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simplesql.utils.QueryResult;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

/**
 * The {@code DatabaseCreateQueryProvider} class is responsible for generating SQL strings
 * specific to creating databases. It implements the {@link QueryProvider} interface and
 * provides compatibility checks and SQL query execution logic tailored for multiple database
 * systems such as MySQL, MariaDB, and PostgreSQL.
 * <p>
 * This class encapsulates details like the database name, character set, collation, and
 * additional database-specific options such as LC_COLLATE and LC_CTYPE for PostgreSQL.
 * It also supports defining actions to be executed after the query execution.
 */
public class DatabaseCreateQueryProvider implements QueryProvider {

    private String database;

    private CreateMethode createMethode = CreateMethode.DEFAULT;

    private CharacterSet characterSet = CharacterSet.UTF8MB4;

    private String collate;
    private String lcCollate;
    private String lcCtype;


    private RunnableAction<QueryResult<DatabaseCreateQueryProvider>> actionAfterQuery;

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
                    sql.append(" WITH ");
                    hasWith = true;
                    sql.append("ENCODING '").append(enc).append("'");
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

    /**
     * Sets the database name for the query.
     *
     * @param database the name of the database to be set
     * @return the current instance of {@code DatabaseCreateQueryProvider} for method chaining
     */
    public DatabaseCreateQueryProvider database(String database) {
        this.database = database;
        return this;
    }

    /**
     * Sets the create method for the query provider and returns the current instance for method chaining.
     * If the provided create method is {@code null}, the default create method will be used.
     *
     * @param createMethode the {@code CreateMethode} to define the creation strategy for the database structure.
     *                      If {@code null}, the default create method will be applied.
     * @return the current instance of {@code DatabaseCreateQueryProvider} for method chaining.
     */
    public DatabaseCreateQueryProvider createMethode(CreateMethode createMethode) {
        this.createMethode = createMethode != null ? createMethode : CreateMethode.DEFAULT;
        return this;
    }

    /**
     * Sets the character set for the database creation query and returns the current
     * instance for method chaining.
     *
     * @param characterSet the {@code CharacterSet} to specify the character encoding
     *                     for the database.
     * @return the current instance of {@code DatabaseCreateQueryProvider} to allow
     * for method chaining.
     */
    public DatabaseCreateQueryProvider characterSet(CharacterSet characterSet) {
        this.characterSet = characterSet;
        return this;
    }

    /**
     * Sets the collation for the database creation query and returns the current
     * instance of {@code DatabaseCreateQueryProvider} to allow for method chaining.
     *
     * @param collate the collation to specify how the database should sort text values.
     * @return the current instance of {@code DatabaseCreateQueryProvider} for method chaining.
     */
    public DatabaseCreateQueryProvider collate(String collate) {
        this.collate = collate;
        return this;
    }

    /**
     * Sets the LC_COLLATE attribute, specifying the collation rules for the database.
     * This determines linguistic ordering and sorting for the database during creation.
     * Returns the current instance of {@code DatabaseCreateQueryProvider} to allow for method chaining.
     *
     * @param lcCollate the LC_COLLATE value to define collation rules for the database
     * @return the current instance of {@code DatabaseCreateQueryProvider} for method chaining
     */
    public DatabaseCreateQueryProvider lcCollate(String lcCollate) {
        this.lcCollate = lcCollate;
        return this;
    }

    /**
     * Sets the LC_CTYPE attribute, specifying the character classification rules
     * for the database during creation. This determines locale-specific character
     * classification behavior such as upper/lower case distinctions.
     *
     * @param lcCtype the LC_CTYPE value to define character classification rules for the database
     * @return the current instance of {@code DatabaseCreateQueryProvider} for method chaining
     */
    public DatabaseCreateQueryProvider lcCtype(String lcCtype) {
        this.lcCtype = lcCtype;
        return this;
    }

    /**
     * Sets the action to be executed after a query is processed and allows
     * method chaining for configuring the database creation query.
     *
     * @param actionAfterQuery the {@code RunnableAction<Boolean>} to be executed after
     *                         running the query. The {@code Boolean} parameter indicates
     *                         the success or failure of the query execution.
     * @return the current instance of {@code DatabaseCreateQueryProvider} to allow
     * for method chaining.
     */
    public DatabaseCreateQueryProvider actionAfterQuery(RunnableAction<QueryResult<DatabaseCreateQueryProvider>> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<QueryResult<DatabaseCreateQueryProvider>> actionAfterQuery() {
        return actionAfterQuery;
    }

    /**
     * Retrieves the name of the database associated with this query provider.
     *
     * @return the database name as a {@code String}
     */
    public String database() {
        return this.database;
    }

    /**
     * Retrieves the current {@code CreateMethode} indicating the strategy for creating
     * database structures, such as tables. If the value is not explicitly set, it may
     * return a default strategy.
     *
     * @return the {@code CreateMethode} representing the database creation strategy
     */
    public CreateMethode createMethode() {
        return this.createMethode;
    }

    /**
     * Retrieves the character set configured for the database creation query.
     *
     * @return the {@code CharacterSet} representing the character encoding specified
     * for the database.
     */
    public CharacterSet characterSet() {
        return this.characterSet;
    }

    /**
     * Retrieves the collation setting for the database creation query.
     *
     * @return the collation as a {@code String}, representing how the database should sort text values.
     */
    public String collate() {
        return this.collate;
    }

    /**
     * Retrieves the LC_COLLATE setting for the database creation query.
     * This value defines the collation rules for the database,
     * which determine the linguistic ordering and sorting behavior.
     *
     * @return the LC_COLLATE setting as a {@code String}
     */
    public String lcCollate() {
        return this.lcCollate;
    }

    /**
     * Retrieves the LC_CTYPE setting for the database creation query.
     * The LC_CTYPE value defines the locale-specific character classification rules,
     * which determine behavior such as upper/lower case distinctions.
     *
     * @return the LC_CTYPE setting as a {@code String}, representing character classification rules for the database
     */
    public String lcCtype() {
        return this.lcCtype;
    }
}
