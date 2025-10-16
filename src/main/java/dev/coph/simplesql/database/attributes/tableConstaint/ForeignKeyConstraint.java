package dev.coph.simplesql.database.attributes.tableConstaint;

import dev.coph.simplesql.database.attributes.ForeignKeyAction;

import java.util.List;

public final class ForeignKeyConstraint implements TableConstraint {
    private final String name;
    private final List<String> columns;
    private final String refTable;
    private final List<String> refColumns;
    private final ForeignKeyAction onDelete;
    private final ForeignKeyAction onUpdate;

    public ForeignKeyConstraint(String name, List<String> columns, String refTable, List<String> refColumns, ForeignKeyAction onDelete, ForeignKeyAction onUpdate) {
        this.name = name;
        this.columns = columns;
        this.refTable = refTable;
        this.refColumns = refColumns;
        this.onDelete = onDelete;
        this.onUpdate = onUpdate;
    }

    public String name() {
        return name;
    }

    public List<String> columns() {
        return columns;
    }

    public String refTable() {
        return refTable;
    }

    public List<String> refColumns() {
        return refColumns;
    }

    public ForeignKeyAction onDelete() {
        return onDelete;
    }

    public ForeignKeyAction onUpdate() {
        return onUpdate;
    }
}
