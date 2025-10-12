package dev.coph.simplesql;

import dev.coph.simplelogger.Logger;
import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.*;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.providers.*;
import dev.coph.simplesql.utils.test.Tester;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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

    /**
     * Populates a default set of entries into the database.
     * This method creates 1000 random entries, each containing a UUID
     * and a Base64-encoded comment string. The entries are then inserted
     * into the "test6" table using the provided database adapter.
     *
     * @param databaseAdapter the database adapter used to execute the insert queries
     */
    private static void createDefaultEntries(DatabaseAdapter databaseAdapter) {
        Query insertQuery = new Query(databaseAdapter);
        for (int i = 0; i < 10000; i++) {
            UUID uuid = UUID.randomUUID();
            String comment = Base64.getEncoder().encodeToString(uuid.toString().getBytes());
            int number = i + new Random().nextInt();
            InsertQueryProvider insertQueryProvider = new InsertQueryProvider()
                    .table("test6")
                    .insertMethode(InsertMethode.INSERT_OR_UPDATE)
                    .conflictColumns(List.of("uuid"))
                    .entry("uuid", uuid.toString())
                    .entry("number", number)
                    .entry("comment", comment);

            InsertQueryProvider insertQueryProvider2 = new InsertQueryProvider()
                    .table("test7")
                    .insertMethode(InsertMethode.INSERT_OR_UPDATE)
                    .conflictColumns(List.of("uuid"))
                    .entry("uuid", uuid.toString())
                    .entry("number", number)
                    .entry("comment", comment);
            insertQuery.queries(insertQueryProvider, insertQueryProvider2);
        }
        System.out.println("Inserting 1000 entries into test6 and test7...");
        insertQuery.execute();
    }

    /**
     * Parses and prints the contents of a {@code ResultSet}.
     * This method extracts metadata and row data from the given {@code ResultSet},
     * formats it into a tabular representation, and prints it to the console.
     * Each column name and its corresponding row values are displayed in a structured format.
     * If a {@code SQLException} occurs during processing, the exception stack trace is printed.
     *
     * @param resultSet the {@code ResultSet} to be parsed and printed
     */
    private static void parseResultSet(ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            StringBuilder header = new StringBuilder("|");
            for (int i = 1; i <= columnCount; i++) {
                header.append(String.format(" %-20s |", metaData.getColumnName(i)));
            }
            System.out.println(header);
            while (resultSet.next()) {
                StringBuilder row = new StringBuilder("|");
                for (int i = 1; i <= columnCount; i++) {
                    row.append(String.format(" %-20s |", resultSet.getString(i)));
                }
                System.out.println(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
//TODO: FIX THAT F***ING MESS HERE (escaping, etc). CHANGE TO PREPARED STATEMENTS

//TODO:  Add indexes
//TODO:  Add db-engine
//TODO:  DELETE Where in
//TODO:  JOIN
//TODO:  Transaktionsmanagement (BEGIN TRANSACTION / START TRANSACTION | COMMIT | ROLLBACK)
//TODO:  Metadata -> Table/Column Name/Amount/DATATYPE
//TODO:  FOREIGN KEY
//TODO:  SQL-Operators: WHERE, GROUP BY, HAVING, OFFSET, UNION/UNION ALL, LIKE, BETWEEN, IN
//TODO:  User administration: CREATE USER, GRANT, REVOKE
//TODO:  CREATE TRIGGER
//TODO:  CREATE TEMPORARY TABLE
