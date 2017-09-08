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

package uk.ac.cam.crim.inspiringfutures.ESM;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import uk.ac.cam.crim.inspiringfutures.R;

/**
 * <p> Created by  Gideon Mills on 04/08/2017 for InspiringFutures. </p>
 */

public class ESM_Radios extends ESM_MultipleChoice {

    public static final String TAG = "ESM_Radios";
    private static final int LAYOUT_ID = R.layout.esm_radios;

    private TextView mQuestion;
    private TextView mInstructions;
    private RadioGroup mRadioGroup;
//    private UncheckableRadioButton[] mRadioButtons;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mQuestion = view.findViewById(R.id.esm_question);
        setCompulsory(getResources(), mQuestion, question());

        mInstructions = view.findViewById(R.id.esm_instructions);
        mInstructions.setText(instructions());

        mButtons = new UncheckableRadioButton[ options().length ];
        mRadioGroup = view.findViewById(R.id.esm_radios);
        for (int i=0; i<options().length; i++) {
            final UncheckableRadioButton option = new UncheckableRadioButton(getActivity());
            mButtons[i] = option;
            option.setId(i);
            option.setText(options()[i]);
            if ( options()[i].equalsIgnoreCase( getString(R.string.other) ) || options()[i].equalsIgnoreCase( String.valueOf(ESM_Question.COMPULSORY_FLAG) + getString(R.string.other) ) ) {
                if (options()[i].equalsIgnoreCase( String.valueOf(ESM_Question.COMPULSORY_FLAG) + getString(R.string.other) )) {
                    option.setOnClickListener( super.mCompulsoryOtherListener );
                    setCompulsory(getResources(), option, options()[i]);
                } else {
                    option.setOnClickListener( super.mOtherListener );
                }
            }
            mRadioGroup.addView(option);
        }
        restoreButtonsText();
    }

    @Override
    public String getDefaultInstructions() {
        return getString(R.string.esm_radios_default_instructions);
    }

    @Override
    public String getResponse() {
        if (null != mRadioGroup){
            if (-1 != mRadioGroup.getCheckedRadioButtonId()) {
                return mButtons[ mRadioGroup.getCheckedRadioButtonId() ].getText().toString();
            } else {
                // No option selected, not a problem
                return "";
            }
        } else {
            // Fragment not fully intialised, shouldn't ever happen
            return null;
        }
    }

    @Override
    public boolean isAnswered() {
        return (null != mRadioGroup) && (-1 != mRadioGroup.getCheckedRadioButtonId());
    }

    @Override
    public int getLayoutId() {
        return LAYOUT_ID;
    }



}
