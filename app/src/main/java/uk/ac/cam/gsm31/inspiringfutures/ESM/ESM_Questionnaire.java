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

package uk.ac.cam.gsm31.inspiringfutures.ESM;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import uk.ac.cam.gsm31.inspiringfutures.LocalDatabase.LocalDatabaseHelper;
import uk.ac.cam.gsm31.inspiringfutures.LocalDatabase.LocalDatabaseSchema;
import uk.ac.cam.gsm31.inspiringfutures.MainActivity;
import uk.ac.cam.gsm31.inspiringfutures.R;
import uk.ac.cam.gsm31.inspiringfutures.util.JSONContentValues;

/**
 * Represents and manages an ordered collection of ESM_QUESTION objects
 *
 * <p> Created by Gideon Mills on 11/07/2017 for InspiringFutures. </p>
 */

public class ESM_Questionnaire extends Fragment { //} implements ESM_Question.ESMQuestionListener {

    public static final String TAG = "ESM_Questionnaire";
    public static final String KEY_QUESTIONNAIRE_ID = "questionnaire_id";
    public static final String KEY_QUESTIONS_ARRAY = "questions_array";

    private String mID;
    private JSONArray mJSONQuestions;
    private List<ESM_Question> mQuestions;
//    private Object[] mResponses;
    private ESM_Question mCurrentQuestion;
    private int mCurrentQuestionIndex;
    private Button mNextButton;
    private Button mBackButton;
    private TextView mQuestionCount;

    private FragmentManager mFragmentManager;

    /**
     * Initialise a questionnaire with the given ID from an array of questions
     * @param ID           Unique identifier for the questionnaire
     * @param questions    Array of questions in JSON representation
     * @return  This object with ID and questions loaded
     * @throws JSONException One or more items in the array do not represent valid questions
     */
    public ESM_Questionnaire create(@NonNull String ID, @NonNull JSONArray questions) throws JSONException {
        mID = ID;
        mJSONQuestions = questions;
        mQuestions = listify(mJSONQuestions);
//        mResponses = new Object[mJSONQuestions.length()];
        mCurrentQuestionIndex = 0;
        return this;
    }

    /**
     * @param questions    Array of questions in JSON representation
     * @return  This object with questions loaded and randomly generated ID
     * @throws JSONException One or more items in the array does not represent a valid question
     */
    public ESM_Questionnaire create(@NonNull JSONArray questions) throws JSONException {
        return create(UUID.randomUUID().toString(), questions);
    }

    /**
     * @param json    JSON containing ID and questions under appropriate keys
     * @return  This object with questions and ID loaded
     * @throws JSONException Value missing from JSONObject or one or more items in array do not represent valid questions
     */
    public ESM_Questionnaire create(@NonNull JSONObject json) throws JSONException {
        return create( json.getString(KEY_QUESTIONNAIRE_ID), json.getJSONArray(KEY_QUESTIONS_ARRAY) );
    }

