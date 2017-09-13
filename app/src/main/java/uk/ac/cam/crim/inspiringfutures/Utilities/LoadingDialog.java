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

package uk.ac.cam.crim.inspiringfutures.Utilities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * <p> Created by  Gideon Mills on 17/08/2017 for InspiringFutures. </p>
 */
public class LoadingDialog extends DialogFragment {

    public static final String TAG = "LoadingDialog";

    private String mText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        Dialog loadingDialog = new Dialog(getActivity());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        try {
            loadingDialog.getWindow().setGravity(Gravity.CENTER);
            loadingDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        } catch (NullPointerException e) {
            loadingDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setIndeterminate(true);

        TextView textView = new TextView(getActivity());
        if (null != mText) {
            textView.setText(mText);
        }

        ViewGroup.LayoutParams wrapParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setLayoutParams( wrapParams );
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        int padding = (int) (20 * getResources().getDisplayMetrics().density + 0.5f);       // setPadding takes px so convert from dp
        linearLayout.setPadding(padding, padding, padding, padding);

        linearLayout.addView(progressBar, -1, wrapParams);
        linearLayout.addView(textView, -1, wrapParams);

        loadingDialog.setContentView(linearLayout);
        return loadingDialog;
    }

    public LoadingDialog setText(String text) {
        mText = text;
        return this;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        getActivity().finish();
    }
}
