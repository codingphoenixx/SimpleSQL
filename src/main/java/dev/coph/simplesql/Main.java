package dev.coph.simplesql;

import com.mysql.cj.log.Log;
import dev.coph.simplelogger.Logger;
import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.*;
import dev.coph.simplesql.object.Database;
import dev.coph.simplesql.query.Query;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Logger.getInstance();
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
                .driverType(DatabaseAdapter.DriverType.MARIADB)
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
                .driverType(DatabaseAdapter.DriverType.SQLITE)
                .sqliteFile(sqliteFile)
                .build();
        databaseAdapter.connect();

        runQuery(databaseAdapter);
    }

    private static void runQuery(DatabaseAdapter databaseAdapter) {
        Database database = new Database(databaseAdapter, "testing");
        database.getTable("testing");
    }
}
