package dev.coph.simplesql.database.attributes;

/**
 * The {@code ForeignKeyAction} enum represents the actions that can be performed
 * on a foreign key constraint when a referenced entity is modified or deleted.
 * It supports the typical behaviors defined by SQL standards.
 */
public enum ForeignKeyAction {
    /**
     * Represents the SQL {@code RESTRICT} action for foreign key constraints.
     * <p>
     * The {@code RESTRICT} action prevents the deletion or update of a referenced entity
     * if any dependent rows exist in the referencing table. It ensures referential integrity
     * by rejecting operations that would leave orphaned records in the database.
     */
    RESTRICT,
    /**
     * Represents the SQL {@code NO ACTION} action for foreign key constraints.
     * <p>
     * The {@code NO ACTION} action defers referential integrity checks until the end of the
     * current transaction. If any referential integrity violations are detected at that point,
     * the transaction will be rolled back. This differs from {@code RESTRICT}, which checks
     * the constraints immediately.
     */
    NO_ACTION,
    /**
     * Represents the SQL {@code SET NULL} action for foreign key constraints.
     * <p>
     * The {@code SET NULL} action sets the value of the referencing column(s) to {@code NULL}
     * when the referenced entity is deleted or updated. This behavior is used to maintain
     * referential integrity by ensuring that dependent rows in the referencing table are
     * not left with invalid references. It assumes that the referencing column(s) allow
     * {@code NULL} values.
     */
    SET_NULL,
    /**
     * Represents the SQL {@code SET DEFAULT} action for foreign key constraints.
     * <p>
     * The {@code SET DEFAULT} action assigns the default value to the referencing column(s)
     * when the referenced entity is deleted or updated. This behavior assumes that the
     * referencing column(s) are configured with a default value. It is used to ensure
     * referential integrity by replacing invalid references with a predetermined default value.
     */
    SET_DEFAULT,
    /**
     * Represents the SQL {@code CASCADE} action for foreign key constraints.
     * <p>
     * The {@code CASCADE} action propagates changes made to a referenced entity to all dependent
     * rows in referencing tables. If the referenced entity is updated, the corresponding foreign
     * key values in the referencing table are also updated. If the referenced entity is deleted,
     * all dependent rows in the referencing table are deleted. This ensures referential integrity
     * by automatically propagating changes to related records.
     */
    CASCADE;

    /**
     * Converts the current enumeration value into its corresponding SQL string representation.
     * This method returns the SQL keyword associated with the enumeration constant.
     *
     * @return A string representing the SQL keyword associated with the enumeration constant.
     * @throws IllegalStateException if the enumeration value is unexpected or unhandled.
     */
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
