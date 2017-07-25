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

import android.os.Bundle;
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

import uk.ac.cam.gsm31.inspiringfutures.R;

/**
 * <p> Created by Gideon Mills on 11/07/2017 for InspiringFutures. </p>
 */

public class ESM_Questionnaire extends Fragment { //} implements ESM_Question.ESMQuestionListener {

    public static final String TAG = "ESM_Questionnaire";
    private static final String KEY_QUESTIONNAIRE_ID = "questionnaire_id";
    private static final String KEY_QUESTIONS_ARRAY = "questions_array";

    private String mID;
    private JSONArray mJSONQuestions;
    private List<ESM_Question> mQuestions;
    private JSONArray mResponses;
    private ESM_Question mCurrentQuestion;
    private int mCurrentQuestionIndex;
    private Button mNextButton;
    private Button mBackButton;
//    private View.OnClickListener mNextListener;

    private FragmentManager mFragmentManager;

    /**
     * Initialise a questionnaire with the given ID from an array of questions
     * @param ID           Unique identifier for the questionnaire
     * @param questions    Array of questions in JSON representation
     * @return  This object with ID and questions loaded
     * @throws JSONException One or more items in the array does not represent a valid question
     */
    public ESM_Questionnaire create(String ID, JSONArray questions) throws JSONException {
        mID = ID;
        mJSONQuestions = questions;
        mQuestions = listify(mJSONQuestions);
        mResponses = new JSONArray();
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
     * @throws JSONException Value missing from JSONObject or one or more items in array does not represent a valid question
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
                try {
                    Log.d(TAG, "Received response for question " + mCurrentQuestionIndex);
                    mResponses.put( mCurrentQuestionIndex, mCurrentQuestion.getResponse());
                    mCurrentQuestionIndex++;
                    if (mCurrentQuestionIndex < mQuestions.size()) {
                        loadQuestion(mCurrentQuestionIndex);
                    } else {
                        submitResponses();
                    }
                } catch (JSONException e) {
                    // As far as I can tell this will only occur if mCurrentQuestionIndex is NaN or infinity
                    // This should never happen
                    e.printStackTrace();
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
//            question.setQuestionListener(this);
            questionsList.add(question); // TODO Problem here?
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
        mFragmentManager.beginTransaction().replace(R.id.question_container, mCurrentQuestion, mCurrentQuestion.TAG).commit();
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
        Log.d(TAG, "Submitting responses to" + mCurrentQuestionIndex + " questions");
        Toast.makeText(getActivity(), R.string.submission_toast, Toast.LENGTH_LONG).show();
        // TODO
        getActivity().finish();
    }

}
