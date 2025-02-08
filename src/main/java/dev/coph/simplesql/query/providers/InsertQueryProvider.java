package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.adapter.DatabaseAdapter;
import dev.coph.simplesql.database.attributes.InsertMethode;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryEntry;
import dev.coph.simplesql.query.QueryProvider;
import dev.coph.simpleutilities.check.Check;
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
 * <p>
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
    public String generateSQLString(Query query) {
        Check.ifNullOrEmptyMap(entries, "entries");
        Check.ifNullOrEmptyMap(table, "tablename");

        StringBuilder sql = new StringBuilder("INSERT ");

        if (insertMethode.equals(InsertMethode.INSERT_IGNORE)) {
            if (query.databaseAdapter() == null) {
                return null;
            }
            if (query.databaseAdapter().driverType().equals(DatabaseAdapter.DriverType.MYSQL) || query.databaseAdapter().driverType().equals(DatabaseAdapter.DriverType.MARIADB)) {
                sql.append("IGNORE ");
            } else if (query.databaseAdapter().driverType().equals(DatabaseAdapter.DriverType.SQLITE)) {
                sql.append("OR IGNORE ");
            }
        }


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
     * Method which will be used for inserting the value.
     */
    @Setter
    private InsertMethode insertMethode = InsertMethode.INSERT;
}
