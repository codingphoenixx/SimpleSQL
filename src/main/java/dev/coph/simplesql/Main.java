package dev.coph.simplesql;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.*;
import dev.coph.simplesql.query.Query;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        System.out.println("--------------------------- MARIADB ------------------------------");
        startMariaDB();
        System.out.println("--------------------------- SQLITE ------------------------------");
        startSQLite();
        System.out.println("--------------------------- FINISHED ------------------------------");
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
        var tableCreateQuery = Query.tableCreate()
                .table("test6")
                .column("uuid", DataType.VARCHAR, 64, ColumnType.UNIQUE)
                .column("comment", DataType.LONGTEXT)
                .column("number", DataType.INTEGER, ColumnType.PRIMARY_KEY_AUTOINCREMENT)
                .createMethode(CreateMethode.IF_NOT_EXISTS);

        var insertQuery = Query.insert()
                .table("test6")
                .insertMethode(InsertMethode.INSERT_IGNORE)
                .entry("uuid", "0123456789");

        var selectQuery = Query.select()
                .table("test6")
                .condition("uuid", "1234567890")
                .actionAfterQuery(resultSet -> {
                    boolean next = resultSet.next();
                    System.out.println("Has next: " + next);
                    if(next){
                        System.out.println("Comment: " + resultSet.getString("comment"));
                    }
                });

        var updateQuery = Query.update()
                .table("test6")
                .condition("uuid", "1234567890")
                .updateIgnore(false)
                .updatePriority(UpdatePriority.LOW)
                .entry("comment", new Random().nextInt())
                ;

        var selectQuery2 = Query.select()
                .table("test6")
                .orderBy("uuid")
                .actionAfterQuery(resultSet -> {
                    boolean next = resultSet.next();
                    System.out.println("Has next: " + next);
                    if(next){
                        System.out.println("uuid: " + resultSet.getString("uuid"));
                        System.out.println("Comment: " + resultSet.getString("comment"));
                    }
                });

        var selectQuery3 = Query.select()
                .table("test6")
                .orderBy("uuid", Order.Direction.DESCENDING)
                .limit(1, 1)
                .actionAfterQuery(resultSet -> {
                    boolean next = resultSet.next();
                    System.out.println("Has next: " + next);
                    if(next){
                        System.out.println("uuid: " + resultSet.getString("uuid"));
                        System.out.println("Comment: " + resultSet.getString("comment"));
                    }
                });


        System.out.println("------  \tTableCreate\t  ------");
        new Query(databaseAdapter).queries(tableCreateQuery).execute();
        System.out.println("------  \t Insert \t  ------");
        new Query(databaseAdapter).queries(insertQuery).execute();
        System.out.println("------  \tSelect 1\t  ------");
        new Query(databaseAdapter).queries(selectQuery).execute();
        System.out.println("------  \tUpdate\t  ------");
        new Query(databaseAdapter).queries(updateQuery).execute();
        System.out.println("------  \tSelect 2\t  ------");
        new Query(databaseAdapter).queries(selectQuery2).execute();
        System.out.println("------  \tSelect 3\t  ------");
        new Query(databaseAdapter).queries(selectQuery3).execute();
    }
}
