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


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import uk.ac.cam.crim.inspiringfutures.R;

/**
 * <p> Created by gsm31 on 10/08/2017 for InspiringFutures. </p>
 */

public class ESM_MultipleChoice_Other extends DialogFragment {

    public static final String TAG = "other_dialog";
    public static final String DIVIDER = ": ";

    private CompoundButton mButton;
    private boolean mCompulsory = false;

    /**
     * @param mButton    CompoundButton with which this dialog is associated
     * @return Updated object
     */
    public ESM_MultipleChoice_Other setButton(@NonNull CompoundButton mButton) {
        this.mButton = mButton;
        return this;
    }

    public ESM_MultipleChoice_Other setCompulsory(boolean isCompulsory) {
        this.mCompulsory = isCompulsory;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        setRetainInstance(true);

        View view = getActivity().getLayoutInflater().inflate(R.layout.esm_other_dialog, null);

        final Dialog dialog;

        if (null != mButton) {
            final EditText mResponse = view.findViewById(R.id.esm_other);
            mResponse.setHint(R.string.other_prompt);
            mResponse.setText(
                    ( mButton.getText().toString().equalsIgnoreCase( getString(R.string.other) ) || mButton.getText().toString().equalsIgnoreCase( String.valueOf(ESM_Question.COMPULSORY_FLAG) + getString(R.string.other) ) ) ?
                            "" :
                            mButton.getText().toString().substring(
                                    1 + getString(R.string.other).length() + DIVIDER.length()
                            )
            );
            mResponse.requestFocus();

            dialog = new AlertDialog.Builder(getActivity())
                    .setView(view)
                    .setTitle(getString(R.string.other))
                    .setPositiveButton(android.R.string.ok, null)       // Listener set in OnShowListener to allow dialog to be kept open on button click
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (mCompulsory) {
                                        mButton.toggle();
                                    }
                                }
                            }
                    )
                    .create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button positiveButton = ( (AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String response = mResponse.getText().toString();
                                    if (mCompulsory) {
                                        if (response.isEmpty()) {
                                            Toast.makeText(getActivity(), R.string.esm_compulsory, Toast.LENGTH_SHORT).show();
                                        } else {
                                            mButton.setText(getString(R.string.other) + DIVIDER + response);
                                            dialog.dismiss();
                                        }
                                    } else {
                                        if (!response.isEmpty()) {
                                            mButton.setText(getString(R.string.other) + DIVIDER + response);
                                        }
                                        dialog.dismiss();
                                    }
                                }
                            }
                    );
                }
            });

            dialog.setCanceledOnTouchOutside(false);

        } else {
            dialog = new AlertDialog.Builder(getActivity())
                    .setView(view)
                    .create();
        }
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (null != mButton) {
            mButton.setChecked(false);
        }
    }
}
