/*
 * Copyright 2017 Gideon Mills
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.cam.gsm31.inspiringfutures.util;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Represents a ContentValues in a JSONArray, allowing conversion to and from strings
 *
 * <p>Insertion and retrieval take linear time so shouldn't be used for large sets</p>
 *
 * Values must be one of byte[], Boolean, Byte, Double, Float, Integer, Long, Short, String
 *
 * <p> Created by  Gideon Mills on 09/08/2017 for InspiringFutures. </p>
 */

public class JSONContentValues extends JSONArray {

    public static final String INVALID_CONTENT_VALUES_TYPE = "Value provided is not valid for ContenValues";

    public JSONContentValues() {
        super();
    }
    public JSONContentValues(String json) throws JSONException {
        super(json);
    }

    public void put(@NonNull String key, @NonNull byte[] value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }
    public void put(@NonNull String key,@NonNull Boolean value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }
    public void put(@NonNull String key,@NonNull Byte value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }
    public void put(@NonNull String key,@NonNull Double value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }
    public void put(@NonNull String key, @NonNull Float value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }
    public void put(@NonNull String key,@NonNull Integer value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }
    public void put(@NonNull String key,@NonNull Long value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }
    public void put(@NonNull String key,@NonNull Short value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }
    public void put(@NonNull String key,@NonNull String value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }

