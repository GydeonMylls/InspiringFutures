/*
 * Copyright 2017 Gideon Mills.
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * <p> Created by Gideon Mills on 11/07/2017 for InspiringFutures. </p>
 */

public class ESM_Questionnaire {

    private List<ESM_Question> mQuestions;
    private JSONArray mResponses;

    private ESM_Question.ESMQuestionListener mListener = new ESM_Question.ESMQuestionListener() {
        @Override
        public void receiveResponse(String response) {
            // TODO
            mResponses.put(response);
        }

        @Override
        public void receiveCancel() {
            // TODO
        }
    };

    public ESM_Questionnaire(JSONArray questions) throws JSONException {
        if (null == questions) throw new JSONException("No JSONArray provided");        // TODO Consistency in checking for null references


    }
}
