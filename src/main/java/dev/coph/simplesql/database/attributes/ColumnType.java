package dev.coph.simplesql.database.attributes;

import dev.coph.simplesql.driver.DriverType;
import dev.coph.simplesql.query.Query;

/**
 * Enum representing the various types of constraints that can be applied to a database column.
 * It is used to define the specific constraints for a column in the database schema.
 */
public enum ColumnType {
    /**
     * Represents the default column type in a database.
     * This is typically used when no specific constraint is applied to the column.
     */
    DEFAULT,
    /**
     * Represents the PRIMARY_KEY constraint of a database column.
     * This ensures that the column has unique values and does not contain null values.
     * Typically used to uniquely identify each record in a table.
     */
    PRIMARY_KEY,
    /**
     * Represents the PRIMARY_KEY constraint with AUTOINCREMENT functionality for a database column.
     * This ensures that the column has unique values, does not contain null values, and its values are
     * automatically incremented by the database whenever a new record is inserted.
     */
    PRIMARY_KEY_AUTOINCREMENT,
    /**
     * Represents the UNIQUE constraint of a database column.
     * This ensures that all values in the column are distinct.
     * The constraint prohibits duplicate entries in the column,
     * but unlike the PRIMARY_KEY constraint, it allows null values.
     */
    UNIQUE;


    /**
     * Converts the enumeration value to its string representation based on the database context provided.
     *
     * @param query the query object that includes information about the database adapter and driver type,
     *              used to determine the specific string representation of the enumeration value.
     * @return the string representation of the enumeration, which may vary depending on the database type
     * or driver adapter provided in the query.
     */
    public String toString(Query query) {
        if (query.databaseAdapter() == null)
            return this.name().replaceAll("_", " ");

        if (query.databaseAdapter().driverType().equals(DriverType.MYSQL) || query.databaseAdapter().driverType().equals(DriverType.MARIADB)) {
            if (this == PRIMARY_KEY_AUTOINCREMENT) {
                return "PRIMARY KEY AUTO_INCREMENT";
            }
        } else if (query.databaseAdapter().driverType().equals(DriverType.SQLITE)) {
            if (this == PRIMARY_KEY_AUTOINCREMENT) {
                return "PRIMARY KEY AUTOINCREMENT";
            }
        }

        return this.name().replaceAll("_", " ");
    }
}
