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

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.iid.InstanceID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import uk.ac.cam.crim.inspiringfutures.ESM.ESM_CheckBoxes;
import uk.ac.cam.crim.inspiringfutures.ESM.ESM_Info;
import uk.ac.cam.crim.inspiringfutures.ESM.ESM_Questionnaire;
import uk.ac.cam.crim.inspiringfutures.ESM.ESM_Radios;
import uk.ac.cam.crim.inspiringfutures.ESM.ESM_Text;
import uk.ac.cam.crim.inspiringfutures.RemoteServer.RemoteConnection;
import uk.ac.cam.crim.inspiringfutures.Utilities.LoadingDialog;

/**
 * <p> Created by Gideon Mills on 11/07/2017 for InspiringFutures. </p>
 */
public class MainActivity extends AppCompatActivity { //implements DialogInterface.OnDismissListener {

    public static final String TAG = "MainActivity";
    public static final String KEY_DEVICE_ID = "device_id";
    public static final String KEY_PROGRAMME_ID = "programme_id";
    public static final String KEY_DAILY_QUESTIONNAIRE = "daily_questionnaire";
    private static SharedPreferences sPreferences;

    private static String sDeviceId;
    private static String sProgrammeId;
    private static String sDailyQuestionnaireString;
    private Fragment mQuestionContainer;        // Separate to facilitate loading of non-daily questionnaires
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

        if (sPreferences.contains(KEY_DEVICE_ID)) {
            sDeviceId = sPreferences.getString(KEY_DEVICE_ID, null);
            Log.d(TAG, "Found device ID: " + sDeviceId);
        } else {
            sDeviceId = InstanceID.getInstance(getApplicationContext()).getId();
            Log.d(TAG, "No device ID found, setting ID: " + sDeviceId);
            sPreferences.edit().putString(KEY_DEVICE_ID, sDeviceId).apply();
        }

        if (sPreferences.contains(KEY_PROGRAMME_ID) && sPreferences.contains(KEY_DAILY_QUESTIONNAIRE)) {       // TODO Uncomment this
            sProgrammeId = sPreferences.getString(KEY_PROGRAMME_ID, null);
            Log.d(TAG, "Found programme ID: " + sProgrammeId);
            sDailyQuestionnaireString = sPreferences.getString(KEY_DAILY_QUESTIONNAIRE, null);
            Log.d(TAG, "Found daily questionnaire: " + sDailyQuestionnaireString);

            try {
                mQuestionnaire = new ESM_Questionnaire().create(sDailyQuestionnaireString);
                loadQuestionnaire();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Log.d(TAG, "No programme ID found, displaying selection dialog");
            new ProgrammePickerTask().execute();
        }

    }

