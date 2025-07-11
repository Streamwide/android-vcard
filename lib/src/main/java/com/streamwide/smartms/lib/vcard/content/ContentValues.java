/*
 *
 * 	StreamWIDE (Team on The Run)
 *
 * @createdBy  AndroidTeam on Thu, 16 May 2024 09:41:20 +0100
 * @copyright  Copyright (c) 2024 StreamWIDE UK Ltd (Team on the Run)
 * @email      support@teamontherun.com
 *
 * 	© Copyright 2024 StreamWIDE UK Ltd (Team on the Run). StreamWIDE is the copyright holder
 * 	of all code contained in this file. Do not redistribute or
 *  	re-use without permission.
 *
 * @lastModifiedOn Thu, 2 May 2024 20:52:37 +0100
 */

package com.streamwide.smartms.lib.vcard.content;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.streamwide.smartms.lib.vcard.logger.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to store a set of values that the {@link android.content.ContentResolver}
 * can process.
 */
public final class ContentValues {
    public static final String TAG = "ContentValues";

    /** Holds the actual values */
    private HashMap<String, Object> mValues;

    /**
     * Creates an empty set of values using the default initial size
     */
    public ContentValues() {
        // Choosing a default size of 8 based on analysis of typical
        // consumption by applications.
        mValues = new HashMap<>(8);
    }

    /**
     * Creates an empty set of values using the given initial size
     *
     * @param size the initial size of the set of values
     */
    public ContentValues(int size) {
        mValues = new HashMap<>(size, 1.0f);
    }

    /**
     * Creates a set of values copied from the given set
     *
     * @param from the values to copy
     */
    public ContentValues(@NonNull ContentValues from) {
        mValues = new HashMap<>(from.mValues);
    }

    /**
     * Creates a set of values copied from the given HashMap. This is used
     * by the Parcel unmarshalling code.
     *
     * {@hide}
     */
       @Override
    public boolean equals(Object object) {
        if (!(object instanceof ContentValues)) {
            return false;
        }
        return mValues.equals(((ContentValues) object).mValues);
    }

