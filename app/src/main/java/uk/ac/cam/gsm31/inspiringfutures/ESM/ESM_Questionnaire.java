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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import uk.ac.cam.gsm31.inspiringfutures.LocalDatabase.LocalDatabaseHelper;
import uk.ac.cam.gsm31.inspiringfutures.LocalDatabase.LocalDatabaseSchema;
import uk.ac.cam.gsm31.inspiringfutures.MainActivity;
import uk.ac.cam.gsm31.inspiringfutures.R;
import uk.ac.cam.gsm31.inspiringfutures.util.InvalidTypeException;
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

    private FragmentManager mFragmentManager;

    /**
     * Initialise a questionnaire with the given ID from an array of questions
     * @param ID           Unique identifier for the questionnaire
     * @param questions    Array of questions in JSON representation
     * @return  This object with ID and questions loaded
     * @throws JSONException One or more items in the array do not represent valid questions
     */
    public ESM_Questionnaire create(String ID, JSONArray questions) throws JSONException {
        mID = ID;
        mJSONQuestions = questions;
        mQuestions = listify(mJSONQuestions);
//        mResponses = new Object[mJSONQuestions.length()];
        mCurrentQuestionIndex = 0;
        return this;
    }

    /** Initialise a questionnaire from an array of questions, generating a random id
     *
     * @param questions    Array of questions in JSON representation
     * @return  This object with questions loaded
     * @throws JSONException One or more items in the array does not represent a valid question
     */
    public ESM_Questionnaire create(JSONArray questions) throws JSONException {
        return create(UUID.randomUUID().toString(), questions);
    }

    /**
     * Initialises a questionnaire from an ID and an array of questions stored in a JSONObject
     * @param json    Contains ID and questions under appropriate keys
     * @return  This object with questions and ID loaded
     * @throws JSONException Value missing from JSONObject or one or more items in array do not represent valid questions
     */
    public ESM_Questionnaire create(JSONObject json) throws JSONException {
        return create( json.getString(KEY_QUESTIONNAIRE_ID), json.getJSONArray(KEY_QUESTIONS_ARRAY) );
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
                Log.d(TAG, "Received response for question " + mCurrentQuestionIndex);
                Toast.makeText(getActivity(), "Response: "+mCurrentQuestion.getResponse().toString(), Toast.LENGTH_SHORT).show();
                mCurrentQuestionIndex++;
                if (mCurrentQuestionIndex < mQuestions.size()) {
                    loadQuestion(mCurrentQuestionIndex);
                } else {
                    submitResponses();
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
        mFragmentManager.beginTransaction().replace(R.id.question_container, mCurrentQuestion, ESM_Question.TAG).commit();
    }

    /**
     * Sets mNextButton's text depending on question number
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
     * Commits user responses
     */
    private void submitResponses() {
        Log.d(TAG, "Submitting responses to " + mCurrentQuestionIndex + " questions");

        SQLiteDatabase db = new LocalDatabaseHelper( getContext().getApplicationContext() )
                .getWritableDatabase();
        try{
            if (LocalDatabaseSchema.DiaryTable.NAME == mID) {
                db.insert(LocalDatabaseSchema.DiaryTable.NAME, null, getContentValues() );
            } else{
                // TODO
            }
            Toast.makeText(getActivity(), R.string.submission_toast, Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
            getActivity().finish();
        }
    }

    private ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(LocalDatabaseSchema.DEVICE_ID, ((MainActivity) getActivity()).getDeviceId() );
        values.put(LocalDatabaseSchema.QUESTIONNAIRE_ID, this.mID);
        for (int i=0; i<mQuestions.size(); i++) {
            Object response = mQuestions.get(i).getResponse();
            if (null != response) {
                putContentValues(values, "question"+i, response );
            } else {
                Log.d(TAG, "getContentValues failed, one or more questions are not fully initialised and returned null to getResponse()");
                return null;
            }
        }
        return values;
    }

//    byte[], Boolean, Byte, Double, Float, Integer, Long, Short, String
    private String getResponsesAsString() {
        JSONArray out = new JSONArray();

        // TODO
        return null;
    }

    /**
     * Helper method to insert key-value pairs into a ContentValues object, where the value is of an unknown but valid type.
     *
     * <p>This only exists because ContentValues accepts a limited number of types and no more elegant solution exists</p>
     *
     * @param values    Object into which to insert
     * @param key       Key
     * @param value     Value, must be one of byte[], Boolean, Byte, Double, Float, Integer, Long, Short, String
     */
    private void putContentValues(@NonNull ContentValues values,@NonNull String key,@NonNull Object value ) {
        if (value instanceof Short) {
            values.put(key, (Short) value);
        } else if (value instanceof Long) {
            values.put(key, (Long) value);
        } else if (value instanceof Double) {
            values.put(key, (Double) value);
        } else if (value instanceof Integer) {
            values.put(key, (Integer) value);
        } else if (value instanceof String) {
            values.put(key, (String) value);
        } else if (value instanceof Boolean) {
            values.put(key, (Boolean) value);
        } else if (value instanceof Float) {
            values.put(key, (Float) value);
        } else if (value instanceof byte[]) {
            values.put(key, (byte[]) value);
        } else if (value instanceof Byte) {
            values.put(key, (Byte) value);
        } else {
            throw new InvalidTypeException(JSONContentValues.INVALID_CONTENT_VALUES_TYPE);
        }
    }

    // TODO URGENT Loses references to buttons on rotation, button text vanishes
}
