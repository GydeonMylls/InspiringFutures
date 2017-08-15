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

package uk.ac.cam.gsm31.inspiringfutures;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;

/**
 * <p> Created by  Gideon Mills on 24/07/2017 for InspiringFutures. </p>
 */

public class ProgrammePicker extends DialogFragment {

    public static final String TAG = "programme_picker";

    private String[] mCourses;
    private RadioGroup mRadioGroup;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        setRetainInstance(true);

        try {
            String coursesString = getString(R.string.programmes);      // TODO Non-priority: Get courses from remote
            JSONArray coursesJSON = new JSONArray(coursesString);
            mCourses = new String[ coursesJSON.length() ];
            for (int i=0; i<coursesJSON.length(); i++) {
                mCourses[i] = (String) coursesJSON.get(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Arrays.sort(mCourses, String.CASE_INSENSITIVE_ORDER);

        View view = getActivity().getLayoutInflater().inflate(R.layout.programme_picker, null);

        mRadioGroup = view.findViewById(R.id.programme_picker_radios);

        for (int i = 0; i < mCourses.length; i++) {
            RadioButton option = new RadioButton(getActivity());
            option.setId(i);
            option.setText(mCourses[i]);
            mRadioGroup.addView(option);
        }

        final Dialog dialog = new AlertDialog.Builder(getActivity())
            .setView(view)
            .setPositiveButton(R.string.dialog_submit, null)    // OnClickListener set by OnShowListener to allow button to be clicked without dismissing dialog
            .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button positiveButton = ( (AlertDialog) dialogInterface ).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mRadioGroup.getCheckedRadioButtonId() != -1) {
                            String course;
                            course = mCourses[ mRadioGroup.getCheckedRadioButtonId() ];
                            getActivity().getPreferences(Context.MODE_PRIVATE)
                                    .edit()
                                    .putString( MainActivity.KEY_PROGRAMME_ID, course )
                                    .apply();
                            dialog.dismiss();
                        } else {
                            // Cannot use app unless participating in a programme
                            Toast.makeText(getActivity(), R.string.programme_picker_cancel, Toast.LENGTH_SHORT).show();
//                            dialog.cancel();
                        }
                    }
                });
            }
        });

        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onCancel(DialogInterface builder) {
        // Cannot use app unless participating in a programme
        Log.d(TAG, "User has declined to select a programme, closing app");
        super.onCancel(builder);
        Toast.makeText(getActivity(), R.string.programme_picker_cancel, Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    @Override
    public void onDismiss(DialogInterface builder) {
        super.onDismiss(builder);
        ( (DialogInterface.OnDismissListener) getActivity() ).onDismiss(builder);
    }

    // TODO Password protection
}