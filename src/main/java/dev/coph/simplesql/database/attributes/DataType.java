package dev.coph.simplesql.database.attributes;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents a data type that can be used in database schema definitions or SQL queries.
 * The `DataType` class defines various common database column types, each with specific
 * characteristics such as length limitations, supported ranges, or formatting rules.
 * <p>
 * The class supports both fixed and variable length data types, numeric types,
 * date/time types, and specific character types.
 *
 * @param canHaveObject Indicates whether this DataType instance can associate with an object value.
 *                      <p>
 *                      This field determines if the corresponding data type is capable of handling
 *                      objects as valid values, which might influence how data is represented or
 *                      processed in SQL-related operations.
 * @param requireObject Represents whether a {@code DataType} strictly requires an associated object value.
 *                      <p>
 *                      This field determines if it is mandatory for a data type to be paired with a non-null object
 *                      when performing operations such as validation or SQL generation.
 *                      <p>
 *                      For example, certain data types like {@code VARCHAR} or {@code CHAR} might require an object value
 *                      for processing, whereas others like {@code BOOLEAN} or {@code INTEGER} might not.
 * @param name          Represents the name of the data type. This is a mandatory identifier for a
 *                      specific data type used in SQL-related operations or database interactions.
 *                      The value of this field is immutable once assigned.
 */
public record DataType(boolean canHaveObject, boolean requireObject, boolean canBeUnsigned, String name) {
    /**
     * A FIXED length string (can contain letters, numbers, and special characters). The size parameter specifies the column length in characters - can be from 0 to 255. Default is 1
     */
    public static final DataType CHAR = new DataType(true, false, false, "CHAR");

    /**
     * A VARIABLE length string (can contain letters, numbers, and special characters). The size parameter specifies the maximum string length in characters - can be from 0 to 65535
     */
    public static final DataType VARCHAR = new DataType(true, true, false, "VARCHAR");

    /**
     * Holds a string with a maximum length of 255 characters
     */
    public static final DataType TINYTEXT = new DataType(false, false, false, "TINYTEXT");

    /**
     * Holds a string with a maximum length of 16.777.215 characters
     */
    public static final DataType MEDIUMTEXT = new DataType(false, false, false, "MEDIUMTEXT");

    /**
     * Holds a string with a maximum length of 4.294.967.295 characters
     */
    public static final DataType LONGTEXT = new DataType(false, false, false, "LONGTEXT");

    /**
     * 0 is considered as false, 1 is considered as true.
     */
    public static final DataType BOOLEAN = new DataType(false, false, false, "BOOLEAN");

    /**
     * A very small integer. Signed range is from -128 to 127. Unsigned range is from 0 to 255. The size parameter specifies the maximum display width (which is 255)
     */
    public static final DataType TINYINT = new DataType(true, false, true, "TINYINT");

    /**
     * A small integer. Signed range is from -32768 to 32767. Unsigned range is from 0 to 65535. The size parameter specifies the maximum display width (which is 255)
     */
    public static final DataType SMALLINT = new DataType(true, false, true, "SMALLINT");

    /**
     * A medium integer. Signed range is from -2147483648 to 2147483647. Unsigned range is from 0 to 4294967295. The size parameter specifies the maximum display width (which is 255)
     */
    public static final DataType INTEGER = new DataType(true, false, true, "INTEGER");

    /**
     * A large integer. Signed range is from -9223372036854775808 to 9223372036854775807. Unsigned range is from 0 to 18446744073709551615. The size parameter specifies the maximum display width (which is 255)
     */
    public static final DataType BIGINT = new DataType(true, false, true, "BIGINT");

    /**
     * A floating point number. MySQL uses the object value to determine whether to use FLOAT or DOUBLE for the resulting data type. If object is from 0 to 24, the data type becomes FLOAT(). If object is from 25 to 53, the data type becomes DOUBLE()
     */
    public static final DataType FLOAT = new DataType(true, false, true, "FLOAT");

    /**
     * A normal-size floating point number. Parameters are not available
     */
    public static final DataType DOUBLE = new DataType(false, false, true, "DOUBLE");

