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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.cam.gsm31.inspiringfutures.ESM.ESM_Question;
import uk.ac.cam.gsm31.inspiringfutures.ESM.ESM_Questionnaire;

/**
 * <p> Created by Gideon Mills on 11/07/2017 for InspiringFutures. </p>
 */
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
                    JSONArray json = new JSONArray()
                            .put(new JSONObject().put(ESM_Question.ESM_TYPE, "uk.ac.cam.gsm31.inspiringfutures.ESM.ESM_Text").put(ESM_Question.QUESTION, "Is this a question?").put(ESM_Question.INSTRUCTIONS, ""))
                            .put(new JSONObject().put(ESM_Question.ESM_TYPE, "uk.ac.cam.gsm31.inspiringfutures.ESM.ESM_Text").put(ESM_Question.QUESTION, "How about another question?").put(ESM_Question.INSTRUCTIONS, "Put different words please").put(ESM_Question.IS_LAST, true));
                    Log.d(TAG, "Attempting autocreate");
                    ESM_Questionnaire questionnaire = new ESM_Questionnaire(json);
                    questionnaire.startQuestionnaire(getFragmentManager());
//                    JSONObject json = new JSONObject().put(ESM_Question.ESM_TYPE, "uk.ac.cam.gsm31.inspiringfutures.ESM.ESM_Text").put(ESM_Question.QUESTION, "Is this a question?").put(ESM_Question.INSTRUCTIONS, "Put some words");
//                    ESM_Question text = ESM_Question.getESMQuestion(json);
//                    text.setListener(new ESM_Question.ESMQuestionListener() {
//                        @Override
//                        public void receiveResponse(String response) {
//                            Toast.makeText(getApplicationContext(), "Question answered", Toast.LENGTH_LONG).show();
//                        }
//
//                        @Override
//                        public void receiveCancel() {
//
//                        }
//                    });
//                    text.show(getFragmentManager(), text.TAG);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Arooga!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}

// TODO PRIORITY Proper tagging, extras and logging