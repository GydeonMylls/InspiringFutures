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

import java.lang.reflect.InvocationTargetException;

/**
 * Represents a general ESM question with a JSONObject, essentially just a wrapper for the JSON with some helpful methods. This is the base class for all question types and cannot be instantiated itself.
 *
 * <p> The basic structure is that all question values are stored in the JSON and the class holds the relevant keys it fields and manages dialog creation and behaviour.</p>
 *
 * <p> </p>Created by Gideon Mills on 07/07/2017 for InspiringFutures. </p>
 */

public abstract class ESM_Question extends DialogFragment {

    private static final String TAG = "ESM_Question";

    private JSONObject mJSON;

    public static final String ESM_TYPE = "esm_type";
    public static final String QUESTION = "question";
    public static final String INSTRUCTIONS = "instructions";
    public static final String IS_LAST = "IS_LAST";

    private ESMQuestionListener mListener;


    /**
     * Getter for question type, adds automatically if not already in JSON.
     *
     * @return Class name of question as a String
     */
    public String type() {
        String t = "";
        try {
            t = (String) mJSON.get(ESM_TYPE);
        } catch (JSONException e) {
            Log.e(TAG, "JSON does not contain type, adding");
            try {
                t = this.getClass().getName();
                mJSON.put(ESM_TYPE, t);
            } catch (JSONException e1) {
                // Can never happen
                e1.printStackTrace();
            }
        }
        return t;
    }

    /**
     * Getter for question text, adds an empty String if no question found.
     *
     * @return Question text as a String
     */
    public String question() {
        String q = "";
        try {
            q = (String) mJSON.get(QUESTION);
        } catch (JSONException e) {
            Log.e(TAG, "JSON does not contain QUESTION, adding blank string");
            try {
                mJSON.put(QUESTION, q);
            } catch (JSONException e1) {
                // Can never happen
                e1.printStackTrace();
            }
        }
        return q;
    }

    /**
     * Getter for question instructions, adds an empty String if no instructions found.
     *
     * @return Question instructions as a String
     */
    public String instructions() {
        String i = "";
        try {
            i = (String) mJSON.get(INSTRUCTIONS);
        } catch (JSONException e) {
            Log.e(TAG, "JSON does not contain instructions, adding blank string");
            try {
                mJSON.put(INSTRUCTIONS, i);
            } catch (JSONException e1) {
                // Can never happen
                e1.printStackTrace();
            }
        }
        return i;
    }

    /**
     * Getter for a boolean value to denote whether or not this question is the last to be displayed, adds false if no value found. Only real use is to have different button text on the last question dialog.
     *
     * @return True if this is the last question to be displayed, false otherwise
     */
    public boolean isLast() {
        boolean l = false;
        try {
            l = mJSON.getBoolean(IS_LAST);
        } catch (JSONException e) {
            Log.e(TAG, "JSON does not contain IS_LAST value, adding false");
            try {
                mJSON.put(IS_LAST, l);
            } catch (JSONException e1) {
                // Can never happen
                e1.printStackTrace();
            }
        }
        return l;
    }

    /**
     * Setter for question text, for testing purposes only.
     *
     * @param question    Text of question
     * @return Updated question object, allows chaining
     */
    public ESM_Question question(String question) {
        try {
            mJSON.put(QUESTION, question);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Setter for question instructions, for testing purposes only.
     *
     * @param instructions    Text of instructions
     * @return Updated question object, allows chaining
     */
    public ESM_Question instructions(String instructions) {
        try {
            mJSON.put(INSTRUCTIONS, instructions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     *  Setter for boolean value to denote whether this question is the last to be displayed. Due to the getter behaviour this only needs to be set for last question.
     *
     * @param isLast    True if this is the last question to be displayed, false otherwise
     * @return Updated question object, allows chaining
     */
    public ESM_Question isLast(boolean isLast) {
        try {
            mJSON.put(IS_LAST, isLast);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Loads a JSON representation of a question. This method is in lieu of a constructor .
     *
     * @param json    ESM question as a JSONObject
     * @return Updated question object, allows chaining
     */
    public ESM_Question fromJSON(JSONObject json) {
        mJSON = json;
        return this;
    }

    /**
     * Gets question represented as a JSON, for storage and transmission purposes.
     *
     * @return JSONObject of question
     */
    public JSONObject toJSON() {
        return mJSON;
    }

    /**
     * Loads and inflates a layout from it's name, streamlines dialog creation for subclasses.
     *
     * @param layoutName    Name of layout
     * @return Inflated layout as view object
     */
    protected View getViewByName(String layoutName) {
        return getActivity().getLayoutInflater().inflate( getResources().getIdentifier(layoutName, "layout", getActivity().getPackageName() ) , null);
    }

    // Overriding this as abstract forces subclasses to implement it (I think, quite possibly not and this is just pointless)
    @Override
    public abstract Dialog onCreateDialog(Bundle savedInstanceState);

    /**
     * Interface for objects to be notified when a question is answered of the questionnaire canceled.
     */
    public interface ESMQuestionListener {
        // TODO Listener interface

        /**
         * Method called when a question is answered.
         * @param response    User response to question
         */
        void receiveResponse(String response);

        /**
         * Method called when questionnaire is cancelled.
         */
        void receiveCancel();
    }

    /**
     * Register an object to be updated when questions are answered or canceled. A question can only have one listener, successive calls have no effect.
     *
     * @param listener Object implementing the ESMQuestionListener interface
     * @return Updated question object, allows chaining
     */
    public ESM_Question setListener(ESMQuestionListener listener) {
        // Can only have one listener
        if (mListener == null) { mListener = listener; } return this;
    }

    /**
     * @return Object that is notified of question answers, etc.
     */
    public ESMQuestionListener getListener() {
        return mListener;
    }

    /**
     * Creates an ESM question from a JSON, automatically detecting and instantiating the correct question type. Question types are stored as full class names to facilitate this.
     *
     * @param json    JSONObject representation of question
     * @return ESM_Question representation of question
     * @throws JSONException JSONObject is not a recognised question type
     */
    public static ESM_Question getESMQuestion(JSONObject json) throws JSONException {
        String type = (String) json.get(ESM_TYPE);
        Log.d(TAG, "getESMQuestion: Detected QUESTION type" + type);
        ESM_Question question = null;
        try {
            question = (ESM_Question) Class.forName(type).getConstructors()[0].newInstance();       // Class must have a single accessible constructor that takes no arguments
            question.fromJSON(json);
        } catch (java.lang.InstantiationException e) {
            Log.e(TAG, "getESMQuestion: Question type refers to an abstract class");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(TAG, "getESMQuestion: Question constructor cannot be accessed");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e(TAG, "getESMQuestion: Question constructor threw exception");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // This is the only exception that should ever occur, insofar as exceptions should ever occur
            Log.e(TAG, "getESMQuestion: Unknown QUESTION type");
            throw new JSONException("JSON is not a known ESM_Question type");
        }
        // Only happens if JSON is a known QUESTION type
        return question;
    }

}