    @Override
    public int hashCode() {
        return mValues.hashCode();
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(@NonNull String key, @Nullable String value) {
        mValues.put(key, value);
    }

    /**
     * Adds all values from the passed in ContentValues.
     *
     * @param other the ContentValues from which to copy
     */
    public void putAll(@NonNull ContentValues other) {
        mValues.putAll(other.mValues);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(@NonNull String key, @Nullable Byte value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(@NonNull String key, @Nullable Short value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(@NonNull String key, @Nullable Integer value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(@NonNull String key, @Nullable Long value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(@NonNull String key, @Nullable Float value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(@NonNull String key, @Nullable Double value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(@NonNull String key, @Nullable Boolean value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(@NonNull String key, @Nullable byte[] value) {
        mValues.put(key, value);
    }

    /**
     * Adds a null value to the set.
     *
     * @param key the name of the value to make null
     */
    public void putNull(@NonNull String key) {
        mValues.put(key, null);
    }

    /**
     * Returns the number of values.
     *
     * @return the number of values
     */
    public int size() {
        return mValues.size();
    }

    /**
     * Remove a single value.
     *
     * @param key the name of the value to remove
     */
    public void remove(@NonNull String key) {
        mValues.remove(key);
    }

    /**
     * Removes all values.
     */
    public void clear() {
        mValues.clear();
    }

    /**
     * Returns true if this object has the named value.
     *
     * @param key the value to check for
     * @return {@code true} if the value is present, {@code false} otherwise
     */
    public boolean containsKey(@NonNull String key) {
        return mValues.containsKey(key);
    }

    /**
     * Gets a value. Valid value types are {@link String}, {@link Boolean}, and
     * {@link Number} implementations.
     *
     * @param key the value to get
     * @return the data for the value
     */
    public @Nullable Object get(@NonNull String key) {
        return mValues.get(key);
    }

    /**
     * Gets a value and converts it to a String.
     *
     * @param key the value to get
     * @return the String for the value
     */
    public @Nullable String getAsString(@NonNull String key) {
        Object value = mValues.get(key);
        return value != null ? mValues.get(key).toString() : null;
    }

    /**
     * Gets a value and converts it to a Long.
     *
     * @param key the value to get
     * @return the Long value, or null if the value is missing or cannot be converted
     */
    public @Nullable Long getAsLong(@NonNull String key) {
        Object value = mValues.get(key);
        try {
            return value != null ? ((Number) value).longValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Long.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Logger.error(TAG, "Cannot parse Long value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Logger.error(TAG, "Cannot cast value for " + key + " to a Long");
                return null;
            }
        }
    }

    /**
     * Gets a value and converts it to an Integer.
     *
     * @param key the value to get
     * @return the Integer value, or null if the value is missing or cannot be converted
     */
    public @Nullable Integer getAsInteger(@NonNull String key) {
        Object value = mValues.get(key);
        try {
            return value != null ? ((Number) value).intValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Integer.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Logger.error(TAG, "Cannot parse Integer value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Logger.error(TAG, "Cannot cast value for " + key + " to a Integer");
                return null;
            }
        }
    }

    /**
     * Gets a value and converts it to a Short.
     *
     * @param key the value to get
     * @return the Short value, or null if the value is missing or cannot be converted
     */
    public @Nullable Short getAsShort(@NonNull String key) {
        Object value = mValues.get(key);
        try {
            return value != null ? ((Number) value).shortValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Short.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Logger.error(TAG, "Cannot parse Short value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Logger.error(TAG, "Cannot cast value for " + key + " to a Short");
                return null;
            }
        }
    }

    /**
     * Gets a value and converts it to a Byte.
     *
     * @param key the value to get
     * @return the Byte value, or null if the value is missing or cannot be converted
     */
    public @Nullable Byte getAsByte(@NonNull String key) {
        Object value = mValues.get(key);
        try {
            return value != null ? ((Number) value).byteValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Byte.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Logger.error(TAG, "Cannot parse Byte value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Logger.error(TAG, "Cannot cast value for " + key + " to a Byte");
                return null;
            }
        }
    }

    /**
     * Gets a value and converts it to a Double.
     *
     * @param key the value to get
     * @return the Double value, or null if the value is missing or cannot be converted
     */
    public @Nullable Double getAsDouble(@NonNull String key) {
        Object value = mValues.get(key);
        try {
            return value != null ? ((Number) value).doubleValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Double.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Logger.error(TAG, "Cannot parse Double value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Logger.error(TAG, "Cannot cast value for " + key + " to a Double");
                return null;
            }
        }
    }

    /**
     * Gets a value and converts it to a Float.
     *
     * @param key the value to get
     * @return the Float value, or null if the value is missing or cannot be converted
     */
    public @Nullable Float getAsFloat(@NonNull String key) {
        Object value = mValues.get(key);
        try {
            return value != null ? ((Number) value).floatValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Float.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Logger.error(TAG, "Cannot parse Float value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Logger.error(TAG, "Cannot cast value for " + key + " to a Float");
                return null;
            }
        }
    }

    /**
     * Gets a value and converts it to a Boolean.
     *
     * @param key the value to get
     * @return the Boolean value, or null if the value is missing or cannot be converted
     */


    /**
     * Gets a value that is a byte array. Note that this method will not convert
     * any other types to byte arrays.
     *
     * @param key the value to get
     * @return the byte[] value, or null is the value is missing or not a byte[]
     */
    public @Nullable byte[] getAsByteArray(@NonNull String key) {
        Object value = mValues.get(key);
        if (value instanceof byte[]) {
            return (byte[]) value;
        } else {
            return null;
        }
    }

    /**
     * Returns a set of all of the keys and values
     *
     * @return a set of all of the keys and values
     */
    public @NonNull Set<Map.Entry<String, Object>> valueSet() {
        return mValues.entrySet();
    }



    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String name : mValues.keySet()) {
            String value = getAsString(name);
            if (sb.length() > 0) sb.append(" ");
            sb.append(name).append("=").append(value);
        }
        return sb.toString();
    }
}
