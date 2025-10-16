package dev.coph.simplesql;

import dev.coph.simplelogger.Logger;
import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.utils.test.Tester;

import java.io.File;
import java.io.IOException;


public class Main {

    public static void main(String[] args) {
        Logger.instance().logLevel(Logger.LogLevel.DEBUG);
        System.out.println("--------------------------------------- MARIADB ------------------------------------------");
        startMariaDB();
        System.out.println("--------------------------------------- SQLITE ------------------------------------------");
        startSQLite();
        System.out.println("--------------------------------------- FINISHED ------------------------------------------");
    }


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


    private static void runQuery(DatabaseAdapter databaseAdapter) {
        Tester tester = new Tester(databaseAdapter);
        tester.runTests();
    }


}


//TODO:
//      - TableAlterQueryProviders

//TODO:  DELETE Where in
//TODO:  FOREIGN KEY
//TODO:  SQL-Operators: WHERE, GROUP BY, HAVING, OFFSET, UNION/UNION ALL, LIKE, BETWEEN, IN
//TODO:  User administration: CREATE USER, GRANT, REVOKE
//TODO:  CREATE TRIGGER

//TODO: Remove overloaded constructors in column and change to builder