    public Object get(@NonNull String key) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    return ((JSONKeyValuePair) super.get(i)).getValue();
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        return null;
    }
    
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        JSONKeyValuePair kvp;
        for (int i=0; i<super.length(); i++) {
            try {
                kvp = (JSONKeyValuePair) super.get(i);
                try {
                    putContentValues(cv, kvp.getKey(), kvp.getValue());
                } catch (InvalidTypeException e) {
                    // Shouldn't every happen but just in case
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return cv;
    }

    /**
     * Contains methods to represent a ContentValues-compatible key-value pair in a JSON object
     */
    public static class JSONKeyValuePair extends JSONObject {

        public static final String KEY = "KEY";
        public static final String VALUE_TYPE = "VALUE_TYPE";
        public static final String VALUE = "VALUE";

        // byte[], Boolean, Byte, Double, Float, Integer, Long, Short, String
        public JSONKeyValuePair(@NonNull String key,@NonNull byte[] value) {
            super();
            try {
                this.put(KEY, key)
                        .put(VALUE_TYPE, value.getClass().getName())
                        .put(VALUE, Arrays.toString(value));
            } catch (JSONException e) {
                // Should never happen
                e.printStackTrace();
            }
        }
        public JSONKeyValuePair(@NonNull String key,@NonNull Boolean value) {
            super();
            try {
                this.put(KEY, key)
                        .put(VALUE_TYPE, value.getClass().getName())
                        .put(VALUE, value.toString());
            } catch (JSONException e) {
                // Should never happen
                e.printStackTrace();
            }
        }
        public JSONKeyValuePair(@NonNull String key,@NonNull Byte value) {
            super();
            try {
                this.put(KEY, key)
                        .put(VALUE_TYPE, value.getClass().getName())
                        .put(VALUE, value.toString());
            } catch (JSONException e) {
                // Should never happen
                e.printStackTrace();
            }
        }
        public JSONKeyValuePair(@NonNull String key,@NonNull Double value) {
            super();
            try {
                this.put(KEY, key)
                        .put(VALUE_TYPE, value.getClass().getName())
                        .put(VALUE, value.toString());
            } catch (JSONException e) {
                // Should never happen
                e.printStackTrace();
            }
        }
        public JSONKeyValuePair(@NonNull String key,@NonNull Float value) {
            super();
            try {
                this.put(KEY, key)
                        .put(VALUE_TYPE, value.getClass().getName())
                        .put(VALUE, value.toString());
            } catch (JSONException e) {
                // Should never happen
                e.printStackTrace();
            }
        }
        public JSONKeyValuePair(@NonNull String key,@NonNull Integer value) {
            super();
            try {
                this.put(KEY, key)
                        .put(VALUE_TYPE, value.getClass().getName())
                        .put(VALUE, value.toString());
            } catch (JSONException e) {
                // Should never happen
                e.printStackTrace();
            }
        }
        public JSONKeyValuePair(@NonNull String key,@NonNull Long value) {
            super();
            try {
                this.put(KEY, key)
                        .put(VALUE_TYPE, value.getClass().getName())
                        .put(VALUE, value.toString());
            } catch (JSONException e) {
                // Should never happen
                e.printStackTrace();
            }
        }
        public JSONKeyValuePair(@NonNull String key,@NonNull Short value) {
            super();
            try {
                this.put(KEY, key)
                        .put(VALUE_TYPE, value.getClass().getName())
                        .put(VALUE, value.toString());
            } catch (JSONException e) {
                // Should never happen
                e.printStackTrace();
            }
        }
        public JSONKeyValuePair(@NonNull String key,@NonNull String value) {
            super();
            try {
                this.put(KEY, key)
                        .put(VALUE_TYPE, value.getClass().getName())
                        .put(VALUE, value);
            } catch (JSONException e) {
                // Should never happen
                e.printStackTrace();
            }
        }


        public String getKey() throws JSONException {
            return this.getString(KEY);
        }

        public String getValueType() throws JSONException {
            return this.getString(VALUE_TYPE);
        }

        public String getValueString() throws JSONException {
            return this.getString(VALUE);
        }

        public Object getValue() throws JSONException {
            String type = this.getValueType();
            String value = this.getValueString();
            //    byte[], Boolean, Byte, Double, Float, Integer, Long, Short, String
            if (byte[].class.getName() == type) {
                value = value.substring(1,value.length()-1);            // Drop square brackets
                String[] stringArray = value.split(", ");               // Separate items
                byte[] byteArray = new byte[ stringArray.length ];      // New byte[] of equal length
                for (int i=0; i<stringArray.length; i++) {
                    byteArray[i] = Byte.parseByte( stringArray[i] );    // Parse strings as bytes
                }
                return byteArray;
            } else if (Boolean.class.getName() == type) {
                return Boolean.parseBoolean(value);
            } else if (Byte.class.getName() == type) {
                return Byte.parseByte(value);
            } else if (Double.class.getName() == type) {
                return Double.parseDouble(value);
            } else if (Float.class.getName() == type) {
                return Float.parseFloat(value);
            } else if (Integer.class.getName() == type) {
                return Integer.parseInt(value);
            } else if (Long.class.getName() == type) {
                return Long.parseLong(value);
            } else if (Short.class.getName() == type) {
                return Short.parseShort(value);
            } else if (String.class.getName() == type) {
                return value;
            } else {
                throw new JSONException("Unsupported type");
            }
        }



    }

    public static void putJSONContentValues(@NonNull JSONContentValues values,@NonNull String key,@NonNull Object value) {
        if (value instanceof Short) {
            values.put(key, (Short) value);
        } else if (value instanceof Long) {
            values.put(key, (Long) value);
        } else if (value instanceof Double) {
            values.put(key, (Double) value);
        } else if (value instanceof Integer) {
            values.put(key, (Integer) value);
        } else if (value instanceof String) {
            values.put(key, (String) value);
        } else if (value instanceof Boolean) {
            values.put(key, (Boolean) value);
        } else if (value instanceof Float) {
            values.put(key, (Float) value);
        } else if (value instanceof byte[]) {
            values.put(key, (byte[]) value);
        } else if (value instanceof Byte) {
            values.put(key, (Byte) value);
        } else {
            throw new InvalidTypeException(JSONContentValues.INVALID_CONTENT_VALUES_TYPE);
        }
    }

    /**
     * Helper method to insert key-value pairs into a ContentValues object, where the value is of an unknown but valid type.
     *
     * <p>This only exists because ContentValues accepts a limited number of types and no more elegant solution exists</p>
     *
     * @param values    Object into which to insert
     * @param key       Key
     * @param value     Value, must be one of byte[], Boolean, Byte, Double, Float, Integer, Long, Short, String
     */
    public static void putContentValues(@NonNull ContentValues values, @NonNull String key, @NonNull Object value ) {
        if (value instanceof Short) {
            values.put(key, (Short) value);
        } else if (value instanceof Long) {
            values.put(key, (Long) value);
        } else if (value instanceof Double) {
            values.put(key, (Double) value);
        } else if (value instanceof Integer) {
            values.put(key, (Integer) value);
        } else if (value instanceof String) {
            values.put(key, (String) value);
        } else if (value instanceof Boolean) {
            values.put(key, (Boolean) value);
        } else if (value instanceof Float) {
            values.put(key, (Float) value);
        } else if (value instanceof byte[]) {
            values.put(key, (byte[]) value);
        } else if (value instanceof Byte) {
            values.put(key, (Byte) value);
        } else {
            throw new InvalidTypeException(JSONContentValues.INVALID_CONTENT_VALUES_TYPE);
        }
    }

}
