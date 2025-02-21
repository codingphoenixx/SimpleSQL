package dev.coph.simplesql;

import dev.coph.simplelogger.Logger;
import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.Condition;
import dev.coph.simplesql.database.attributes.Group;
import dev.coph.simplesql.database.attributes.Operator;
import dev.coph.simplesql.database.attributes.SelectFunction;
import dev.coph.simplesql.database.functions.numeric.NumericFunction;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.providers.InsertQueryProvider;
import dev.coph.simplesql.query.providers.SelectQueryProvider;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Base64;
import java.util.UUID;

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
        //Database database = new Database(databaseAdapter, "testing");
        //database.getTable("testing");

        /*
        TODO:
        SELECT department_id FROM employees GROUP BY department_id HAVING COUNT (*) > 3
        SELECT * FROM employees WHERE last_name SIMILAR TO '[STU]%' AND salary BETWEEN 1000 AND 10000 ORDER BY last_name
        SELECT first_name, last_name, salary, job_title FROM employees JOIN jobs ON employees.job_id = jobs.job_id ORDER BY salary DESC LIMIT 1
        SELECT first_name, last_name, salary AS netto, salary * 1.3 AS brutto FROM employees
         */



        SelectQueryProvider selectQueryProvider = new SelectQueryProvider()
                .table("test6")
                .function(new NumericFunction.Count("*"))
                .group(new Group().key("number").condition(new Condition("number", Operator.GREATER_THAN, 10)))
                .actionAfterQuery(resultSet -> {
                    parseResultSet(resultSet);
                });

        new Query(databaseAdapter).executeQuery(selectQueryProvider);
    }

    private static void createDefaultEntries(DatabaseAdapter databaseAdapter){
        Query insertQuery = new Query(databaseAdapter);
        for (int i = 0; i < 1000; i++) {
            UUID uuid = UUID.randomUUID();
            String comment = Base64.getEncoder().encodeToString(uuid.toString().getBytes());
            InsertQueryProvider insertQueryProvider = new InsertQueryProvider()
                    .table("test6")
                    .entry("uuid", uuid.toString())
                    .entry("comment", comment);
            insertQuery.queries(insertQueryProvider);
        }
        insertQuery.execute();
    }
    private static void parseResultSet(ResultSet resultSet){
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            StringBuilder header = new StringBuilder("|");
            for (int i = 1; i <= columnCount; i++) {
                header.append(String.format(" %-20s |", metaData.getColumnName(i)));
            }
            System.out.println(header);
            System.out.println("-".repeat(header.length()));
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
