package dev.coph.simplesql.query.providers;

import de.codingphoenix.phoenixbase.check.Checks;
import de.codingphoenix.phoenixbase.database.request.tablealter.TableAlterRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Action for the {@link TableAlterRequest} that will add an attribute to a column.
 */
@Setter
@Getter
@Accessors(fluent = true)
public class TableAlterAddAttributeAction implements TableAlterRequest.AlterRequestAction {

    /**
     * The name of the column the attribute should be added.
     */
    private String columnName;

    /**
     * The Type of the new column.
     */
    private AttributeType attributeType;

    @Override
    public StringBuilder generateSQL() {
        Checks.checkIfNullOrEmptyMap(columnName, "columnName");
        Checks.checkIfNullOrEmptyMap(attributeType, "attributeType");
        return new StringBuilder("ADD ").append(attributeType.name().replaceAll("_", " ")).append(" (").append(columnName).append(")");
    }

    /**
     * The type of attribute to add.
     */
    public enum AttributeType {
        UNIQUE, PRIMARY_KEY
    }

//    ALTER TABLE tabellenname ADD UNIQUE (spaltenname);
//    ALTER TABLE tabellenname ADD PRIMARY KEY (spaltenname);

}
