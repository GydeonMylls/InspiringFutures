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

package uk.ac.cam.crim.inspiringfutures.LocalDatabase;

/**
 * <p> Created by  Gideon Mills on 26/07/2017 for InspiringFutures. </p>
 */

public class LocalDatabaseSchema {

    // TODO Do I need column names?

    public static final String DATABASE_NAME = "inspiringfutures.db";
    public static final int DATABASE_VERSION = 1;

    public static final class ResponsesTable {
        public static final String NAME = "Responses_Table";

        public static final class Columns {
            public static final String DEVICE_ID = "device_id";
            public static final String QUESTIONNAIRE_ID = "questionnaire_id";
            public static final String TIMESTAMP = "timestamp";
            public static final String TRANSMITTED = "transmitted";
            public static final String RESPONSES = "responses";
        }

//    public static final class DiaryTable {
//        public static final String NAME = "diary_test";
//        public static final int NUMBER_OF_QUESTIONS = 2;
//        public static final String COLUMN_NAME = "question";
////        public static final class Columns {
////            public static final String TEXT_QUESTION = "question1";
////            public static final String RADIO_QUESTION = "question2";
////            public static final String CHECKBOXES_QUESTION = "question3";
////            public static final String FACE_QUESTION = "question4";
////            public static final String SLIDER_QUESTION = "question5";
////            public static final String STARS_QUESTION = "question6";
////            public static final String PHOTO_QUESTION = "question7";
////            public static final String VIDEO_QUESTION = "question8";
////            public static final String AUDIO_QUESTION = "question9";
////        }
//
//    }
//
//    public static final class OthersTable {
//        public static final String NAME = "others_test";
//        public static final String COLUMNS = "question";
    }
}
