package uk.ac.cam.gsm31.inspiringfutures.ESM;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created by Gideon Mills on 11/07/2017 for InspiringFutures.
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
