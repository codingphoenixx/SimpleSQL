package dev.coph.simplesql.utils;

public class StringEscapeUtils {
    /**
     * Imported from Apache Commons Lang <br>
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
