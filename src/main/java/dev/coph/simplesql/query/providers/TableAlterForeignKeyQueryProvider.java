package dev.coph.simplesql.query.providers;

import dev.coph.simplesql.database.attributes.ActionType;
import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.exception.FeatureNotSupportedException;
import dev.coph.simplesql.query.Query;
import dev.coph.simplesql.utils.DatabaseCheck;
import dev.coph.simpleutilities.action.RunnableAction;
import dev.coph.simpleutilities.check.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


public class TableAlterForeignKeyQueryProvider extends TableAlterQueryProvider {

    private ActionType action;
    private String constraintName;
    private List<String> columns = new ArrayList<>();
    private String referencedTable;
    private List<String> referencedColumns = new ArrayList<>();
    private ReferentialAction onDelete;
    private ReferentialAction onUpdate;
    private DeferrableType deferrable;
    private InitiallyDeferrable initiallyDeferred;
    private RunnableAction<Boolean> actionAfterQuery;

    @Override
    public String getAlterTableString(Query query) {
        Check.ifNull(action, "action");
        return switch (action) {
            case ADD -> buildAddForeignKey(query);
            case DROP -> buildDropForeignKey(query);
            default -> throw new UnsupportedOperationException("Unsupported action: " + action);
        };
    }

    private String buildAddForeignKey(Query query) {
        Check.ifNullOrEmptyMap(table, "table name");
        Check.ifNullOrEmptyMap(columns, "columns");
        Check.ifNullOrEmptyMap(referencedTable, "referencedTable");
        Check.ifNullOrEmptyMap(referencedColumns, "referencedColumns");

        if (columns.size() != referencedColumns.size()) {
            throw new IllegalArgumentException("columns and referencedColumns must have same size");
        }

        DriverType driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;


        DatabaseCheck.missingDriver(driver);

        StringBuilder sb = new StringBuilder();

        switch (driver) {
            case MYSQL, MARIADB -> {
                sb.append("ADD ");
                if (constraintName != null && !constraintName.isBlank()) {
                    sb.append("CONSTRAINT ").append(constraintName).append(" ");
                }
                sb.append("FOREIGN KEY (").append(joinIdents(columns, query)).append(") ")
                        .append("REFERENCES ").append(referencedTable)
                        .append(" (").append(joinIdents(referencedColumns, query)).append(")");

                appendReferentialActions(sb, driver);

                if (deferrable != null || initiallyDeferred != null) {
                    throw new FeatureNotSupportedException(driver);
                }
                return sb.toString();
            }
            case POSTGRESQL -> {
                sb.append("ADD ");
                if (constraintName != null && !constraintName.isBlank()) {
                    sb.append("CONSTRAINT ").append(constraintName).append(" ");
                }
                sb.append("FOREIGN KEY (").append(joinIdents(columns, query)).append(") ")
                        .append("REFERENCES ").append(referencedTable)
                        .append(" (").append(joinIdents(referencedColumns, query)).append(")");

                appendReferentialActions(sb, driver);

                if (deferrable != null && deferrable != DeferrableType.NO) {
                    sb.append(deferrable.name().replaceAll("_", " "));
                }
                if (initiallyDeferred != null && initiallyDeferred != InitiallyDeferrable.NO) {
                    sb.append(initiallyDeferred.name().replaceAll("_", " "));
                }
                return sb.toString();
            }
            default -> throw new FeatureNotSupportedException(driver);
        }
    }

    private String buildDropForeignKey(Query query) {
        DriverType driver = query.databaseAdapter() != null
                ? query.databaseAdapter().driverType()
                : null;

        DatabaseCheck.missingDriver(driver);

        switch (driver) {
            case MYSQL, MARIADB -> {
                Check.ifNullOrEmptyMap(constraintName, "constraintName");
                return "DROP FOREIGN KEY " + constraintName;
            }
            case POSTGRESQL -> {
                Check.ifNullOrEmptyMap(constraintName, "constraintName");
                return "DROP CONSTRAINT " + constraintName;
            }
            default -> throw new FeatureNotSupportedException(driver);
        }
    }

    private void appendReferentialActions(StringBuilder sb, DriverType driver) {
        if (onDelete != null) {
            sb.append(" ON DELETE ").append(mapAction(onDelete, driver));
        }
        if (onUpdate != null) {
            sb.append(" ON UPDATE ").append(mapAction(onUpdate, driver));
        }
    }

    private String mapAction(ReferentialAction action, DriverType driver) {
        return action.name().replaceAll("_", " ");
    }

    private String joinIdents(List<String> idents, Query query) {
        StringJoiner j = new StringJoiner(", ");
        for (String s : idents) {
            Check.ifNullOrEmptyMap(s, "identifier");
            j.add(s);
        }
        return j.toString();
    }

    public TableAlterForeignKeyQueryProvider action(ActionType action) {
        this.action = action;
        return this;
    }

    public TableAlterForeignKeyQueryProvider constraintName(String name) {
        this.constraintName = name;
        return this;
    }

    public TableAlterForeignKeyQueryProvider column(String column) {
        this.columns.add(column);
        return this;
    }

    public TableAlterForeignKeyQueryProvider columns(List<String> columns) {
        if (columns != null) this.columns.addAll(columns);
        return this;
    }

    public TableAlterForeignKeyQueryProvider referencedTable(String table) {
        this.referencedTable = table;
        return this;
    }

    public TableAlterForeignKeyQueryProvider referencedColumn(String column) {
        this.referencedColumns.add(column);
        return this;
    }

    public TableAlterForeignKeyQueryProvider referencedColumns(List<String> columns) {
        if (columns != null) this.referencedColumns.addAll(columns);
        return this;
    }

    public TableAlterForeignKeyQueryProvider onDelete(ReferentialAction action) {
        this.onDelete = action;
        return this;
    }

    public TableAlterForeignKeyQueryProvider onUpdate(ReferentialAction action) {
        this.onUpdate = action;
        return this;
    }

    public TableAlterForeignKeyQueryProvider deferrable(DeferrableType deferrable) {
        this.deferrable = deferrable;
        return this;
    }

    public TableAlterForeignKeyQueryProvider initiallyDeferred(InitiallyDeferrable initiallyDeferred) {
        this.initiallyDeferred = initiallyDeferred;
        return this;
    }

    public TableAlterForeignKeyQueryProvider actionAfterQuery(RunnableAction<Boolean> actionAfterQuery) {
        this.actionAfterQuery = actionAfterQuery;
        return this;
    }

    @Override
    public RunnableAction<Boolean> actionAfterQuery() {
        return actionAfterQuery;
    }

    public enum ReferentialAction {
        NO_ACTION, RESTRICT, CASCADE, SET_NULL, SET_DEFAULT
    }

    public enum DeferrableType {NO, DEFERRABLE, NOT_DEFERRABLE}

    public enum InitiallyDeferrable {NO, INITIALLY_DEFERRED, INITIALLY_IMMEDIATE}
}
