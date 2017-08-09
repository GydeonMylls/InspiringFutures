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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.ac.cam.gsm31.inspiringfutures.R;

/**
 * <p> Created by  Gideon Mills on 04/08/2017 for InspiringFutures. </p>
 */

public class ESM_CheckBoxes extends ESM_MultipleChoice {

    public static final String TAG = "ESM_CheckBoxes";
    private static final String DEFAULT_INSTRUCTIONS = "Select one or more";

    private TextView mQuestion;
    private TextView mInstructions;
    private LinearLayout mCheckBoxesGroup;
    private CheckBox[] mCheckBoxesArray;

    private boolean[] mChecked;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.esm_checkboxes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mQuestion = view.findViewById(R.id.esm_question);
        mQuestion.setText(question());

        mInstructions = view.findViewById(R.id.esm_instructions);
        mInstructions.setText(instructions());

        mChecked = new boolean[mOptions.length];

        mCheckBoxesGroup = view.findViewById(R.id.esm_checkboxes);
        mCheckBoxesArray = new CheckBox[mOptions.length];
        for (int i=0; i<mOptions.length; i++) {
            CheckBox option = new CheckBox(getActivity());
            option.setText(mOptions[i]);
            // Listener to add to and remove from mChecked as applicable
            final int finalI = i;       // Necessary to access i in listener
            option.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    //  assert !isChecked;
                    mChecked[finalI] = isChecked;
                }
            });

            mCheckBoxesGroup.addView(option);
            mCheckBoxesArray[i] = option;
        }

    }

    @Override
    public String getDefaultInstructions() {
        return DEFAULT_INSTRUCTIONS;
    }

    @Override
    public String getResponse() {
        if (null != mCheckBoxesArray) {
            String out = "";
            for (CheckBox box : mCheckBoxesArray) {
                if (box.isChecked()) {
                    out += ", " + box.getText();
                }
            }
            out = out.substring(2);     // Remove leading ", "
            return out;
        } else {
            // Fragment not fully intialised, shouldn't ever happen
            return null;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
