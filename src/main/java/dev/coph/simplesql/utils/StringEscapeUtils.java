package dev.coph.simplesql.utils;

/**
 * Utility class for escaping and unescaping Strings in various formats.
 * This class provides methods to assist in escaping strings for SQL purposes
 * and potentially other formats in the future. It is primarily used to
 * prevent injection vulnerabilities by sanitizing strings.
 */
public class StringEscapeUtils {
    /**
     * Escapes single quotes in a SQL string to prevent SQL injection vulnerabilities.
     * This method replaces each single quote character (') in the input string with
     * two single quote characters ('').
     *
     * @param str the input string that needs SQL escaping; can be null
     * @return the SQL-escaped string, or null if the input string is null
     */
    public static String escapeSql(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("'", "''");
    }
}
