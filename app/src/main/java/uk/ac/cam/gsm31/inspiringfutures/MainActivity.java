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

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.iid.InstanceID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.cam.gsm31.inspiringfutures.ESM.ESM_Question;
import uk.ac.cam.gsm31.inspiringfutures.ESM.ESM_Questionnaire;

/**
 * <p> Created by Gideon Mills on 11/07/2017 for InspiringFutures. </p>
 */
public class MainActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    public static final String TAG = "MainActivity";
    public static final String KEY_DEVICE_ID = "device_id";
    public static final String KEY_PROGRAMME_ID = "programme_id";
    private static SharedPreferences sPreferences;

    private static JSONArray TEST_QUESTIONNAIRE_JSON;
    private static ESM_Questionnaire TEST_QUESTIONNAIRE;
    private static JSONObject TEST_QUESTION_JSON;
    private static ESM_Question TEST_QUESTION;

    private static String sDeviceId;
    private static String sProgrammeId;
    private Fragment mQuestionContainer;
    private ESM_Questionnaire mQuestionnaire;

    public static String getDeviceId() {
        return sDeviceId;
    }

    public static String getProgrammeId() {
        return sProgrammeId;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sPreferences = getPreferences(MODE_PRIVATE);

        if (false) { //(sPreferences.contains(KEY_DEVICE_ID)) { TODO Uncomment this
            sDeviceId = sPreferences.getString(KEY_DEVICE_ID, null);
            Log.d(TAG, "Found device ID: " + sDeviceId);
            sProgrammeId = sPreferences.getString(KEY_PROGRAMME_ID, null);
            Log.d(TAG, "Found programme ID: " + sProgrammeId);
        } else {
            Log.d(TAG, "No preferences found, initialising");
            SharedPreferences.Editor editor = sPreferences.edit();
            sDeviceId = InstanceID.getInstance(getApplicationContext()).getId();
            Log.d(TAG, "Setting device ID: " + sDeviceId);
            editor.putString(KEY_DEVICE_ID, sDeviceId);
            editor.apply();        // As yet no other preferences to be set

            ProgrammePicker programmePicker = new ProgrammePicker();
            programmePicker.show(getFragmentManager(), ProgrammePicker.TAG);

        }

        try {
            TEST_QUESTION_JSON = new JSONObject().put(ESM_Question.KEY_ESM_TYPE, "uk.ac.cam.gsm31.inspiringfutures.ESM.ESM_Text").put(ESM_Question.KEY_QUESTION, "Is this a question?").put(ESM_Question.KEY_INSTRUCTIONS, "Put some words");
            TEST_QUESTION = ESM_Question.getESMQuestion(TEST_QUESTION_JSON);
            TEST_QUESTIONNAIRE_JSON = new JSONArray()
                    .put( new JSONObject().put(ESM_Question.KEY_ESM_TYPE, "uk.ac.cam.gsm31.inspiringfutures.ESM.ESM_Text").put(ESM_Question.KEY_QUESTION, "Is this a question?").put(ESM_Question.KEY_INSTRUCTIONS, "Put some words") )
                    .put( new JSONObject().put(ESM_Question.KEY_ESM_TYPE, "uk.ac.cam.gsm31.inspiringfutures.ESM.ESM_Text").put(ESM_Question.KEY_QUESTION, "What about this?").put(ESM_Question.KEY_INSTRUCTIONS, "Put some other words") );
            TEST_QUESTIONNAIRE = new ESM_Questionnaire().create("Test",TEST_QUESTIONNAIRE_JSON);
        } catch (JSONException e) {
            Log.e(TAG, "BIG PROBLEM!");
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        mQuestionContainer = fragmentManager.findFragmentById(R.id.questionnaire_container);
        if (null == mQuestionContainer) {
            mQuestionContainer = TEST_QUESTIONNAIRE;
            fragmentManager.beginTransaction().add(R.id.questionnaire_container, mQuestionContainer).commit();
        }

    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        // Set sProgrammeId from newly stored preference
        sProgrammeId = sPreferences.getString(KEY_PROGRAMME_ID, "ERROR");
        Log.d(TAG, "Setting programme ID: " + sProgrammeId);
    }
}

// TODO PRIORITY Proper tagging, extras and logging