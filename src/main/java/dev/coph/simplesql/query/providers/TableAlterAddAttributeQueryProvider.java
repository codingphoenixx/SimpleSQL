package dev.coph.simplesql.query.providers;


import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.utils.Check;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Action for the {@link TableAlterQueryProvider} that will add an attribute to a column.
 */
@Setter
@Getter
@Accessors(fluent = true)
public class TableAlterAddAttributeQueryProvider extends TableAlterQueryProvider {

    /**
     * The name of the column the attribute should be added.
     */
    private String columnName;

    /**
     * The Type of the new column.
     */
    private AttributeType attributeType;



    @Override
    public String getAlterTableString(Query query) {
        Check.ifNullOrEmptyMap(columnName, "columnName");
        Check.ifNullOrEmptyMap(attributeType, "attributeType");
        return new StringBuilder("ADD ").append(attributeType.name().replaceAll("_", " ")).append(" (").append(columnName).append(")").toString();
    }

    /**
     * The type of attribute to add.
     */
    public enum AttributeType {
        UNIQUE, PRIMARY_KEY
    }


}
