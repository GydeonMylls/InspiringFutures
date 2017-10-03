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

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * <p> Created by  Gideon Mills on 25/08/2017 for InspiringFutures. </p>
 */

public class ResponsesCursorWrapper extends CursorWrapper {

    public ResponsesCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public String getDeviceId() {
        return getString( getColumnIndex( LocalDatabaseSchema.ResponsesTable.Columns.DEVICE_ID ) );
    }

    public String getQuestionnaireId() {
        return getString( getColumnIndex( LocalDatabaseSchema.ResponsesTable.Columns.QUESTIONNAIRE_ID ) );
    }

    public String getTimestamp() {
        return getString( getColumnIndex( LocalDatabaseSchema.ResponsesTable.Columns.TIMESTAMP ) );
    }

    public String getResponsesString() {
        return getString( getColumnIndex( LocalDatabaseSchema.ResponsesTable.Columns.RESPONSES ) );
    }

}
