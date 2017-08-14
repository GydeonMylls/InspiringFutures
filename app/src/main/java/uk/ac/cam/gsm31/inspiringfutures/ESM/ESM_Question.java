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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    public static final String KEY_COMPULSORY = "compulsory";
    public static final char COMPULSORY_FLAG = '*';

    protected JSONObject mJSON;

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
    public void onDestroy() {
        Log.d(TAG, "Destroying "+type());
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);       // Hopefully this will get moved to ESM_Question
    }

    /**
     * Getter for question type, adds automatically if not already in JSON.
     *
     * @return Class name of question as a String
     */
    public String type() {
        String t = "";
        try {
            t = mJSON.getString(KEY_ESM_TYPE);
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
            compulsory();
            q = mJSON.getString(KEY_QUESTION);
        } catch (JSONException e) {
            Log.e(TAG, "JSON does not contain question, adding blank string");
            question(q);
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
            i = mJSON.getString(KEY_INSTRUCTIONS);
        } catch (JSONException e) {
            Log.e(TAG, "JSON does not contain instructions, adding default instructions");
            instructions(i);
        }
        return i;
    }

    /**
     * Getter for whether question is compulsory, checking whether question contains '*' and setting if necesssary, defaults to false
     *
     * @return Boolean to denote whether question is compulsory
     */
    public boolean compulsory() {
        boolean c = false;
        try {
            c = mJSON.getBoolean(KEY_COMPULSORY);
        } catch (JSONException e) {
            Log.e(TAG, "JSON does not contain compulsory flag, inferring from question");
            String question = "";
            try {
                question = mJSON.getString(KEY_QUESTION);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            if (question.contains(String.valueOf(COMPULSORY_FLAG))) {
                Log.d(TAG, "Question contains '"+String.valueOf(COMPULSORY_FLAG)+"' denoting that it is compulsory, setting compulsory flag as true");
                try {
                    mJSON.put(KEY_QUESTION, question.replace(String.valueOf(COMPULSORY_FLAG),""));
                } catch (JSONException e1) {
                    // Can't see why this would ever happen
                    e1.printStackTrace();
                }
                c = true;
            } else {
                Log.d(TAG, "Setting compulsory flag to false");
            }
            compulsory(c);
        }
        return c;
    }

    /**
     * Setter for question text.
     *
     * @param question    Text of question
     * @return Updated question object, must be cast back to it's true type
     */
    public ESM_Question question(@NonNull String question) {
        try {
            mJSON.put(KEY_QUESTION, question);
        } catch (JSONException e) {
            // Can't see why this should ever happen
            e.printStackTrace();
        }
        compulsory();
        return this;
    }

    /**
     * Setter for question instructions.
     *
     * @param instructions    Text of instructions
     * @return Updated question object, must be cast back to it's true type
     */
    public ESM_Question instructions(@NonNull String instructions) {
        try {
            mJSON.put(KEY_INSTRUCTIONS, instructions);
        } catch (JSONException e) {
            // Can't see why this should ever happen
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Setter for whether question is compulsory.
     *
     * @param isCompulsory    Boolean to denote whether question is compulsory
     * @return Updated question object, must be cast back to it's true type
     */
    public ESM_Question compulsory(boolean isCompulsory) {
        try {
            mJSON.put(KEY_COMPULSORY, isCompulsory);
        } catch (JSONException e) {
            // Can't see why this should ever happen
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
    public ESM_Question fromJSON(@NonNull JSONObject json) {
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
     * Used if no instructions are provided.
     *
     * @return Default instructions for this question type.
     */
    public abstract String getDefaultInstructions();

    /**
     * Used to inflate view
     * @return ID (as found in R) of layout
     */
    public abstract int getLayoutId();

    /**
     * Returns user response to question
     *
     * @return Response must be one of byte[], Boolean, Byte, Double, Float, Integer, Long, Short, String
     */
    public abstract Object getResponse();

    /**
     * @return Boolean to denote whether question has been answered
     */
    public abstract boolean isAnswered();

//    /**
//     * Inserts user response to question into a ContentValues object as an appropriate type
//     *
//     * @param values    Set into which to insert response
//     * @param key       Key with which to insert
//     * @return      Updated set
//     */
//    public abstract ContentValues insertResponse(ContentValues values, String key);

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
        Class<?> clss = null;
        ESM_Question question = null;
        try {
            clss = Class.forName(type);
        } catch (ClassNotFoundException e) {
            try {
                // Assume it's a class in the same package as this
                clss = Class.forName(ESM_Question.class.getPackage().getName() + type );
            } catch (ClassNotFoundException e1) {
                // This is the only exception that should ever occur, insofar as exceptions should ever occur
                Log.e(TAG, "getESMQuestion: Unknown question type");
                throw new JSONException("JSON is not a known ESM_Question type");
            }
        }
        if (null != clss) {
            try {
                question = (ESM_Question) clss.getConstructors()[0].newInstance();      // Class must have a single accessible constructor that takes no arguments
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
            }
        }
        // Only happens if JSON is a known question type,
        return question;
    }

    @Override
    public String toString() {
        return mJSON.toString();
    }

    // TODO Compulsory questions: * in question
}
