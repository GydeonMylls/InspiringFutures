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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
        String createResponsesTable = "create table " + LocalDatabaseSchema.ResponsesTable.NAME + "( "
                + LocalDatabaseSchema.ResponsesTable.Columns.DEVICE_ID          + " VARCHAR(20)"                                                                + ", "
                + LocalDatabaseSchema.ResponsesTable.Columns.QUESTIONNAIRE_ID                                                                                   + ", "  // Let SQLite choose type
                + LocalDatabaseSchema.ResponsesTable.Columns.TIMESTAMP          + " INTEGER DEFAULT (cast(strftime('%s','now') as int)) NOT NULL PRIMARY KEY"   + ", "
                + LocalDatabaseSchema.ResponsesTable.Columns.RESPONSES                                                                                          + ", "  // Let SQLite choose type
                + LocalDatabaseSchema.ResponsesTable.Columns.TRANSMITTED        + " BOOLEAN"
                + ")"
                ;
//        createResponsesTable += ")";
        sqLiteDatabase.execSQL(createResponsesTable);

        // Create FilesTable
        String createFilesTable = "create table " + LocalDatabaseSchema.FilesTable.NAME + "( "
                + LocalDatabaseSchema.FilesTable.Columns.FILEPATH + " NOT NULL PRIMARY KEY" + ", "     // Let SQLite choose type, no need to upload file multiple times if used in multiple questions
                + LocalDatabaseSchema.FilesTable.Columns.TRANSMITTED    + " BOOLEAN"
                + ")"
                ;
        sqLiteDatabase.execSQL(createFilesTable);
    }

    public static void markResponseAsTransmitted(String timestamp, LocalDatabaseHelper localDatabaseHelper) {
        ContentValues values = new ContentValues();
        values.put(LocalDatabaseSchema.ResponsesTable.Columns.TRANSMITTED, true);
        localDatabaseHelper.getWritableDatabase().update(
                LocalDatabaseSchema.ResponsesTable.NAME,                                                                                            // table
                values,                                                                                                                             // values
                LocalDatabaseSchema.ResponsesTable.Columns.TIMESTAMP + "=? AND " + LocalDatabaseSchema.ResponsesTable.Columns.TRANSMITTED + "=0",   // whereClause
                new String[] {timestamp}                                                                                                            // whereArgs
        );
    }

    public static void markFileAsTransmitted(String filePath, LocalDatabaseHelper localDatabaseHelper) {
        ContentValues values = new ContentValues();
        values.put(LocalDatabaseSchema.ResponsesTable.Columns.TRANSMITTED, true);
        localDatabaseHelper.getWritableDatabase().update(
                LocalDatabaseSchema.FilesTable.NAME,                                                                                        // table
                values,                                                                                                                     // values
                LocalDatabaseSchema.FilesTable.Columns.FILEPATH + "=? AND " + LocalDatabaseSchema.FilesTable.Columns.TRANSMITTED + "=0",    // whereClause
                new String[] {filePath}                                                                                                     // whereArgs
        );
    }

    public static Cursor getUntransmittedResponses(SQLiteDatabase sqLiteDatabase) {
        return sqLiteDatabase.query(
                LocalDatabaseSchema.ResponsesTable.NAME,                                // table
                new String[] {                                                          // columns
                        LocalDatabaseSchema.ResponsesTable.Columns.DEVICE_ID,
                        LocalDatabaseSchema.ResponsesTable.Columns.QUESTIONNAIRE_ID,
                        LocalDatabaseSchema.ResponsesTable.Columns.TIMESTAMP,
                        LocalDatabaseSchema.ResponsesTable.Columns.RESPONSES,
                },
                LocalDatabaseSchema.ResponsesTable.Columns.TRANSMITTED + "=0",          // selection
                null,                                                                   // selectionargs
                null,                                                                   // groupBy
                null,                                                                   // having
                LocalDatabaseSchema.ResponsesTable.Columns.TIMESTAMP + " ASC"           // orderBy
        );
    }

    public static Cursor getUntransmittedFiles(SQLiteDatabase sqLiteDatabase) {
        return sqLiteDatabase.query(
                LocalDatabaseSchema.FilesTable.NAME,                                    // table
                new String[] {                                                          // columns
                        LocalDatabaseSchema.FilesTable.Columns.FILEPATH
                },
                LocalDatabaseSchema.ResponsesTable.Columns.TRANSMITTED + "=0",          // selection
                null,                                                                   // selectionargs
                null,                                                                   // groupBy
                null,                                                                   // having
                null                                                                    // orderBy
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Deliberately blank for now, expecting no significant changes to database schema
    }

}
