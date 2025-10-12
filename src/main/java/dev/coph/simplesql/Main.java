package dev.coph.simplesql;

import dev.coph.simplelogger.Logger;
import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.utils.test.Tester;

import java.io.File;
import java.io.IOException;

/**
 * The Main class serves as the entry point for the application. It initializes and demonstrates
 * database connections using MariaDB or SQLite and executes predefined queries on those databases.
 * This class contains static helper methods for various operations such as starting services,
 * executing queries, and parsing results.
 * Only for testing.
 */
public class Main {
    /**
     * The main method serves as the entry point for the application. It initializes the logger,
     * starts the MariaDB database process, and prints out logging information for different stages
     * of execution. Currently, the SQLite database process is commented out.
     *
     * @param args command-line arguments passed during application execution
     */
    public static void main(String[] args) {
        Logger.getInstance().logLevel(Logger.LogLevel.DEBUG);
        System.out.println("--------------------------------------- MARIADB ------------------------------------------");
        startMariaDB();
        System.out.println("--------------------------------------- SQLITE ------------------------------------------");
        startSQLite();
        System.out.println("--------------------------------------- FINISHED ------------------------------------------");
    }

    /**
     * Initializes and starts a MariaDB database connection.
     * This method creates a {@code DatabaseAdapter} instance configured for MariaDB,
     * connects to the specified database, and executes a predefined query.
     * <p>
     * The database connection is configured with the following parameters:
     * - Host: localhost
     * - Port: 3306
     * - Database Name: testing
     * - User: root
     * - Password: (empty)
     * - Driver Type: MariaDB
     * <p>
     * After establishing the connection, the method delegates query execution
     * to the {@code runQuery} method using the created {@code DatabaseAdapter} instance.
     */
    private static void startMariaDB() {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter.Builder()
                .host("localhost")
                .port(3306)
                .database("testing")
                .user("root")
                .password("")
                .driverType(DriverType.MARIADB)
                .build();
        databaseAdapter.connect();
        runQuery(databaseAdapter);
    }

    /**
     * Initializes and starts an SQLite database connection.
     * <p>
     * This method checks for the existence of the SQLite database file (`test.db`).
     * If the file does not exist, it creates a new file. A {@code DatabaseAdapter} instance is then
     * configured to connect to this SQLite database, and the connection is established.
     * <p>
     * Once the connection is successfully established, the method delegates query execution
     * to the {@code runQuery} method using the created {@code DatabaseAdapter} instance.
     * <p>
     * Exceptions during file creation are caught and logged using {@code e.printStackTrace()}.
     * <p>
     * Dependencies:
     * - {@code java.io.File}
     * - {@code java.io.IOException}
     * - {@code DatabaseAdapter}
     */
    private static void startSQLite() {
        File sqliteFile = new File("test.db");
        if (!sqliteFile.exists()) {
            try {
                sqliteFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        DatabaseAdapter databaseAdapter = new DatabaseAdapter.Builder()
                .driverType(DriverType.SQLITE)
                .sqliteFile(sqliteFile)
                .build();
        databaseAdapter.connect();

        runQuery(databaseAdapter);
    }

    /**
     * Executes a query using the provided database adapter.
     * This method sets up a query provider for a specified table,
     * configures the query to use SQL functions, and specifies an action
     * to execute on the result set after the query is completed.
     * The query is then executed using the provided database adapter.
     *
     * @param databaseAdapter the adapter used to connect and execute queries against the database
     */
    private static void runQuery(DatabaseAdapter databaseAdapter) {
        Tester tester = new Tester(databaseAdapter);
        tester.runTests();
    }


}


//TODO:
//      REWORK-CLASSES:
//      - DatabaseCreateQueryProvider
//      - DatabaseDropQueryProvider
//      - TableDropQueryProvider
//      - TruncateQueryProvider
//      - TableAlterQueryProviders

//TODO:  Add indexes
//TODO:  Add db-engine
//TODO:  DELETE Where in
//TODO:  Metadata -> Table/Column Name/Amount/DATATYPE
//TODO:  FOREIGN KEY
//TODO:  SQL-Operators: WHERE, GROUP BY, HAVING, OFFSET, UNION/UNION ALL, LIKE, BETWEEN, IN
//TODO:  User administration: CREATE USER, GRANT, REVOKE
//TODO:  CREATE TRIGGER