    /**
     * A date. Format: YYYY-MM-DD. The supported range is from '1000-01-01' to '9999-12-31'
     */
    public static final DataType DATE = new DataType(false, false, false, "DATE");

    /**
     * A date and time combination. Format: YYYY-MM-DD hh:mm:ss. The supported range is from '1000-01-01 00:00:00' to '9999-12-31 23:59:59'.
     */
    public static final DataType DATETIME = new DataType(false, false, false, "DATETIME");

    /**
     * A timestamp. TIMESTAMP values are stored as the number of seconds since the Unix epoch ('1970-01-01 00:00:00' UTC). Format: YYYY-MM-DD hh:mm:ss. The supported range is from '1970-01-01 00:00:01' UTC to '2038-01-09 03:14:07' UTC.
     */
    public static final DataType TIMESTAMP = new DataType(false, false, false, "TIMESTAMP");

    /**
     * A time. Format: hh:mm:ss. The supported range is from '-838:59:59' to '838:59:59'
     */
    public static final DataType TIME = new DataType(false, false, false, "TIME");

    /**
     * Represents the ENUM SQL data type in a database schema.
     * This data type allows a predefined set of string values,
     * from which a column can store one. Useful for defining
     * constrained categorical data.
     * <p>
     * The attributes of this data type are:
     * - Can have an associated object.
     * - Requires an associated object.
     * - Cannot be unsigned.
     * - Has the name "ENUM".
     */
    public static final DataType ENUM = new DataType(true, true, false, "ENUM");

    /**
     * Represents a data type in a SQL schema. This class defines various attributes and behavior
     * for different SQL data types, allowing for handling SQL-compatible operations and definitions.
     *
     * @param canHaveObject Specifies whether the data type can have an associated object.
     * @param requireObject Specifies whether the data type requires an associated object.
     * @param canBeUnsigned Specifies whether the data type can hold unsigned values.
     * @param name          The name of the SQL data type.
     */
    public DataType {
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Converts the current data type representation to its corresponding SQL definition.
     * This method generates a SQL-compatible StringBuilder representation for the data type
     * based on the provided value and unsigned state.
     *
     * @param value    The value to be used in the SQL definition, relevant for ENUM types or
     *                 data types that can have an associated object.
     * @param unsigned The unsigned state of the data type. Determines if the data type
     *                 should include an "UNSIGNED" modifier in its SQL representation.
     * @return A StringBuilder object representing the SQL definition of the data type.
     * If the type is ENUM, the SQL definition includes the enum values. For other
     * types, the definition may include the value or unsigned modifier based on
     * the type's characteristics.
     * @throws IllegalArgumentException If the data type is ENUM and the provided value is null.
     */
    public StringBuilder toSQL(Object value, UnsignedState unsigned) {
        if (name.equals("ENUM")) {
            if (value == null) {
                throw new IllegalArgumentException("ENUM data type requires a non-null value");
            }
            return new StringBuilder()
                    .append(name)
                    .append("(")
                    .append(
                            Arrays.stream(value.toString().split(";"))
                                    .map(String::trim)
                                    .filter(s -> !s.isEmpty())
                                    .map(s -> "'" + s.replace("'", "''") + "'")
                                    .collect(Collectors.joining(", "))
                    ).append(")");
        }
        String unsignedSuffix = unsigned != null && unsigned.equals(UnsignedState.ACTIVE) && canBeUnsigned ? " UNSIGNED" : "";
        return new StringBuilder()
                .append(name)
                .append(canHaveObject() && value != (requireObject ? 0 : null) ? "(" + value + ")" + unsignedSuffix : unsignedSuffix);
    }


    /**
     * Determines whether this DataType instance can have an associated object.
     *
     * @return true if the DataType can have an associated object; false otherwise.
     */
    @Override
    public boolean canHaveObject() {
        return this.canHaveObject;
    }

    /**
     * Determines whether this DataType instance requires an associated object.
     *
     * @return true if the DataType requires an associated object; false otherwise.
     */
    @Override
    public boolean requireObject() {
        return this.requireObject;
    }

    /**
     * Retrieves the name associated with this DataType instance.
     *
     * @return The name of the DataType as a string.
     */
    @Override
    public String name() {
        return this.name;
    }
}
