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

package uk.ac.cam.crim.inspiringfutures;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Arrays;

import uk.ac.cam.crim.inspiringfutures.Utilities.SHA256Hasher;

/**
 * <p> Created by  Gideon Mills on 24/07/2017 for InspiringFutures. </p>
 */

public class ProgrammePicker extends DialogFragment {

    public static final String TAG = "programme_picker";

    private Dialog mDialog;
    private DialogInterface.OnCancelListener mOnCancelListener;
    private DialogInterface.OnDismissListener mOnDismissListener;

    private String[] mCourses;
    private String[] mHashes;
    private RadioGroup mRadioGroup;
    private EditText mPassword;

    /**
     * Setter for courses.
     * @param courses    Array of course names
     * @return Updated object
     */
    public ProgrammePicker setCourses(@NonNull String[] courses) {
        mCourses = courses;
        return this;
    }

    /**
     * Setter for courses.
     * @param courses    List of options, seperated by the divider string
     * @param divider    String that separates options
     * @return Updated object
     */
    public ProgrammePicker setCourses(@NonNull String courses, @NonNull String divider) {
        mCourses = courses.split(divider);
        for (String s : mCourses) { s.trim(); }
        return this;
    }

    /**
     * Setter for courses.
     * @param courses    List of options, seperated either by a comma (,) or a return character (\n)
     * @return Updated object
     */
    public ProgrammePicker setCourses(@NonNull String courses) {
        if (courses.contains("\n")) {
            return setCourses(courses, "\n");
        } else {
            return setCourses(courses, ", ");
        }
    }

    /**
     * Setter for hashes.
     * @param hashes    Array of password hashes
     * @return Updated object
     */
    public ProgrammePicker setHashes(@NonNull String[] hashes) {
        mHashes = hashes;
        return this;
    }

    /**
     * Setter for password hashes.
     * @param hashes    List of password hashes, seperated by the divider string
     * @param divider    String that separates options
     * @return Updated object
     */
    public ProgrammePicker setHashes(@NonNull String hashes, @NonNull String divider) {
        mHashes = hashes.split(divider);
        for (String s : mHashes) { s.trim(); }
        return this;
    }

    /**
     * Setter for password hashes.
     * @param hashes    List of password hashes, seperated either by a comma (,) or a return character (\n)
     * @return Updated object
     */
    public ProgrammePicker setHashess(@NonNull String hashes) {
        if (hashes.contains("\n")) {
            return setCourses(hashes, "\n");
        } else {
            return setCourses(hashes, ", ");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        setRetainInstance(true);

        Arrays.sort(mCourses, String.CASE_INSENSITIVE_ORDER);

        View view = getActivity().getLayoutInflater().inflate(R.layout.programme_picker, null);
        mRadioGroup = view.findViewById(R.id.programme_picker_radios);
        mPassword = view.findViewById(R.id.programme_picker_password);

        for (int i = 0; i < mCourses.length; i++) {
            RadioButton option = new RadioButton(getActivity());
            option.setId(i);
            option.setText(mCourses[i]);
            mRadioGroup.addView(option);
        }

        mDialog = new AlertDialog.Builder(getActivity())
            .setView(view)
            .setPositiveButton(android.R.string.ok, null)    // OnClickListener set by OnShowListener to allow button to be clicked without dismissing dialog
            .create();

        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = ( (AlertDialog) dialogInterface ).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mRadioGroup.getCheckedRadioButtonId() != -1) {
                            int courseId = mRadioGroup.getCheckedRadioButtonId();
                            String course = mCourses[ courseId ];
                            String password = mPassword.getText().toString();
                            String passwordHash = SHA256Hasher.sha256Hash(password);

                            if (passwordHash.equalsIgnoreCase( mHashes[courseId] )) {
//                                getActivity().getPreferences(Context.MODE_PRIVATE)
                                PreferenceManager.getDefaultSharedPreferences(getActivity())
                                        .edit()
                                        .putString( MainActivity.KEY_PROGRAMME_ID, course )
                                        .apply();
                                mDialog.dismiss();
                            } else {
                                mPassword.setText("");
                                Toast.makeText(getActivity(), R.string.programme_picker_wrong_password, Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // Cannot use app unless participating in a programme
                            Toast.makeText(getActivity(), R.string.programme_picker_cancel_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mDialog.setCanceledOnTouchOutside(false);
        mDialog.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );
        return mDialog;
    }

    @Override
    public void onCancel(DialogInterface builder) {
        super.onCancel(builder);
        if (null != mOnCancelListener) {
            mOnCancelListener.onCancel(mDialog);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (null != mOnDismissListener) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mOnDismissListener = listener;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        mOnCancelListener = listener;
    }

    // ProgrammePickerTask and DailQuestionnaireUpdaterTask are in MainActivity, primarily to facilitate access to static fields and resources

}