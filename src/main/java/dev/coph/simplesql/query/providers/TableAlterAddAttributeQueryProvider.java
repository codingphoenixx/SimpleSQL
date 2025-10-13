package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class TableAlterAddAttributeQueryProvider extends TableAlterQueryProvider {

    private String columnName;
    private List<String> columns;

    private AttributeType attributeType;

    private String constraintName;
    private String indexName;

    private RunnableAction<Boolean> actionAfterQuery;

    @Override
    public String getAlterTableString(Query query) {
        Check.ifNull(attributeType, "attributeType");

        var driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        DatabaseCheck.missingDriver(driver);

        List<String> cols = new ArrayList<>();
        if (columns != null && !columns.isEmpty()) {
            cols.addAll(columns);
        } else {
            Check.ifNullOrEmptyMap(columnName, "columnName");
            cols.add(columnName);
        }

        String colList = renderColumnList(cols);

        return switch (attributeType) {
            case PRIMARY_KEY -> renderAddPrimaryKey(driver, colList);
            case UNIQUE -> renderAddUnique(driver, colList);
        };
    }

    private String renderAddPrimaryKey(DriverType driver, String colList) {
        return switch (driver) {
            case MYSQL, MARIADB -> "ADD PRIMARY KEY " + "(" + colList + ")";
            case POSTGRESQL -> {
                if (constraintName != null && !constraintName.isBlank()) {
                    yield "ADD CONSTRAINT " + constraintName + " PRIMARY KEY (" + colList + ")";
                } else {
                    yield "ADD PRIMARY KEY (" + colList + ")";
                }
            }
            default -> throw new FeatureNotSupportedException(driver);
        };
    }

    private String renderAddUnique(DriverType driver, String colList) {
        return switch (driver) {
            case MYSQL, MARIADB -> {
                if (constraintName != null && !constraintName.isBlank()) {
                    yield "ADD CONSTRAINT " + constraintName + " UNIQUE (" + colList + ")";
                } else if (indexName != null && !indexName.isBlank()) {
                    yield "ADD UNIQUE INDEX " + indexName + " (" + colList + ")";
                } else {
                    yield "ADD UNIQUE (" + colList + ")";
                }
            }
            case POSTGRESQL -> {
                if (constraintName != null && !constraintName.isBlank()) {
                    yield "ADD CONSTRAINT " + constraintName + " UNIQUE (" + colList + ")";
                } else {
                    yield "ADD UNIQUE (" + colList + ")";
                }
            }
            default -> throw new FeatureNotSupportedException(driver);
        };
    }

    private String renderColumnList(List<String> cols) {
        StringJoiner joiner = new StringJoiner(", ");
        for (String c : cols) {
            Check.ifNullOrEmptyMap(c, "column in columns");
            joiner.add(c);
        }
        return joiner.toString();
    }

    public String columnName() {
        return this.columnName;
    }

    public List<String> columns() {
        return this.columns;
    }

    public AttributeType attributeType() {
        return this.attributeType;
    }

    public String constraintName() {
        return this.constraintName;
    }

    public String indexName() {
        return this.indexName;
    }

    public TableAlterAddAttributeQueryProvider columnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public TableAlterAddAttributeQueryProvider columns(List<String> columns) {
        this.columns = columns != null ? new ArrayList<>(columns) : null;
        return this;
    }

    public TableAlterAddAttributeQueryProvider attributeType(AttributeType attributeType) {
        this.attributeType = attributeType;
        return this;
    }

    public TableAlterAddAttributeQueryProvider constraintName(String constraintName) {
        this.constraintName = constraintName;
        return this;
    }

    public TableAlterAddAttributeQueryProvider indexName(String indexName) {
        this.indexName = indexName;
        return this;
    }

    public TableAlterAddAttributeQueryProvider actionAfterQuery(
            RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }

    public enum AttributeType {
        UNIQUE,
        PRIMARY_KEY
    }
}
