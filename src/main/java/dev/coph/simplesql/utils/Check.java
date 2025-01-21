package dev.coph.simplesql.utils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Check {



    /**
     * Checks if object is null
     *
     * @param object Object that will be checked
     * @param name   Name of the object for the error message
     */
    public static void ifNull(Object object, String name) {
        if (object == null)
            throw new NullPointerException("Object '" + name + "' is null");
    }

    /**
     * Checks if string only has allowed characters
     * Default Regex: A-Z; a-z; 0-9; _
     *
     * @param input Object that will be checked
     * @param name   Name of the object for the error message
     */
    public static void ifStringOnlyHasAllowedCharacters(String input, String name) {
        if (input != null && !input.matches("^[A-Za-z0-9_]+$"))
            throw new IllegalArgumentException("Object '" + name + "' has forbidden characters.");
    }

    /**
     * Checks if string only has allowed characters
     *
     * @param input Object that will be checked
     * @param regex The regex for the check.
     * @param name   Name of the object for the error message
     */
    public static void ifStringOnlyHasAllowedCharacters(String input, String regex, String name) {
        if (input != null && input.matches(regex))
            throw new IllegalArgumentException("Object '" + name + "' has forbidden characters.");
    }

    /**
     * Checks if object has spaces
     *
     * @param object Object that will be checked
     * @param name   Name of the object for the error message
     */
    public static void ifHasSpaces(Object object, String name) {
        if (object instanceof String string && string.contains(" "))
            throw new NullPointerException("Object '" + name + "' has spaces");
    }

    /**
     * Checks if object is not a number
     *
     * @param object Object that will be checked
     * @param name   Name of the object for the error message
     */
    public static void ifNotNumber(Object object, String name) {
        if (!(object instanceof Number))
            throw new NullPointerException("Object '" + name + "' is not a number");
    }

    /**
     * Checks if object is 'null' or if it`s a map it`s empty
     *
     * @param object Object that will be checked
     * @param name   Name of the object for the error message
     */
    public static void ifNullOrEmptyMap(Object object, String name) {
        if (object == null) {
            throw new NullPointerException("Object '" + name + "' is null");
        } else if (object instanceof Set<?> set && set.isEmpty()) {
            throw new NullPointerException("Set '" + name + "' is empty");
        } else if (object instanceof List<?> list && list.isEmpty()) {
            throw new NullPointerException("List '" + name + "' is empty");
        } else if (object instanceof Map<?, ?> map && map.isEmpty()) {
            throw new NullPointerException("Map '" + name + "' is empty");
        }
    }

    /**
     * Checks if the provided file object is null or if the file does not exist on the filesystem.
     *
     * @param file The file to be checked.
     * @param name The name of the file object for use in the error message.
     * @throws NullPointerException if the file object is null.
     * @throws IllegalArgumentException if the file does not exist.
     */
    public static void ifNullOrNotExits(File file, String name) {
        if (file == null)
            throw new NullPointerException("Object '" + name + "' is null");
        if(!file.exists())
            throw new IllegalArgumentException("File '" + name + "' does not exist");
    }

    /**
     * Check if an Integer is at min value
     *
     * @param integer Integer that will be checked
     * @param name    Name of the object for the error message
     */
    public static void ifIntMinValue(int integer, String name) {
        if (integer == Integer.MIN_VALUE)
            throw new NullPointerException("Integer '" + name + "' was not set.");
    }
}
