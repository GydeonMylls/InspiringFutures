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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

    public static final String esm_layout = "esm_text";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = getViewByName(esm_layout);

        // Can't use dialog title as it cuts off long strings
        TextView esm_question = view.findViewById(R.id.esm_question);
        esm_question.setText(question());

        TextView esm_instructions = view.findViewById(R.id.esm_instructions);
        esm_instructions.setText(instructions());

        final EditText esm_response = view.findViewById(R.id.esm_response);

        Dialog dialog = new AlertDialog.Builder(getActivity())                          // TODO Maybe move dialog creation to ESM_Question
                .setView(view)
                .setPositiveButton(isLast() ? "Submit" : "Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getListener().receiveResponse(esm_response.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getListener().receiveCancel();
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

}
