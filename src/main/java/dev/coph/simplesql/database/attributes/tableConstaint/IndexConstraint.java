package dev.coph.simplesql.database.attributes.tableConstaint;

import java.util.List;

public final class IndexConstraint implements TableConstraint {
    private final String name;
    private final List<String> columns;
    private final boolean unique;

    public IndexConstraint(String name, List<String> columns, boolean unique) {
        this.name = name;
        this.columns = columns;
        this.unique = unique;
    }

    public String name() {
        return name;
    }

    public List<String> columns() {
        return columns;
    }

    public boolean unique() {
        return unique;
    }
}
