package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.Column;
import dev.coph.simplesql.database.attributes.ColumnPosition;
import dev.coph.simplesql.database.attributes.ColumnType;
import dev.coph.simplesql.database.attributes.CreateMethode;
import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.query.QueryEntry;
import dev.coph.simplesql.utils.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * A query provider class for generating SQL "ALTER TABLE" statements to add a new column to a table.
 * This class extends the {@link TableAlterQueryProvider} and implements the specific logic required
 * to construct the "ADD COLUMN" action within an "ALTER TABLE" statement.
 */
@Setter
@Getter
@Accessors(fluent = true)
public class TableAlterAddColumnQueryProvider extends TableAlterQueryProvider {

    /**
     * The position of the column.
     */
    private ColumnPosition postion = ColumnPosition.DEFAULT;
    /**
     * If {@link ColumnPosition} is set to {@code  ColumnPosition.AFTER} the name of the column the new will be added after.
     */
    private String afterColumnName;


    /**
     * Defines the strategy for creating database structures such as tables
     * as part of the query generation process within the {@code TableAlterAddColumnQueryProvider}.
     *
     * The {@code createMethode} variable determines how the database structure
     * creation logic is applied, based on the selected strategy from the
     * {@link CreateMethode} enumeration. It defaults to {@link CreateMethode#DEFAULT}.
     */
    private CreateMethode createMethode = CreateMethode.DEFAULT;

    /**
     * Represents a column to be added to the table schema in the context of a table alteration query.
     * The column encapsulates the properties and metadata required for defining a database column,
     * such as name, data type, and constraints.
     */
    private Column column;

    @Override
    public String getAlterTableString(Query query) {
        Check.ifNull(column, "column");

        StringBuilder stringBuilder = new StringBuilder("ADD COLUMN ").append((createMethode == CreateMethode.IF_NOT_EXISTS ? "IF NOT EXISTS " : null)).append(column.toString(query));

        if (postion == ColumnPosition.DEFAULT) {
            return stringBuilder.toString();
        } else if (postion == ColumnPosition.FIRST) {
            stringBuilder.append(" FIRST");
        } else if (postion == ColumnPosition.AFTER) {
            stringBuilder.append(" AFTER ").append(afterColumnName);
        }

        return stringBuilder.toString();
    }


    /**
     * Adds a column to the list of columns to be included in the table creation query.
     * If the list of columns has not been initialized, it is initialized before adding the column.
     * This method supports a fluent API style, allowing method chaining.
     *
     * @param column the Column object representing the database column to be added to the table schema
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider column(Column column) {
        this.column = column;
        return this;
    }

    /**
     * Adds a column with the specified key and data type to the list of columns
     * to be included in the table creation query. If the list of columns has not
     * been initialized, it is initialized before adding the column. This method
     * supports a fluent API style, allowing method chaining.
     *
     * @param key      the name of the column to be added
     * @param dataType the data type of the column to be added
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider column(String key, DataType dataType) {
        this.column = new Column(key, dataType);
        return this;
    }

    /**
     * Adds a column with the specified key, data type, and an additional parameter object
     * to the list of columns to be included in the table creation query.
     * If the list of columns has not been initialized, it is initialized before adding the column.
     * This method supports a fluent API style, allowing method chaining.
     *
     * @param key                     the name of the column to be added
     * @param dataType                the data type of the column to be added
     * @param dataTypeParameterObject an additional parameter object associated with the column's data type
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider column(String key, DataType dataType, Object dataTypeParameterObject) {
        this.column = new Column(key, dataType, dataTypeParameterObject);
        return this;
    }

    /**
     * Adds a column with the specified key, data type, and an additional parameter object
     * to the list of columns to be included in the table creation query.
     * If the list of columns has not been initialized, it is initialized before adding the column.
     * This method supports a fluent API style, allowing method chaining.
     *
     * @param key      the name of the column to be added
     * @param dataType the data type of the column to be added
     * @param notNull  if the column is allowed to have no value
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider column(String key, DataType dataType, boolean notNull) {
        this.column = new Column(key, dataType, notNull);
        return this;
    }

    /**
     * Adds a column with the specified key, data type, and an additional parameter object
     * to the list of columns to be included in the table creation query.
     * If the list of columns has not been initialized, it is initialized before adding the column.
     * This method supports a fluent API style, allowing method chaining.
     *
     * @param key                     the name of the column to be added
     * @param dataType                the data type of the column to be added
     * @param dataTypeParameterObject an additional parameter object associated with the column's data type
     * @param notNull                 if the column is allowed to have no value
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider column(String key, DataType dataType, Object dataTypeParameterObject, boolean notNull) {
        this.column = new Column(key, dataType, dataTypeParameterObject, notNull);
        return this;
    }

    /**
     * Adds a column with the specified key, data type, an additional parameter object,
     * and column type to the list of columns to be included in the table creation query.
     * If the list of columns has not been initialized, it is initialized before adding the column.
     * This method supports a fluent API style, allowing method chaining.
     *
     * @param key                     the name of the column to be added
     * @param dataType                the data type of the column to be added
     * @param dataTypeParameterObject an additional parameter object associated
     *                                with the column's data type
     * @param columnType              the type of the column (e.g., primary key, normal column, etc.)
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider column(String key, DataType dataType, Object dataTypeParameterObject, ColumnType columnType) {
        this.column = new Column(key, dataType, dataTypeParameterObject, columnType);
        return this;
    }

    /**
     * Adds a column with the specified key, data type, an additional parameter object,
     * and column type to the list of columns to be included in the table creation query.
     * If the list of columns has not been initialized, it is initialized before adding the column.
     * This method supports a fluent API style, allowing method chaining.
     *
     * @param key                     the name of the column to be added
     * @param dataType                the data type of the column to be added
     * @param dataTypeParameterObject an additional parameter object associated
     *                                with the column's data type
     * @param columnType              the type of the column (e.g., primary key, normal column, etc.)
     * @param notNull                 if the column is allowed to be null
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider column(String key, DataType dataType, Object dataTypeParameterObject, ColumnType columnType, boolean notNull) {
        this.column = new Column(key, dataType, dataTypeParameterObject, columnType, notNull);
        return this;
    }


    /**
     * Adds a column with the specified key, data type, and column type to the list of columns
     * to be included in the table creation query. If the list of columns has not been initialized,
     * it is initialized before adding the column. This method supports a fluent API style,
     * allowing method chaining.
     *
     * @param key        the name of the column to be added
     * @param dataType   the data type of the column to be added
     * @param columnType the type of the column (e.g., primary key, normal column, etc.)
     * @return the current instance of TableCreateQueryProvider for method chaining
     */
    public TableAlterAddColumnQueryProvider column(String key, DataType dataType, ColumnType columnType) {
        this.column = new Column(key, dataType, columnType);
        return this;
    }
}
