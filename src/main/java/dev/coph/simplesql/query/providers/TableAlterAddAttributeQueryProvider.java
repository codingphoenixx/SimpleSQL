package dev.coph.simplesql.query.providers;


import dev.coph.simplesql.driver.DriverCompatibility;
import dev.coph.simplesql.query.Query;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

/**
 * A class that generates SQL "ALTER TABLE" queries for adding attributes to
 * a specified column. This class extends {@code TableAlterQueryProvider} and
 * specializes in constructing statements to add attributes such as "UNIQUE" or
 * "PRIMARY KEY" to existing columns in a database table.
 */
public class TableAlterAddAttributeQueryProvider extends TableAlterQueryProvider {

    /**
     * The name of the column the attribute should be added.
     */
    private String columnName;

    /**
     * The Type of the new column.
     */
    private AttributeType attributeType;
    private RunnableAction<Boolean> actionAfterQuery;

    @Override
    public DriverCompatibility compatibility() {
        return driverType -> true;
    }

    @Override
    public String getAlterTableString(Query query) {
        Check.ifNullOrEmptyMap(columnName, "columnName");
        Check.ifNullOrEmptyMap(attributeType, "attributeType");
        return "ADD " + attributeType.name().replaceAll("_", " ") + " (" + columnName + ")";
    }

    /**
     * Retrieves the name of the column to which the attribute should be added.
     *
     * @return the name of the column as a string.
     */
    public String columnName() {
        return this.columnName;
    }

    /**
     * Retrieves the type of the attribute to add.
     *
     * @return the attribute type, represented as an instance of {@code AttributeType}.
     */
    public AttributeType attributeType() {
        return this.attributeType;
    }

    /**
     * Sets the name of the column to which the attribute should be added.
     *
     * @param columnName the name of the column as a string
     * @return the current instance of TableAlterAddAttributeQueryProvider for method chaining
     */
    public TableAlterAddAttributeQueryProvider columnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public TableAlterAddAttributeQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    /**
     * Sets the type of the attribute to be added to the column.
     *
     * @param attributeType the type of the attribute, represented as an instance of {@code AttributeType}
     * @return the current instance of {@code TableAlterAddAttributeQueryProvider} for method chaining
     */
    public TableAlterAddAttributeQueryProvider attributeType(AttributeType attributeType) {
        this.attributeType = attributeType;
        return this;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }

    /**
     * The type of attribute to add.
     */
    public enum AttributeType {
        /**
         * Represents a type of attribute that enforces uniqueness on the values
         * of the associated column or field in a database context. This constraint
         * ensures that all values in the column or field are distinct, preventing
         * duplicate entries.
         */
        UNIQUE,
        /**
         * Designates an attribute that serves as the primary key in a database context.
         * A primary key uniquely identifies each record in a table and ensures that
         * no duplicate values exist in the associated column.
         */
        PRIMARY_KEY
    }


}
