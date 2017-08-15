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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * <p> Created by  Gideon Mills on 24/07/2017 for InspiringFutures. </p>
 */

public class LocalDatabaseHelper extends SQLiteOpenHelper {

    public LocalDatabaseHelper(Context context) {
        super(context, LocalDatabaseSchema.DATABASE_NAME, null, LocalDatabaseSchema.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create DiaryTable
        String createString = "create table " + LocalDatabaseSchema.ResponsesTable.NAME + "("
                + " _id integer primary key autoincrement, " // TODO Do I want this? Use timestamp as primary key?
                + LocalDatabaseSchema.ResponsesTable.Columns.DEVICE_ID + ", "
                + LocalDatabaseSchema.ResponsesTable.Columns.QUESTIONNAIRE_ID + ", "
                + LocalDatabaseSchema.ResponsesTable.Columns.TIMESTAMP + ", "
                + LocalDatabaseSchema.ResponsesTable.Columns.RESPONSES + ", "
                + LocalDatabaseSchema.ResponsesTable.Columns.TRANSMITTED
                ;
        createString += ")";
        sqLiteDatabase.execSQL(createString);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // TODO Deliberately blank for now, may update later
    }
}
