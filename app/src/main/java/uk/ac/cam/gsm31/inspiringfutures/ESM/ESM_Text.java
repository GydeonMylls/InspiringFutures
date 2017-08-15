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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import uk.ac.cam.gsm31.inspiringfutures.R;

/**
 * The simplest form of question, straightforward plaintext entry. This is really just a wrapper for ESM_Question that implements the dialog.
 *
 * <p> Created by Gideon Mills on 10/07/2017 for InspiringFutures. </p>
 */

public class ESM_Text extends ESM_Question {

    public static final String TAG = "ESM_Text";
    private static final int LAYOUT_ID = R.layout.esm_text;

    private TextView mQuestion;
    private EditText mResponse;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mQuestion = view.findViewById(R.id.esm_question);
        setCompulsory(getResources(), mQuestion, question());

        mResponse = view.findViewById(R.id.esm_response);
        mResponse.setHint(instructions());

    }

    @Override
    public String getDefaultInstructions() {
        return getString(R.string.esm_text_default_instructions);
    }

    @Override
    public String getResponse() {
        if (null != mResponse) {
            return mResponse.getText().toString();
        } else { return null; }
    }

    @Override
    public boolean isAnswered() {
        return (null != mResponse) && !mResponse.getText().toString().isEmpty();
    }

    @Override
    public int getLayoutId() {
        return LAYOUT_ID;
    }

    // TODO Automatically show and hide keyboard

}
