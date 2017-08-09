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

package uk.ac.cam.gsm31.inspiringfutures.ESM;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Parent class for ESM questions that present options to the user
 *
 * <p> Created by  Gideon Mills on 04/08/2017 for InspiringFutures. </p>
 */

public abstract class ESM_MultipleChoice extends ESM_Question {

    public static final String TAG = "ESM_MultipleChoice";
    public static final String KEY_OPTIONS = "esm_options";

    protected String[] mOptions;

    /**
     * Getter for multiple choice options, adds an empty array if no options found.
     * @return Options for multipl choice
     */
    public String[] options() {
        if (null == mOptions) {
            String[] o = new String[0];
            try {
                JSONArray a = (JSONArray) mJSON.get(KEY_OPTIONS);
                o = new String[a.length()];
                for (int i=0; i<a.length(); i++) {
                    o[i] = a.getString(i);
                }
            } catch (JSONException e) {
                Log.e(TAG, "JSON does not contain options, adding empty array");
                try {
                    mJSON.put(KEY_OPTIONS, new JSONArray());
                } catch (JSONException e1) {
                    // Can never happen
                    e1.printStackTrace();
                }
            }
            mOptions = o;
        }
        return mOptions;
    }

    /**
     * Setter for multiple choice options.
     * @param options    Array of options
     * @return  Updated question object
     */
    public ESM_MultipleChoice options(@NonNull String[] options) {
        try {
            mOptions = options;
            JSONArray a = new JSONArray();
            for (int i=0; i<options.length; i++) {
                a.put(i, options[i]);
            }
            mJSON.put(KEY_OPTIONS, a);
        } catch (JSONException e) {
            // Can't see why this should ever happen
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public ESM_Question fromJSON(@NonNull JSONObject json) {
        super.fromJSON(json);
        options();
        return this;
    }
}
