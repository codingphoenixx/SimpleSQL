package dev.coph.simplesql.object;

import dev.coph.simplelogger.Logger;
import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.CharacterSet;
import dev.coph.simplesql.database.attributes.CreateMethode;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.providers.DatabaseCreateQueryProvider;
import dev.coph.simplesql.query.providers.DatabaseDropQueryProvider;
import dev.coph.simplesql.query.providers.SelectQueryProvider;
import dev.coph.simplesql.query.providers.TableAlterAddAttributeQueryProvider;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.HashSet;

/**
 * Represents a database instance with associated metadata and operations.
 * The Database class facilitates management of a specific database instance,
 * including creation, table operations, and storage of associated metadata.
 *
 * Instances of this class use the provided DatabaseAdapter to define
 * connectivity properties and support for various database drivers.
 */
@Getter
@Accessors(fluent = true, chain = true)
public class Database {

    /**
     * The DatabaseAdapter used for managing database connectivity and driver configurations.
     * It determines the appropriate driver type (e.g., MySQL, MariaDB, SQLite) and provides
     * support for establishing connections through the HikariCP connection pool.
     * This field is immutable and initialized during the creation of the Database instance.
     */
    private final DatabaseAdapter databaseAdapter;

    /**
     * Represents the name of the database.
     * Used to uniquely identify the database instance within the system.
     * Defaults to "main" for databases using `SQLITE` driver types.
     * This variable is immutable after assignment.
     */
    private final String name;

    /**
     * Constructs a new Database instance.
     *
     * @param databaseAdapter The DatabaseAdapter associated with the database.
     *                        Determines the driver type and provides connectivity support.
     * @param name            The name of the database. Defaults to "main" for SQLITE driver types.
     */
    public Database(DatabaseAdapter databaseAdapter, String name) {
        this.databaseAdapter = databaseAdapter;
        this.name = (databaseAdapter.driverType() == DatabaseAdapter.DriverType.SQLITE ? "main" : name);
        create(true, null).complete();
        loadTables().queue();
    }

    /**
     * Constructs a new Database instance with the specified parameters.
     *
     * @param databaseAdapter The DatabaseAdapter associated with the database.
     *                        Determines the driver type and provides connectivity support.
     * @param name            The name of the database. Defaults to "main" for SQLITE driver types.
     * @param characterSet    The character set to be applied to the database.
     */
    public Database(DatabaseAdapter databaseAdapter, String name, CharacterSet characterSet) {
        this.databaseAdapter = databaseAdapter;
        this.name = (databaseAdapter.driverType() == DatabaseAdapter.DriverType.SQLITE ? "main" : name);
        create(true, characterSet).complete();
        loadTables().queue();
    }


    private HashSet<String> tables = new HashSet<>();

    public QueueObject<Boolean> create(boolean ifNotExists, CharacterSet characterSet) {
        return new QueueObject<>(() -> {
            DatabaseCreateQueryProvider database = new DatabaseCreateQueryProvider().database(name).createMethode(ifNotExists ? CreateMethode.IF_NOT_EXISTS : CreateMethode.DEFAULT);
            if (characterSet != null)
                database.characterSet(characterSet);
            return new Query(databaseAdapter).executeQuery(database).succeeded();
        });
    }

    public QueueObject<Boolean> drop() {
        return new QueueObject<>(() -> {
            var databaseDropQuery = new DatabaseDropQueryProvider().database(name);
            return new Query(databaseAdapter).executeQuery(databaseDropQuery).succeeded();
        });
    }

    public QueueObject<HashSet<String>> loadTables() {
        return new QueueObject<>(() -> {
            tables.clear();
            try {
                SelectQueryProvider queryProvider = new SelectQueryProvider() {
                    @Override
                    public String generateSQLString(Query query) {
                        if (query.databaseAdapter() != null && query.databaseAdapter().driverType() == DatabaseAdapter.DriverType.SQLITE) {
                            return "SELECT tbl_name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%';";
                        }
                        return "SHOW TABLES FROM " + name + ";";
                    }
                };
                queryProvider.actionAfterQuery(resultSet -> {
                    if (resultSet == null) {
                        Logger.getInstance().error("Failed to load tables from database.");
                        return;
                    }
                    while (resultSet.next()) {
                        tables.add(resultSet.getString(1));
                    }
                });
                new Query(databaseAdapter).executeQuery(queryProvider);
            } catch (Exception e) {
                e.printStackTrace();
                return tables;
            }
            return tables;
        });
    }


    public Table getTable(String name) {
        return new Table(this, name);
    }

}
