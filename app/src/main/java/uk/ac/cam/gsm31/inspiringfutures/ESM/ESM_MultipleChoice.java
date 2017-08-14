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
import android.view.View;
import android.widget.CompoundButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.cam.gsm31.inspiringfutures.R;

/**
 * Parent class for ESM questions that present options to the user
 *
 * <p> Created by  Gideon Mills on 04/08/2017 for InspiringFutures. </p>
 */

public abstract class ESM_MultipleChoice extends ESM_Question {

    public static final String TAG = "ESM_MultipleChoice";
    public static final String KEY_OPTIONS = "esm_options";
    private static final String KEY_BUNDLE_BUTTONS_TEXT = "buttons_text";

    protected String[] mOptions;        // Only directly access to modify elements
    protected CompoundButton[] mButtons;
    private String[] mButtonsText;

    protected final CompoundButton.OnClickListener mOtherListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if ( !( (CompoundButton) view).isChecked() ) {
                ( (CompoundButton) view).setText( getString(R.string.other) );
            } else {
                new ESM_MultipleChoice_Other().setButton((CompoundButton) view).show(getChildFragmentManager(), TAG);
            }
        }
    };

    protected final CompoundButton.OnClickListener mCompulsoryOtherListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if ( !( (CompoundButton) view).isChecked() ) {
                ( (CompoundButton) view).setText( getString(R.string.other) );
            } else {
                new ESM_MultipleChoice_Other().setButton((CompoundButton) view).setCompulsory(true).show(getChildFragmentManager(), TAG);
            }
        }
    };

    /**
     * Getter for multiple choice options, adds an empty array if no options found.
     * @return Options for multiple choice
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
        if (1 < options.length) {
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
        } else {
            return options(options[0]);
        }
    }

    /**
     * Setter for multiple choice options.
     * @param options    List of options, seperated by the divider string
     * @param divider    String that separates options
     * @return  Updated object
     */
    public ESM_MultipleChoice options(@NonNull String options, @NonNull String divider) {
        String[] opts = options.split(divider);
        for (String s : opts) { s.trim(); }
        return options(opts);
    }

    /**
     * Setter for multiple choice options.
     * @param options    List of options, seperated either by a comma (,) or a return character (\n)
     * @return Updated options
     */
    public ESM_MultipleChoice options(@NonNull String options) {
        if (options.contains("\n")) {
            return options(options, "\n");
        } else {
            return options(options, ", ");
        }
    }

    @Override
    public ESM_Question fromJSON(@NonNull JSONObject json) {
        super.fromJSON(json);
        options();
        return this;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null == mButtonsText) {
            mButtonsText = new String[ mButtons.length ];
        }
        for (int i=0; i<mButtons.length; i++) {
            mButtonsText[i] = mButtons[i].getText().toString();
        }
    }

    /**
     * Restores the user text in Other options
     */
    protected void restoreButtonsText() {
        if ( (null != mButtons) && (null != mButtonsText) ) {
            for (int i=0; i<mButtons.length; i++) {
                mButtons[i].setText( mButtonsText[i] );
            }
        }
    }

}
