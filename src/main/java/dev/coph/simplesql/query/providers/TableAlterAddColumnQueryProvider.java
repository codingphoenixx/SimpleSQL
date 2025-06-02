package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.Column;
import dev.coph.simplesql.database.attributes.ColumnPosition;
import dev.coph.simplesql.database.attributes.ColumnType;
import dev.coph.simplesql.database.attributes.CreateMethode;
import dev.coph.simplesql.database.attributes.DataType;
import dev.coph.simplesql.query.Query;
import dev.coph.simpleutilities.check.Check;
import lombok.experimental.Accessors;

/**
 * A query provider class for generating SQL "ALTER TABLE" statements to add a new column to a table.
 * This class extends the {@link TableAlterQueryProvider} and implements the specific logic required
 * to construct the "ADD COLUMN" action within an "ALTER TABLE" statement.
 */
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
     * <p>
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

    /**
     * Retrieves the position of the column within the table schema or query structure.
     *
     * @return the position of the column, represented as an instance of the ColumnPosition enum
     *         (e.g., DEFAULT, FIRST, AFTER).
     */
    public ColumnPosition postion() {
        return this.postion;
    }

    /**
     * Retrieves the name of the column after which the new column will be positioned.
     *
     * @return the name of the column after which the new column will be added, as a String.
     */
    public String afterColumnName() {
        return this.afterColumnName;
    }

    /**
     * Retrieves the create method used for altering or creating the table schema.
     *
     * @return the current instance of {@code CreateMethode}, representing the strategy
     *         for database structure creation (e.g., DEFAULT, IF_NOT_EXISTS).
     */
    public CreateMethode createMethode() {
        return this.createMethode;
    }

    /**
     * Retrieves the column associated with the current instance of the table alter query provider.
     *
     * @return the column associated with the current table alteration, as an instance of the Column class.
     */
    public Column column() {
        return this.column;
    }

    /**
     * Sets the position of the column within the table schema or query structure.
     * This method supports a fluent API style, allowing method chaining.
     *
     * @param postion the position of the column, represented as an instance of
     *                {@code ColumnPosition} enum (e.g., DEFAULT, FIRST, AFTER)
     * @return the current instance of {@code TableAlterAddColumnQueryProvider} for method chaining
     */
    public TableAlterAddColumnQueryProvider postion(ColumnPosition postion) {
        this.postion = postion;
        return this;
    }

    /**
     * Sets the name of the column after which a new column will be added.
     * This method supports a fluent API style, allowing method chaining.
     *
     * @param afterColumnName the name of the column after which the new column will be added
     * @return the current instance of {@code TableAlterAddColumnQueryProvider} for method chaining
     */
    public TableAlterAddColumnQueryProvider afterColumnName(String afterColumnName) {
        this.afterColumnName = afterColumnName;
        return this;
    }

    /**
     * Sets the create method to be used for altering or creating the table schema.
     * This method supports a fluent API style, enabling method chaining.
     *
     * @param createMethode an instance of {@code CreateMethode} that specifies the strategy for database structure creation
     * @return the current instance of {@code TableAlterAddColumnQueryProvider} for method chaining
     */
    public TableAlterAddColumnQueryProvider createMethode(CreateMethode createMethode) {
        this.createMethode = createMethode;
        return this;
    }
}
