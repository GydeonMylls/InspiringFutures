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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import uk.ac.cam.gsm31.inspiringfutures.R;

/**
 * <p> Created by  Gideon Mills on 04/08/2017 for InspiringFutures. </p>
 */

public class ESM_CheckBoxes extends ESM_MultipleChoice {

    public static final String TAG = "ESM_CheckBoxes";
    public static final String KEY_MAX_SELECTION = "max_selection";
    private static final int LAYOUT_ID = R.layout.esm_checkboxes;

    private TextView mQuestion;
    private TextView mInstructions;
    private LinearLayout mCheckBoxesGroup;

    private boolean[] mChecked;

    /**
     * @return Maximum number of options that may be submitted
     */
    public int maxSelection() {
        int i = this.options().length;
        try {
            i = (int) mJSON.get(KEY_MAX_SELECTION);
        } catch (JSONException e) {
            Log.e(TAG, "JSON does not contain maximum selection, setting to total number of options");
            try {
                mJSON.put(KEY_INSTRUCTIONS, i);
            } catch (JSONException e1) {
                // Can never happen
                e1.printStackTrace();
            }
        }
        return i;
    }

    /**
     *
     * @param maxSelection Maximum number of options that may be submitted
     * @return Updated question object
     */
    public ESM_CheckBoxes maxSelection(int maxSelection) {
        try {
            mJSON.put(KEY_MAX_SELECTION, maxSelection);
        } catch (JSONException e) {
            // Can't see why this should ever happen
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mQuestion = view.findViewById(R.id.esm_question);
        mQuestion.setText(question());

        mInstructions = view.findViewById(R.id.esm_instructions);
        mInstructions.setText(instructions());

        if (null == mChecked) {
            mChecked = new boolean[mOptions.length];
        }

        mCheckBoxesGroup = view.findViewById(R.id.esm_checkboxes);
        mButtons = new CheckBox[mOptions.length];
        for (int i=0; i<mOptions.length; i++) {
            final CheckBox option = new CheckBox(getActivity());
            option.setText(mOptions[i]);

            // Listener to add to and remove from mChecked as applicable
            final int finalI = i;       // Necessary to access i in listener
            option.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    boolean temp = compoundButton.isChecked();
                    if ( isChecked && (getNumberOfChecked() >= maxSelection() ) ) {
                        Toast.makeText(getActivity(), getString(R.string.esm_checkboxes_limit_message, maxSelection()), Toast.LENGTH_SHORT).show();
                        option.setChecked(false);
                        return;
                    }
                    mChecked[finalI] = isChecked;
                }
            });

            if ( options()[i].equalsIgnoreCase( getString(R.string.other) ) || options()[i].equalsIgnoreCase( String.valueOf(ESM_Question.COMPULSORY_FLAG) + getString(R.string.other) ) ) {
                if (options()[i].equalsIgnoreCase( String.valueOf(ESM_Question.COMPULSORY_FLAG) + getString(R.string.other) )) {
                    option.setOnClickListener( super.mCompulsoryOtherListener );
                    option.setText( getString(R.string.other) );
                } else {
                    option.setOnClickListener( super.mOtherListener );
                }
            }

            mCheckBoxesGroup.addView(option);
            mButtons[i] = option;
        }

        // Restore selection and text
        int maxSelection = maxSelection();
        maxSelection(mOptions.length);
        for (int i=0; i<mButtons.length; i++) {
            mButtons[i].setChecked( mChecked[i] );
        }
        maxSelection(maxSelection);
        restoreButtonsText();
    }

    private int getNumberOfChecked() {
        int out = 0;
        for (boolean b : mChecked) {
            if (b) { out++; }
        }
        return out;
    }

    @Override
    public String getDefaultInstructions() {
        return getString(R.string.esm_checkboxes_default_instructions);
    }

    @Override
    public String getResponse() {
        if (null != mButtons) {
            String out = "";
            for (CompoundButton box : mButtons) {
                if (box.isChecked()) {
                    out += ", " + box.getText();
                }
            }
            if (!out.isEmpty()) out = out.substring(2);     // Remove leading ", "
            return out;
        } else {
            // Fragment not fully intialised, shouldn't ever happen
            return null;
        }
    }

    @Override
    public boolean isAnswered() {
        boolean out = false;
        for (boolean b : mChecked) {
            out = out || b;
        }
        return out;
    }

    @Override
    public int getLayoutId() {
        return LAYOUT_ID;
    }

}
