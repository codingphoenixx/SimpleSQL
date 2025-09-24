package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.CharacterSet;
import dev.coph.simplesql.database.attributes.CreateMethode;
import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

/**
 * The {@code DatabaseCreateQueryProvider} class provides functionality to construct
 * SQL CREATE DATABASE queries. It implements the {@link QueryProvider} interface,
 * enabling the generation of SQL strings based on specific parameters such as
 * database name, creation mode, and character set.
 * <p>
 * This class supports customization of the following attributes:
 * - Database name: The name of the database to be created.
 * - CreateMethode: Specifies whether the query should include a condition to
 * only create the database if it does not already exist.
 * - CharacterSet: Defines the character set for the database.
 * <p>
 * The generated SQL query is tailored to the underlying database adapter, except
 * for certain limitations (e.g., SQLite does not support specifying databases).
 * <p>
 * Note:<br>
 * - If the database name is null, an exception will be thrown.<br>
 * - For unsupported drivers or scenarios, an exception might be logged, and the
 * resulting SQL string generation may return null.
 */
public class DatabaseCreateQueryProvider implements QueryProvider {
    /**
     * The {@code database} variable represents the name of the database to be created.
     * It is a mandatory field and should be set to a valid, non-null value.
     * This value is used in generating the SQL CREATE DATABASE query.
     */
    private String database;
    /**
     * The {@code createMethode} variable specifies the method to be used when generating
     * the SQL CREATE DATABASE query. It determines whether the query should include
     * conditional logic for creating the database.
     * <p>
     * Possible values:
     * - {@link CreateMethode#DEFAULT}: A standard create operation without specific conditions.
     * - {@link CreateMethode#IF_NOT_EXISTS}: Creates the database only if it does not already exist.
     * <p>
     * This variable influences the generated SQL by including or omitting the conditional
     * "IF NOT EXISTS" clause based on the selected {@link CreateMethode}.
     */
    private CreateMethode createMethode = CreateMethode.DEFAULT;
    /**
     * Defines the character set to be used for the database creation query.
     * This variable represents the encoding standard for storing text in the database.
     * By default, it is set to UTF8MB4, which supports a wide range of characters, including emojis.
     */
    private CharacterSet characterSet = CharacterSet.UTF8MB4;
    private RunnableAction<Boolean> actionAfterQuery;

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> driverType != DriverType.SQLITE;
    }

    @Override
    public String generateSQLString(Query query) {

        Check.ifNull(database, "database name");
        return "CREATE DATABASE " + (createMethode == CreateMethode.IF_NOT_EXISTS ? "IF NOT EXITS " : "") + database + (characterSet != CharacterSet.UTF8MB4 ? " CHARACTER SET " + characterSet.name() : "");
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }


    public DatabaseCreateQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    /**
     * Retrieves the name of the database associated with this query provider.
     *
     * @return the name of the database
     */
    public String database() {
        return this.database;
    }

    /**
     * Retrieves the {@code CreateMethode} configuration associated with this query provider.
     *
     * @return the {@code CreateMethode} representing the strategy used during the creation
     * of database structures.
     */
    public CreateMethode createMethode() {
        return this.createMethode;
    }

    /**
     * Retrieves the character set configuration associated with this query provider.
     *
     * @return the character set configuration
     */
    public CharacterSet characterSet() {
        return this.characterSet;
    }

    /**
     * Sets the name of the database for the creation query.
     *
     * @param database The name of the database to be created.
     * @return {@link DatabaseCreateQueryProvider} for chaining, allowing further configuration of the query.
     */
    public DatabaseCreateQueryProvider database(String database) {
        this.database = database;
        return this;
    }

    /**
     * Sets the {@code CreateMethode} configuration for this query provider.
     * This determines the strategy used during the creation of database structures.
     *
     * @param createMethode the {@code CreateMethode} to be applied. It specifies the creation strategy, such as
     *                      {@code DEFAULT} or {@code IF_NOT_EXISTS}.
     * @return the {@code DatabaseCreateQueryProvider} instance for method chaining, allowing further configuration.
     */
    public DatabaseCreateQueryProvider createMethode(CreateMethode createMethode) {
        this.createMethode = createMethode;
        return this;
    }

    /**
     * Sets the character set configuration for the database creation query.
     *
     * @param characterSet the character set to be used for the database
     * @return the {@code DatabaseCreateQueryProvider} instance for method chaining
     */
    public DatabaseCreateQueryProvider characterSet(CharacterSet characterSet) {
        this.characterSet = characterSet;
        return this;
    }
}
