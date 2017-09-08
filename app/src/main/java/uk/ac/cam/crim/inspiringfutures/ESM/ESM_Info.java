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

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import uk.ac.cam.crim.inspiringfutures.R;

/**
 * The simplest form of question, straightforward plaintext entry. This is really just a wrapper for ESM_Question that implements the dialog.
 *
 * <p> Created by Gideon Mills on 10/07/2017 for InspiringFutures. </p>
 */

public class ESM_Info extends ESM_Question {

    public static final String TAG = "ESM_Text";
    private static final int LAYOUT_ID = R.layout.esm_info;

    private TextView mTitle;
    private TextView mContent;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTitle = view.findViewById(R.id.esm_title);
        if (!question().isEmpty()) {
            mTitle.setText(question());
        } else {
            mTitle.setVisibility(View.GONE);
        }

        mContent = view.findViewById(R.id.esm_content);
        mContent.setText(
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                        ? Html.fromHtml(instructions(), Html.FROM_HTML_MODE_COMPACT)
                        : Html.fromHtml(instructions())
        );
    }

    @Override
    public String getDefaultInstructions() {
        return "";
    }

    @Override
    public String getResponse() {
        return "";
    }

    @Override
    public boolean isAnswered() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return LAYOUT_ID;
    }

}
