package dev.coph.simplesql.database.attributes.tableConstaint;

public sealed interface TableConstraint permits PrimaryKeyConstraint, UniqueConstraint, IndexConstraint, ForeignKeyConstraint, CheckConstraint {
    String name();
}

