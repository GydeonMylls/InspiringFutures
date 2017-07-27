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

package uk.ac.cam.gsm31.inspiringfutures.LocalDatabase;

/**
 * <p> Created by  Gideon Mills on 26/07/2017 for InspiringFutures. </p>
 */

public class LocalDatabaseSchema {

    public static final class DiaryTable {
        public static final String NAME = "diary_test";

        public static final class Columns {
            public static final String DEVICE_ID = "device_id";
            public static final String QUESTIONNAIRE_ID = "questionnaire_id";
            public static final String TIMESTAMP = "timestamp";
            public static final String TRANSMITTED = "transmitted";
            public static final String TEXT_QUESTION = "text_question";
            public static final String RADIO_QUESTOIN = "radio_question";
            public static final String CHECKBOXES_QUESTION = "checkboxes_questions";
            public static final String FACE_QUESTION = "faces_question";
            public static final String SLIDER_QUESTION = "slider_question";
            public static final String STARS_QUESTION = "stars_question";
            public static final String PHOTO_QUESTION = "photo_question";
            public static final String VIDEO_QUESTION = "video_question";
            public static final String AUDIO_QUESTION = "audio_question";
        }
    }
}
