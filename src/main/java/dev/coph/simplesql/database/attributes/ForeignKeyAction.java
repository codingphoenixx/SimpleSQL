package dev.coph.simplesql.database.attributes;

public enum ForeignKeyAction {
    RESTRICT,
    NO_ACTION,
    SET_NULL,
    SET_DEFAULT,
    CASCADE;

    public String sql() {
        switch (this) {
            case CASCADE -> {
                return "CASCADE";
            }
            case NO_ACTION -> {
                return "NO ACTION";
            }
            case RESTRICT -> {
                return "RESTRICT";
            }
            case SET_DEFAULT -> {
                return "SET DEFAULT";
            }
            case SET_NULL -> {
                return "SET NULL";
            }
            default -> {
                throw new IllegalStateException("Unexpected value: " + this);
            }
        }
    }
}
