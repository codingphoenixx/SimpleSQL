package dev.coph.simplesql.database.attributes;

public class Offset {

    private final  int count;

    public Offset(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return " OFFSET " + count;
    }
}
