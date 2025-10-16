package dev.coph.simplesql.database.attributes.tableConstaint;

import java.util.List;

public final class PrimaryKeyConstraint implements TableConstraint {
    private final String name;
    private final List<String> columns;

    public PrimaryKeyConstraint(String name, List<String> columns) {
        this.name = name;
        this.columns = columns;
    }

    public String name() {
        return name;
    }

    public List<String> columns() {
        return columns;
    }
}
