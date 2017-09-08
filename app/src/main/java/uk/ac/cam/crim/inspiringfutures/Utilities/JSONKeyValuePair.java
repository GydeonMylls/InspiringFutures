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

package uk.ac.cam.crim.inspiringfutures.Utilities;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Contains methods to represent a ContentValues-compatible key-value pair in a JSON object
 */
public class JSONKeyValuePair extends JSONObject {

    public static final String KEY = "KEY";
    public static final String VALUE_TYPE = "VALUE_TYPE";
    public static final String VALUE = "VALUE";

    public JSONKeyValuePair() { super(); }
    public JSONKeyValuePair(String string) throws JSONException { super(string); }

    // byte[], Boolean, Byte, Double, Float, Integer, Long, Short, String
    public JSONKeyValuePair(@NonNull String key, @NonNull byte[] value) {
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
    public JSONKeyValuePair(@NonNull String key, @NonNull Boolean value) {
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
    public JSONKeyValuePair(@NonNull String key, @NonNull Byte value) {
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
    public JSONKeyValuePair(@NonNull String key, @NonNull Double value) {
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
    public JSONKeyValuePair(@NonNull String key, @NonNull Float value) {
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
    public JSONKeyValuePair(@NonNull String key, @NonNull Integer value) {
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
    public JSONKeyValuePair(@NonNull String key, @NonNull Long value) {
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
    public JSONKeyValuePair(@NonNull String key, @NonNull Short value) {
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
    public JSONKeyValuePair(@NonNull String key, @NonNull String value) {
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
