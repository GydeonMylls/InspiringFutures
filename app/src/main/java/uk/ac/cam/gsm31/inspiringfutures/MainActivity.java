package uk.ac.cam.gsm31.inspiringfutures;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.cam.gsm31.inspiringfutures.ESM.ESM_Question;
import uk.ac.cam.gsm31.inspiringfutures.ESM.ESM_Text;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button mQuestionnaireButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQuestionnaireButton = (Button) findViewById(R.id.questionnaire_button);

        mQuestionnaireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // For testing purposes

                try{
                    JSONObject json = new JSONObject().put(ESM_Question.esm_type, "uk.ac.cam.gsm31.inspiringfutures.ESM.ESM_Text").put(ESM_Question.question, "What if the title is really so very long that it has too many character to fit on the screen?").put(ESM_Question.instructions, "Words").put(ESM_Question.isLast, true);
//                    ESM_Text text = (ESM_Text) new ESM_Text().fromJSON(json);
//                    Log.e(TAG, this.getClass().getName());
                    Log.d(TAG, "Attempting autocreate");
                    ESM_Question text = ESM_Question.getESMQuestion(json);
                    text.show(getFragmentManager(), null);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Arooga!", Toast.LENGTH_LONG);
                }
            }
        });
    }

}

// TODO PRIORITY Proper tagging, extras and logging