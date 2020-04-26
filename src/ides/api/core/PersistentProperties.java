package ides.api.core;

import java.util.Properties;

/**
 * A convenience wrapper of {@link java.util.Properties}. It provides methods to
 * get and set properties of types other than String. However, the underlying
 * implementation uses strings to store the actual properties.
 * 
 * @author Lenko Grigorov
 */
@SuppressWarnings("serial")
public class PersistentProperties extends Properties {

    /**
     * Searches for the property with the specified key in this property list and
     * parses the string value as a boolean. If the key is not found in this
     * property list, the default property list, and its defaults, recursively, are
     * then checked. The method returns <code>false</code> if the property is not
     * found.
     * 
     * @param key the property key
     * @return the value in this property list with the specified key value.
     * @see #getBoolean(String, boolean)
     * @see #setBoolean(String, boolean)
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * Searches for the property with the specified key in this property list and
     * parses the string value as a boolean. If the key is not found in this
     * property list, the default property list, and its defaults, recursively, are
     * then checked. The method returns the default value argument if the property
     * is not found.
     * 
     * @param key          the property key
     * @param defaultValue a default value
     * @return the value in this property list with the specified key value.
     * @see #setBoolean(String, boolean)
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getProperty(key, String.valueOf(defaultValue)));
    }

    /**
     * Calls the Hashtable method put. Provided for parallelism with the getProperty
     * method. Enforces use of strings for property keys. Boolean values are
     * properly formatted and stored as strings. The value returned is the result of
     * the Hashtable call to put.
     * 
     * @param key   the key to be placed into this property list
     * @param value the value corresponding to key
     * @return the previous value of the specified key in this property list, or
     *         <code>null</code> if it did not have one.
     * @see #getBoolean(String)
     * @see #getBoolean(String, boolean)
     */
    public synchronized Object setBoolean(String key, boolean value) {
        Object o = getProperty(key);
        setProperty(key, String.valueOf(value));
        return o;
    }

    /**
     * Searches for the property with the specified key in this property list and
     * parses the string value as an integer. If the key is not found in this
     * property list, the default property list, and its defaults, recursively, are
     * then checked. The method returns <code>0</code> if the property is not found.
     * 
     * @param key the property key
     * @return the value in this property list with the specified key value.
     * @see #getInt(String, int)
     * @see #setInt(String, int)
     */
    public int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * Searches for the property with the specified key in this property list and
     * parses the string value as an integer. If the key is not found in this
     * property list, the default property list, and its defaults, recursively, are
     * then checked. The method returns the default value argument if the property
     * is not found.
     * 
     * @param key          the property key
     * @param defaultValue a default value
     * @return the value in this property list with the specified key value.
     * @see #setInt(String, int)
     */
    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Calls the Hashtable method put. Provided for parallelism with the getProperty
     * method. Enforces use of strings for property keys. Integer values are
     * properly formatted and stored as strings. The value returned is the result of
     * the Hashtable call to put.
     * 
     * @param key   the key to be placed into this property list
     * @param value the value corresponding to key
     * @return the previous value of the specified key in this property list, or
     *         <code>null</code> if it did not have one.
     * @see #getInt(String)
     * @see #getInt(String, int)
     */
    public synchronized Object setInt(String key, int value) {
        Object o = getProperty(key);
        setProperty(key, String.valueOf(value));
        return o;
    }
}
