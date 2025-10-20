package dev.coph.simplesql.database.attributes.tableConstaint;

import java.util.List;

public record UniqueConstraint(String name, List<String> columns) implements TableConstraint {
}