    private void loadQuestionnaire() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        mQuestionContainer = fragmentManager.findFragmentById(R.id.questionnaire_container);
        if (null == mQuestionContainer) {
            mQuestionContainer = mQuestionnaire;
            fragmentManager.beginTransaction().add(R.id.questionnaire_container, mQuestionContainer).commit();
        }
    }


    public class ProgrammePickerTask extends AsyncTask<Void,String,Void> {
        public static final String TAG = "ProgrammePickerTask";

        @Override
        protected void onProgressUpdate(String... values) {
            try {
                Toast.makeText(MainActivity.this, values[0], Toast.LENGTH_SHORT).show();      // TODO
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            LoadingDialog loadingDialog = new LoadingDialog().setText( getString(R.string.programme_picker_loading) );
            loadingDialog.show(getSupportFragmentManager(), LoadingDialog.TAG);

            try {
                RemoteConnection remoteConnection = new RemoteConnection(getString(R.string.server_address));
                String programmesFileString = remoteConnection.getFileText( getString(R.string.programmes_filename) );
                String[] fileLines = programmesFileString.split("\n");
                String[] programmes = new String[ fileLines.length ];
                String[] hashes = new String[ fileLines.length ];
                for (int i=0; i<fileLines.length; i++) {
                    String[] lineArr = fileLines[i].split(":");
                    programmes[i] = lineArr[0];
                    hashes[i] = lineArr[1];
                }

                Log.d(TAG, "Found programmes :" + Arrays.toString(programmes)); // TODO print programmes
                ProgrammePicker programmePicker = new ProgrammePicker().setCourses(programmes).setHashes(hashes);
                programmePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        // Cannot use app unless participating in a programme
                        Log.d(TAG, "User has declined to select a programme, closing app");
                        publishProgress( getString(R.string.programme_picker_cancel_message) );
                        MainActivity.this.finish();
                    }
                });
                programmePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        sProgrammeId = sPreferences.getString(KEY_PROGRAMME_ID, "ERROR");
                        Log.d(TAG, "Setting programme ID: " + sProgrammeId);

                        new DailyQuestionnaireUpdaterTask().execute();
                    }
                });
                programmePicker.show(getSupportFragmentManager(), ProgrammePicker.TAG);

            } catch (IOException e) {
                e.printStackTrace();
                publishProgress( getString(R.string.programme_picker_ioexception) );
                MainActivity.this.finish();
            } finally {
                loadingDialog.dismiss();
            }

            return null;
        }

    }

    public class DailyQuestionnaireUpdaterTask extends AsyncTask<Void,String,Void> {
        public static final String TAG = "DailyUpdater";

        @Override
        protected void onProgressUpdate(String... values) {
            try {
                Toast.makeText(MainActivity.this, values[0], Toast.LENGTH_SHORT).show();      // TODO
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            LoadingDialog loadingDialog = new LoadingDialog().setText( getString(R.string.fetching_questionnaire) );
            loadingDialog.show(getSupportFragmentManager(), LoadingDialog.TAG);

            try {
                RemoteConnection remoteConnection = new RemoteConnection(getString(R.string.server_address));
                sDailyQuestionnaireString = remoteConnection.getFileText( sProgrammeId.replace(' ','_') + ".json");
                Log.d(TAG, "Setting daily questionnaire string: " + sDailyQuestionnaireString);
                sPreferences.edit().putString(KEY_DAILY_QUESTIONNAIRE, sDailyQuestionnaireString).apply();
                // Load daily questionnaire if no other is loaded
                if (null == mQuestionnaire) {
                    mQuestionnaire = new ESM_Questionnaire().create(sDailyQuestionnaireString);
                }
                loadQuestionnaire();
            } catch (IOException e) {
                e.printStackTrace();
                publishProgress( getString(R.string.programme_picker_ioexception) );
                MainActivity.this.finish();
            } catch (JSONException e) {
                // Shouldn't happen provided questionnaires are well-formed
                e.printStackTrace();
            } finally {
                loadingDialog.dismiss();
            }

            return null;
        }
    }

    private String getTestQuestionnaireString(int indent) {
        String out = null;
        try {
            ESM_Info INFO_QUESTION = (ESM_Info) new ESM_Info()
                    .question("Test info title")
                    .instructions("<p>You can put HTML text here</p>\n" +
                            "<h1>Heading 1</h1>\n" +
                            "<h1>Heading 2</h1>\n" +
                            "<h3>Heading 3</h3>\n" +
                            "<h4>Heading 4</h4>\n" +
                            "<p><strong>Bold</strong>, <em>italic</em> and <span style=\"text-decoration: underline;\">underlined</span> text</p>\n" +
                            "<p><span style=\"color: #ff0000;\">Red</span>, <span style=\"color: #00ff00;\">green</span> and <span style=\"color: #0000ff;\">blue</span> text</p>\n" +
                            "<p>And</p>\n" +
                            "<p>what</p>\n" +
                            "<p>should</p>\n" +
                            "<p>happen</p>\n" +
                            "<p>if</p>\n" +
                            "<p>the</p>\n" +
                            "<p>text</p>\n" +
                            "<p>is</p>\n" +
                            "<p>sufficiently</p>\n" +
                            "<p>long</p>\n" +
                            "<p>that</p>\n" +
                            "<p>it</p>\n" +
                            "<p>goes</p>\n" +
                            "<p>off</p>\n" +
                            "<p>the</p>\n" +
                            "<p>screen?</p>\n" +
                            "<p>Why</p>\n" +
                            "<p>this</p>\n" +
                            "<p>of</p>\n" +
                            "<p>course</p>\n" +
                            "<p>you</p>\n" +
                            "<p>silly</p>\n" +
                            "<p>billy.</p>\n" +
                            "<p>Isn't</p>\n" +
                            "<p>this</p>\n" +
                            "<p>great‽</p>");
            ESM_Text TEXT_QUESTION = (ESM_Text) new ESM_Text()
                    .question("Test text question") // Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question Test text question")
                    .instructions("Test text instructions");
            ESM_Radios RADIO_QUESTION = (ESM_Radios) new ESM_Radios()
                    .options( new String[] {"Option 1","Option 2","Option 3","Option 4","Option 5","Option 6","Option 7","Option 8","Option 9","*Other"} )
                    .question("*Test radio question")
                    .instructions("Test radio instructions");
            ESM_CheckBoxes CHECKS_QUESTION = (ESM_CheckBoxes) new ESM_CheckBoxes()
                    .maxSelection(3)
                    .options( new String[] {"Option 1","Option 2","Option 3","Option 4","Option 5","Option 6","Option 7","Option 8","Option 9","Other"} )
                    .question("Test checkboxes question")
                    .instructions("Test checkboxes instructions");
            JSONArray TEST_QUESTIONS_JSON = new JSONArray()
                    .put(INFO_QUESTION.toJSON() )
                    .put( TEXT_QUESTION.toJSON() )
                    .put( RADIO_QUESTION.toJSON() )
                    .put( CHECKS_QUESTION.toJSON() )
                    ;
            JSONObject TEST_QUESTIONNAIRE_JSON = new JSONObject().put(ESM_Questionnaire.KEY_QUESTIONNAIRE_ID, "diary_test").put(ESM_Questionnaire.KEY_QUESTIONS_ARRAY, TEST_QUESTIONS_JSON);
            if (indent>0) {
                out = TEST_QUESTIONNAIRE_JSON.toString(indent);
            } else {
                out = TEST_QUESTIONNAIRE_JSON.toString();
            }

//            mQuestionnaire = new ESM_Questionnaire().create(TEST_STRING);

//            String CLEAN_BREAK_STRING = TEMP_CLEAN_BREAK_ENROLMENT.getString(4);
//            TEST_QUESTIONNAIRE = new ESM_Questionnaire().create(CLEAN_BREAK_STRING);

//            String TEST_QUESTIONNAIRE_STRING = TEST_QUESTIONNAIRE_JSON.toString(4);
//            TEST_QUESTIONNAIRE = new ESM_Questionnaire().create( getString(R.string.diary_test) );

//            new ProgrammePickerTask().execute();

        } catch (JSONException e) {
            throw new RuntimeException("Problems with JSONs", e);
        } finally {
            return out;
        }
    }

}