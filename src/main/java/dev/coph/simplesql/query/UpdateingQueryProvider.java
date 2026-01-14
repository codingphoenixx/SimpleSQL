package dev.coph.simplesql.query;

public abstract class UpdateingQueryProvider implements QueryProvider {

    private int affectedRows;


    public int affectedRows() {
        return affectedRows;
    }

    public UpdateingQueryProvider affectedRows(int affectedRows) {
        this.affectedRows = affectedRows;
        return this;
    }
}
