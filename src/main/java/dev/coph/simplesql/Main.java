package dev.coph.simplesql;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.Column;
import dev.coph.simplesql.database.attributes.ColumnType;
import dev.coph.simplesql.database.attributes.CreateMethode;
import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.database.attributes.InsertMethode;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.providers.InsertQueryProvider;
import dev.coph.simplesql.query.providers.TableCreateQueryProvider;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        startMariaDB();
        startSQLLite();
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

    private static void startSQLLite() {
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
                .entry("uuid", "1234567890");

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


        new Query(databaseAdapter).queries(tableCreateQuery).execute();
        new Query(databaseAdapter).queries(insertQuery).execute();
        new Query(databaseAdapter).queries(selectQuery).execute();
    }
}
