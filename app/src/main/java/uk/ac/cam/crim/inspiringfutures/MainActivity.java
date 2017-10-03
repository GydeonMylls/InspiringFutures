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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;

import uk.ac.cam.crim.inspiringfutures.ESM.ESM_Questionnaire;
import uk.ac.cam.crim.inspiringfutures.RemoteServer.RemoteConnection;
import uk.ac.cam.crim.inspiringfutures.Services.BootReceiver;
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

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

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

        sPreferences = PreferenceManager.getDefaultSharedPreferences(this);     //getPreferences(MODE_PRIVATE);

        BootReceiver.startServices(this);

        if (sPreferences.contains(KEY_DEVICE_ID)) {
            sDeviceId = sPreferences.getString(KEY_DEVICE_ID, null);
            Log.d(TAG, "Found device ID: " + sDeviceId);
        } else {
            sDeviceId = InstanceID.getInstance(getApplicationContext()).getId();
            Log.d(TAG, "No device ID found, setting ID: " + sDeviceId);
            sPreferences.edit().putString(KEY_DEVICE_ID, sDeviceId).apply();
        }

        if (sPreferences.contains(KEY_PROGRAMME_ID) && sPreferences.contains(KEY_DAILY_QUESTIONNAIRE)) {  // TODO Enable
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {     // Switch is not really necessary but here for extensibility
            case R.id.menu_item_settings:
                // TODO Create preferences activity

                Dialog licenceDialog = new Dialog(this);
                licenceDialog.setTitle("Licence");

                ViewGroup.LayoutParams wrapParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                TextView textView = new TextView(this);
                textView.setText(
                        "Copyright 2017 Gideon Mills\n" +
                        " \n" +
                        " Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                        " you may not use this file except in compliance with the License.\n" +
                        " You may obtain a copy of the License at\n" +
                        " \n" +
                        "     http://www.apache.org/licenses/LICENSE-2.0\n" +
                        " \n" +
                        " Unless required by applicable law or agreed to in writing, software\n" +
                        " distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                        " WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                        " See the License for the specific language governing permissions and\n" +
                        " limitations under the License."
                );

                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setLayoutParams( wrapParams );
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setGravity(Gravity.CENTER);
                int padding = (int) (20 * getResources().getDisplayMetrics().density + 0.5f);       // setPadding takes px so convert from dp
                linearLayout.setPadding(padding, padding, padding, padding);
                linearLayout.addView(textView, -1, wrapParams);

                ScrollView scrollView = new ScrollView(this);
                scrollView.setLayoutParams(wrapParams);
                scrollView.addView(linearLayout);

                licenceDialog.setContentView(scrollView);
                licenceDialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
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

        /**
         * Wrapper to enable toasting from AsyncTask
         * @param values    First argument will be displayed as a toast
         */
        @Override
        protected void onProgressUpdate(String... values) {
            for (String text : values) {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
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

                Log.d(TAG, "Found programmes :" + Arrays.toString(programmes)); // TODO print programmes <- does this not print correctly?
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

        /**
         * Wrapper to enable toasting from AsyncTask
         * @param values    First argument will be displayed as a toast
         */
        @Override
        protected void onProgressUpdate(String... values) {
            for (String text : values) {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            LoadingDialog loadingDialog = new LoadingDialog().setText( getString(R.string.fetching_questionnaire) );
            loadingDialog.show(getSupportFragmentManager(), LoadingDialog.TAG);

            try {
                RemoteConnection remoteConnection = new RemoteConnection(getString(R.string.server_address) + getString(R.string.server_daily_subdirectory) + "/");
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



}