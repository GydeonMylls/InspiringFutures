package uk.ac.cam.gsm31.inspiringfutures.ESM;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.cam.gsm31.inspiringfutures.MainActivity;
import uk.ac.cam.gsm31.inspiringfutures.R;

/**
 * Created by Gideon Mills on 10/07/2017 for InspiringFutures.
 */

@SuppressLint("ValidFragment")
public class ESM_Text extends ESM_Question {

    private static final String TAG = "ESM_Text";

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
