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
import android.util.Log;

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

public abstract class ESM_Question extends Fragment {

    public static final String TAG = "ESM_Question";
    public static final String KEY_ESM_TYPE = "esm_type";
    public static final String KEY_QUESTION = "question";
    public static final String KEY_INSTRUCTIONS = "instructions";

    private JSONObject mJSON;

    public ESM_Question() {
        super();

        mJSON = new JSONObject();
        type();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Creating "+type());
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "Destroying "+type());
        super.onDestroyView();
    }

    /**
     * Getter for question type, adds automatically if not already in JSON.
     *
     * @return Class name of question as a String
     */
    public String type() {
        String t = "";
        try {
            t = (String) mJSON.get(KEY_ESM_TYPE);
        } catch (JSONException e) {
            Log.e(TAG, "JSON does not contain type, adding");
            try {
                t = this.getClass().getName();
                mJSON.put(KEY_ESM_TYPE, t);
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
            q = (String) mJSON.get(KEY_QUESTION);
        } catch (JSONException e) {
            Log.e(TAG, "JSON does not contain KEY_QUESTION, adding blank string");
            try {
                mJSON.put(KEY_QUESTION, q);
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
        String i = getDefaultInstructions();
        try {
            i = (String) mJSON.get(KEY_INSTRUCTIONS);
        } catch (JSONException e) {
            Log.e(TAG, "JSON does not contain instructions, adding default instructions");
            try {
                mJSON.put(KEY_INSTRUCTIONS, i);
            } catch (JSONException e1) {
                // Can never happen
                e1.printStackTrace();
            }
        }
        return i;
    }

    /**
     * Setter for question text, for testing purposes only.
     *
     * @param question    Text of question
     * @return Updated question object, must be cast back to it's true type
     */
    public ESM_Question question(String question) {
        try {
            mJSON.put(KEY_QUESTION, question);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Setter for question instructions, for testing purposes only.
     *
     * @param instructions    Text of instructions
     * @return Updated question object, must be cast back to it's true type
     */
    public ESM_Question instructions(String instructions) {
        try {
            mJSON.put(KEY_INSTRUCTIONS, instructions);
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

    public abstract String getDefaultInstructions();

    public abstract String getResponse();       // TODO Extend to images, video, audio

    /**
     * Creates an ESM question from a JSON, automatically detecting and instantiating the correct question type. Question types are stored as full class names to facilitate this.
     *
     * @param json    JSONObject representation of question
     * @return ESM_Question representation of question
     * @throws JSONException JSONObject is not a recognised question type
     */
    public static ESM_Question getESMQuestion(JSONObject json) throws JSONException {
        String type = (String) json.get(KEY_ESM_TYPE);
        Log.d(TAG, "getESMQuestion: Detected question type " + type);
        ESM_Question question = null;
        try {
            question = (ESM_Question) Class.forName(type).getConstructors()[0].newInstance();       // Class must have a single accessible constructor that takes no arguments, which should be the case for subclasses of Fragment
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
            Log.e(TAG, "getESMQuestion: Unknown question type");
            throw new JSONException("JSON is not a known ESM_Question type");
        }
        // Only happens if JSON is a known question type
        return question;
    }

    @Override
    public String toString() {
        return mJSON.toString();
    }
}
