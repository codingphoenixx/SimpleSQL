package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.query.QueryEntry;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simplesql.utils.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code InsertQueryProvider} class is responsible for constructing and generating
 * SQL INSERT statements. It supports various INSERT methods such as standard INSERT,
 * INSERT with IGNORE, and INSERT with ON DUPLICATE KEY UPDATE. These operations can
 * be tailored to handle different data insertion use cases.
 *
 * This class implements the {@link QueryProvider} interface, enabling the generation of
 * SQL query strings for database operations.
 */
@Getter
@Accessors(fluent = true, chain = true)
public class InsertQueryProvider implements QueryProvider {

    /**
     * The name of the table that should be deleted.
     */
    @Setter
    private String table;

    /**
     * The entries which will be written to the Database.
     */
    private List<QueryEntry> entries;

    /**
     * Adds an entry which will be written to the Database.
     *
     * @param column Name of the column
     * @param value  Value that will be inserted
     * @return {@link InsertQueryProvider} for chaining.
     */
    public InsertQueryProvider entry(String column, Object value) {
        if (entries == null) {
            entries = new ArrayList<>();
        }
        entries.add(new QueryEntry(column, value));
        return this;
    }


    @Override
    public String generateSQLString() {
        Check.ifNullOrEmptyMap(entries, "entries");
        Check.ifNullOrEmptyMap(table, "tablename");

        StringBuilder sql = new StringBuilder("INSERT ");

        if (insertMethode.equals(InsertMethode.INSERT_IGNORE))
            sql.append("IGNORE ");

        sql.append("INTO ").append(table);

        StringBuilder columString = null;
        StringBuilder objectString = null;
        for (QueryEntry entry : entries) {
            if (columString == null) {
                columString = new StringBuilder(entry.columName());
            } else {
                columString.append(", ").append(entry.columName());
            }
            if (objectString == null) {
                objectString = new StringBuilder(entry.sqlValue());
            } else {
                objectString.append(", ").append(entry.sqlValue());
            }
        }
        sql.append("(").append(columString).append(") VALUES (").append(objectString).append(")");

        if (insertMethode.equals(InsertMethode.INSERT_OR_UPDATE))
            sql.append(generateOnDuplicateString());

        sql.append(";");

        return sql.toString();
    }

    /**
     * Generates the sql string for insert with duplicated key for executing
     *
     * @return the sql string
     */
    private StringBuilder generateOnDuplicateString() {
        StringBuilder sql = new StringBuilder(" ON DUPLICATE KEY UPDATE ");

        StringBuilder insertString = null;
        for (QueryEntry entry : entries) {
            if (insertString == null) {
                insertString = new StringBuilder(entry.columName()).append(" = ").append(entry.sqlValue());
            } else {
                insertString.append(", ").append(entry.columName()).append(" = ").append(entry.sqlValue());
            }
        }
        return sql.append(insertString);
    }


    /**
     * The {@code InsertMethode} enumeration defines various strategies for SQL INSERT operations.
     * This is used to specify the type of INSERT operation to be performed when constructing
     * and executing a SQL query.
     */
    public enum InsertMethode {
        /**
         * Represents the INSERT operation in the {@link InsertMethode} enumeration.
         * This method indicates a standard SQL INSERT operation without any additional conditions or checks.
         * It is used to add new rows of data into a database table.
         */
        INSERT,
        /**
         * Represents the INSERT_OR_UPDATE operation in the {@link InsertMethode} enumeration.
         * This method combines an INSERT operation with an on-duplicate-key update mechanism.
         * If a row with the same unique key already exists in the database, the existing row is updated
         * with the new values provided in the query instead of inserting a duplicate row.
         * It is commonly used for scenarios where upsertion (update or insert) is required.
         */
        INSERT_OR_UPDATE,
        /**
         * Represents the INSERT_IGNORE operation in the {@link InsertMethode} enumeration.
         * This method performs an INSERT operation while ignoring rows that would violate
         * a unique or primary key constraint. Instead of failing due to a duplicate key error,
         * the operation skips the conflicting row and continues with the next one.
         * Commonly used to avoid interruptions when inserting data where some conflicts are permissible.
         */
        INSERT_IGNORE
    }

    /**
     * Method which will be used for inserting the value.
     */
    @Setter
    private InsertMethode insertMethode = InsertMethode.INSERT;
}
