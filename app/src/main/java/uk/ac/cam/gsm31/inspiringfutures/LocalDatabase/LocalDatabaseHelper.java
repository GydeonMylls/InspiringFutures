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

    private static LocalDatabaseHelper sLocalDatabaseHelper;

    public static final String DATABASE_NAME = "inspiringfutures";
    public static final int DATABASE_VERSION = 1;

    public LocalDatabaseHelper getInstance(Context context) {
        if (null == sLocalDatabaseHelper) {
            sLocalDatabaseHelper = new LocalDatabaseHelper(context);
        }
        return sLocalDatabaseHelper;
    }

    private LocalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + LocalDatabaseSchema.DiaryTable.NAME + "("
                + " _id integer primary key autoincrement, "
                + LocalDatabaseSchema.DiaryTable.Columns.DEVICE_ID + ", "
                + LocalDatabaseSchema.DiaryTable.Columns.QUESTIONNAIRE_ID + ", "
                + LocalDatabaseSchema.DiaryTable.Columns.TIMESTAMP + ", "
                + LocalDatabaseSchema.DiaryTable.Columns.TEXT_QUESTION + ", "
                + LocalDatabaseSchema.DiaryTable.Columns.TRANSMITTED
                + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // TODO Deliberately blank for now, may update later
    }
}