    /**
     * @param string    String JSON representation of a questionnaire or an array of questions
     * @return  This object with questions and ID loaded
     * @throws JSONException String does not represent a valid questionnaire or array of questions, one or more values are missing from JSONObject or one or more items in array do not represent valid questions
     */
    public ESM_Questionnaire create(@NonNull String string) throws JSONException {
        try {
            return create( new JSONObject(string) );
        } catch (JSONException e) {
            try {
                return create( new JSONArray(string) );
            } catch (JSONException e1) {
                throw new JSONException(string+" does not represent a valid questionnaire or array of questions");
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Creating questionnaire");
        super.onCreate(savedInstanceState);

        // Retain across configuration changes
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroying questionnaire");
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.esm_questionnaire, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mNextButton = view.findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentQuestion.compulsory()) {
                    if (mCurrentQuestion.isAnswered()) {
                        nextQuestion();
                    } else {
                        Toast.makeText(getActivity(), R.string.esm_compulsory, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    nextQuestion();
                }
            }
        });

        mBackButton = view.findViewById(R.id.previous_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Returning to question " + mCurrentQuestionIndex);
                mCurrentQuestionIndex--;
                if (0 > mCurrentQuestionIndex) {
                    // This should never happen, back button is hidden on first question
                    mCurrentQuestionIndex = 0;
                } else {
                    loadQuestion(mCurrentQuestionIndex);
                }
            }
        });

        mQuestionCount = view.findViewById(R.id.question_count);

        mFragmentManager = getChildFragmentManager();
        if (null == mFragmentManager.findFragmentById(R.id.question_container)) {
            // First time fragment is loaded, start from beginning
            loadQuestion(0);
        }

    }

    /**
     * Converts a JSONArray to a list of ESM_Questions, using ESM_Question.getESMQuestion
     * @param questions    Array of questions
     * @return  List of questions
     * @throws JSONException One of more objects in the array does not represent a valid question
     */
    private List<ESM_Question> listify(JSONArray questions) throws JSONException {
        JSONObject json;
        ESM_Question question;
        List<ESM_Question> questionsList = new ArrayList<ESM_Question>();

        for (int i=0; i< questions.length(); i++) {
            json = (JSONObject) questions.get(i);
            question = ESM_Question.getESMQuestion(json);
            questionsList.add(question);
        }
//        assert questions.length() == mQuestions.size();
        return questionsList;
    }

    /**
     * Displays question with given index
     * @param index Index of question
     */
    private void loadQuestion(int index) {
        mCurrentQuestion = mQuestions.get(index);
        setupNextButton();
        setupPreviousButton();
        setupQuestionCount();
        mFragmentManager.beginTransaction()
                .replace(R.id.question_container, mCurrentQuestion, ESM_Question.TAG)
                .commit();
    }

    /**
     * Set mNextButton's text depending on question number
     */
    private void setupNextButton() {
        if (null != mNextButton) {
            if (mQuestions.size()-1 == mCurrentQuestionIndex) {
                // Last question
                mNextButton.setText(R.string.submit_button);
            } else {
                mNextButton.setText(R.string.next_button);
            }
        }
    }

    /**
     * Set mBackButton's visibility depending on question number
     */
    private void setupPreviousButton() {
        if (null != mBackButton) {
            if (0 == mCurrentQuestionIndex) {
                // First question
                mBackButton.setVisibility(View.INVISIBLE);
            } else {
                mBackButton.setVisibility(View.VISIBLE);
                mBackButton.setText(R.string.back_button);
            }
        }
    }

    /**
     * Set question counter
     */
    private void setupQuestionCount() {
        mQuestionCount.setText( (mCurrentQuestionIndex+1) + "/" + mQuestions.size() );
    }

    private void nextQuestion() {
        Log.d(TAG, "Received response for question " + mCurrentQuestionIndex);
        Toast.makeText(getActivity(), "Response: "+mCurrentQuestion.getResponse().toString(), Toast.LENGTH_SHORT).show();
        mCurrentQuestionIndex++;
        if (mCurrentQuestionIndex < mQuestions.size()) {
            loadQuestion(mCurrentQuestionIndex);
        } else {
            submitResponses();
        }
    }

    /**
     * Commits user responses
     */
    private void submitResponses() {
        Log.d(TAG, "Submitting responses to " + mCurrentQuestionIndex + " questions");

        SQLiteDatabase db = new LocalDatabaseHelper( getContext().getApplicationContext() )
                .getWritableDatabase();
        try {
            db.insert(LocalDatabaseSchema.ResponsesTable.NAME, null, getContentValues());
            Toast.makeText(getActivity(), R.string.submission_toast, Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
            getActivity().finish();
        }
    }

    private ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(LocalDatabaseSchema.ResponsesTable.Columns.DEVICE_ID, MainActivity.getDeviceId() );
        values.put(LocalDatabaseSchema.ResponsesTable.Columns.QUESTIONNAIRE_ID, this.mID);
        values.put(LocalDatabaseSchema.ResponsesTable.Columns.TIMESTAMP, DateFormat.getDateTimeInstance().format( new Date() ) );
        values.put(LocalDatabaseSchema.ResponsesTable.Columns.TRANSMITTED, false);
        values.put(LocalDatabaseSchema.ResponsesTable.Columns.RESPONSES, getResponsesAsString());
        return values;
    }

//    private void putResponses(ContentValues values) {
//        for (int i=0; i<mQuestions.size(); i++) {
//            Object response = mQuestions.get(i).getResponse();
//            if (null != response) {
//                JSONContentValues.putContentValues(values, LocalDatabaseSchema.DiaryTable.COLUMN_NAME+i, response );
//            } else {
//                Log.d(TAG, "getContentValues failed, one or more questions are not fully initialised and returned null to getResponse()");
//                return;
//            }
//        }
//        return;
//    }

    private String getResponsesAsString() {
        JSONContentValues values = new JSONContentValues();
        for (int i=0; i<mQuestions.size(); i++) {
            Object response = mQuestions.get(i).getResponse();
            if (null != response) {
                JSONContentValues.putJSONContentValues(values, "question"+i, response );
            } else {
                Log.d(TAG, "getContentValues failed, one or more questions are not fully initialised and returned null to getResponse()");
                return null;
            }
        }
        return values.toString();
    }

    // TODO URGENT Loses references to buttons on rotation, button text vanishes
}
