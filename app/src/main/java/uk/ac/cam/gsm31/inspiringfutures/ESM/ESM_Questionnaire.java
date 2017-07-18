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

import android.app.FragmentManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * <p> Created by Gideon Mills on 11/07/2017 for InspiringFutures. </p>
 */

public class ESM_Questionnaire implements ESM_Question.ESMQuestionListener {

    public static final String TAG = "ESM_Questionnaire";

    private List<ESM_Question> mQuestions;
    private JSONArray mResponses;
    private FragmentManager mFragmentManager;
    private int mCurrentQuestionIndex;

    public ESM_Questionnaire(JSONArray questions) throws JSONException {
        Log.d(TAG, "Creating questionnaire from JSON array");
        mQuestions = new ArrayList<ESM_Question>();
        mResponses = new JSONArray();
        mCurrentQuestionIndex = 0;

        JSONObject json;
        ESM_Question question;

        for (int i=0; i< questions.length(); i++) {
            json = (JSONObject) questions.get(i);
            question = ESM_Question.getESMQuestion(json);
            question.setListener(this);
            mQuestions.add(question);
        }
//        assert questions.length() == mQuestions.size();
        Log.d(TAG, "Questionnaire successfully created");
    }

    @Override
    public void receiveResponse(String response) {
        Log.d(TAG, "Recieved response for question " + mCurrentQuestionIndex);
        mResponses.put(response);
        mCurrentQuestionIndex++;
        if (mCurrentQuestionIndex < mQuestions.size()) {
            ESM_Question currentQuestion = mQuestions.get(mCurrentQuestionIndex);
            currentQuestion.show(mFragmentManager, currentQuestion.TAG);
        } else {
            submitResponses();
        }
    }

    @Override
    public void receiveCancel() {
        // TODO Delete responses
    }

    public void startQuestionnaire(FragmentManager manager) {
        mFragmentManager = manager;
        Log.d(TAG, "Starting questionnaire");
        ESM_Question firstQuestion = mQuestions.get(0);
        firstQuestion.show(mFragmentManager, firstQuestion.TAG);
    }

    private void submitResponses() {
        Log.d(TAG, "Submitting responses to" + mCurrentQuestionIndex + " questions");
    }

}
