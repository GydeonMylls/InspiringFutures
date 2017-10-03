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
    }

    public static final class FilesTable {
        public static final String NAME = "Files_Table";

        public static final class Columns {
            public static final String FILEPATH = "filepath";
            public static final String TRANSMITTED = "transmitted";
        }
    }
}
