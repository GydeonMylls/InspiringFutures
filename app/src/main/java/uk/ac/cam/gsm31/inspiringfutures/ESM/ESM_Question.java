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

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Represents a general ESM question, with fields common to all question types
 *
 * Created by Gideon Mills on 07/07/2017 for InspiringFutures.
 */

public abstract class ESM_Question extends DialogFragment {

    private static final String TAG = "ESM_Question";

    private JSONObject mJSON;

    public static final String esm_type = "esm_type";
    public static final String question = "question";
    public static final String instructions = "instructions";
    public static final String isLast = "isLast";

    private ESMQuestionListener mListener;

    public String type() {
        String t = "";
        try {
            t = (String) mJSON.get(esm_type);
        } catch (JSONException e) {
            Log.e(TAG, "JSON does not contain type, adding");
            try {
                t = this.getClass().getName();
                mJSON.put(esm_type, t);
            } catch (JSONException e1) {
                // Can never happen
                e1.printStackTrace();
            }
        } finally {
            return t;
        }
    }

    public String question() {
        String q = "";
        try {
            q = (String) mJSON.get(question);
        } catch (JSONException e) {
            Log.e(TAG, "JSON does not contain question, adding blank string");
            try {
                mJSON.put(question, q);
            } catch (JSONException e1) {
                // Can never happen
                e1.printStackTrace();
            }
        } finally {
            return q;
        }
    }

    public String instructions() {
        String i = "";
        try {
            i = (String) mJSON.get(instructions);
        } catch (JSONException e) {
            Log.e(TAG, "JSON does not contain instructions, adding blank string");
            try {
                mJSON.put(instructions, i);
            } catch (JSONException e1) {
                // Can never happen
                e1.printStackTrace();
            }
        } finally {
            return i;
        }
    }

    public boolean isLast() {
        boolean l = false;
        try {
            l = mJSON.getBoolean(isLast);
        } catch (JSONException e) {
            Log.e(TAG, "JSON does not contain isLast value, adding false");
            try {
                mJSON.put(isLast, l);
            } catch (JSONException e1) {
                // Can never happen
                e1.printStackTrace();
            }
        } finally {
            return l;
        }
    }

//    public ESM_Question question(String q) {
//        try {
//            mJSON.put(question, q);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } finally {
//            return this;
//        }
//    }

//    public ESM_Question instructions(String i) {
//        try {
//            mJSON.put(instructions, i);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } finally {
//            return this;
//        }
//    }

//    public ESM_Question isLast(boolean l) {
//        try {
//            mJSON.put(isLast, l);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } finally {
//            return this;
//        }
//    }

    public ESM_Question fromJSON(JSONObject json) {
        mJSON = json;
        return this;
    }

    public JSONObject toJSON() {
        return mJSON;
    }

    protected View getViewByName(String layoutName) {
        return getActivity().getLayoutInflater().inflate( getResources().getIdentifier(layoutName, "layout", getActivity().getPackageName() ) , null);
    }

    @Override
    public abstract Dialog onCreateDialog(Bundle savedInstanceState);

    public interface ESMQuestionListener {
        // TODO Listener interface
        public void receiveResponse(String response);
        public void receiveCancel();
    }

    public ESM_Question setListener(ESMQuestionListener listener) {
        // Can only have one listener
        if (mListener == null) { mListener = listener; } return this;
    }

    public ESMQuestionListener getListener() {
        return mListener;
    }

    public static ESM_Question getESMQuestion(JSONObject json) throws JSONException {
        String type = (String) json.get(esm_type);
        Log.d(TAG, "getESMQuestion: Detected question type" + type);
        ESM_Question question = null;
        try {
            question = (ESM_Question) Class.forName(type).getConstructors()[0].newInstance();       // Class must have a single accessible contructor that takes no arguments
            question.fromJSON(json);
        } catch (java.lang.InstantiationException e) {
            Log.e(TAG, "getESMQuestion: Question type refers to an abstract class");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(TAG, "getESMQuestion: Question constructor cannot be accessed");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e(TAG, "getESMQuestion: Question contructor threw exception");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "getESMQuestion: Unknown question type");
            throw new JSONException("JSON is not a known ESM_Question type");
        }
        // Only happens if JSON is a known question type
        return question;
    }

}
