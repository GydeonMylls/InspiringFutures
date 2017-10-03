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

package uk.ac.cam.crim.inspiringfutures.ESM;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import uk.ac.cam.crim.inspiringfutures.R;

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
    public static final char COMPULSORY_FLAG = '*';

    protected JSONObject mJSON;

    public ESM_Question() {
        super();

        mJSON = new JSONObject();
        type();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // Class.getCanonicalName() is used rather than type() as onCreate() is called before the question JSON is inserted, so type() constructs the type every time
        Log.d(TAG, "Creating "+this.getClass().getCanonicalName());
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroying "+this.getClass().getCanonicalName());
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
            Log.d(TAG, "JSON does not contain type, adding");
            try {
                t = this.getClass().getCanonicalName();
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
//            compulsory();
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
        String i = "";
        try {
            i = mJSON.getString(KEY_INSTRUCTIONS);
        } catch (JSONException e) {
            Log.e(TAG, "JSON does not contain instructions, adding blank string");
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
        if (question().isEmpty()) {
            return false;
        } else {
            return (question().charAt(0) == COMPULSORY_FLAG);
        }
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
//        compulsory();
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
        if (isCompulsory) {
            if (!compulsory()) {
                question( String.valueOf(COMPULSORY_FLAG) + question() );
            }
        } else {
            if (compulsory()) {
                question( question().substring(1) );
            }
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
     * Used to inflate view
     * @return ID (as found in R) of layout
     */
    public abstract int getLayoutId();

    /**
     * Returns user response to question
     *
     * @return Response must be serializable to a String and have a constructor taking a String that reconstructs the object
     */
    public abstract Serializable getResponse();

    /**
     * @return Boolean to denote whether question has been answered
     */
    public abstract boolean isAnswered();

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
            question = (ESM_Question) clss.getConstructor().newInstance();      // Class must have an accessible constructor that takes no arguments
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
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "getESMQuestion: Question constructor does not exist");
            e.printStackTrace();
        }
        // Only happens if JSON is a known question type, returns null if other exceptions occur
        return question;
    }

    /**
     * Static method to set colour of the compulsory flag in question text. Call this to set question text.
     * @param resources    Android resources containing the compulsory flag colour
     * @param textview     Question holder
     * @param text         Text to display in textview
     */
    public static void setCompulsory(Resources resources, TextView textview, String text) {
        if (text.charAt(0) == COMPULSORY_FLAG) {
            textview.setText(text, TextView.BufferType.SPANNABLE);
            Spannable spannable = (Spannable) textview.getText();
            spannable.setSpan(
                    new ForegroundColorSpan( ResourcesCompat.getColor(resources, R.color.compulsory, null) ),
                    0,
                    1,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        } else {
            textview.setText(text);
        }
    }

    @Override
    public String toString() {
        return mJSON.toString();
    }

}
