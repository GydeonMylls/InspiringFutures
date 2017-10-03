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

package uk.ac.cam.crim.inspiringfutures.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.Calendar;

import uk.ac.cam.crim.inspiringfutures.LocalDatabase.LocalDatabaseHelper;
import uk.ac.cam.crim.inspiringfutures.LocalDatabase.ResponsesCursorWrapper;
import uk.ac.cam.crim.inspiringfutures.MainActivity;
import uk.ac.cam.crim.inspiringfutures.R;
import uk.ac.cam.crim.inspiringfutures.RemoteServer.RemoteConnection;

/**
 * <p> Created by  Gideon Mills on 11/09/2017 for InspiringFutures-client. </p>
 */

public class SyncService extends IntentService {

    public static final String TAG = "SyncService";

    public static final String KEY_LAST_SYNC = "last_sync";

    public static final int DEFAULT_SYNC_HOUR = 12;
    public static final int DEFAULT_SYNC_MINUTE = 0;

    public static final long SYNC_INTERVAL = 60 * 1000;      //AlarmManager.INTERVAL_DAY;      // TODO Set interval to one day

    public SyncService() {
        super(TAG);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, SyncService.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "Checking WiFi connection status");
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean wifiConnected = (null != activeNetworkInfo)
                && (activeNetworkInfo.isConnectedOrConnecting());
//                && (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI);    // TODO Enable
        if (wifiConnected) {
            Log.d(TAG, "WiFi connected, starting sync");
            this.getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE)
                    .edit()
                    .putLong(KEY_LAST_SYNC, Calendar.getInstance().getTimeInMillis())
                    .apply();

            LocalDatabaseHelper helper = new LocalDatabaseHelper(this);
            SQLiteDatabase db = helper.getWritableDatabase();
            try {
                RemoteConnection remoteConnection = new RemoteConnection(this.getResources().getString(R.string.server_address));
                ResponsesCursorWrapper toSend = new ResponsesCursorWrapper( LocalDatabaseHelper.getUntransmitted(db) );
                remoteConnection.transmitResponses( toSend, helper );
                new IFNotification(this, this.getResources(), "Sync completed", "", null, 1).show();
            } catch (IOException | ArrayIndexOutOfBoundsException e) {
                // TODO
                e.printStackTrace();
            } finally {
                db.close();
            }

//            RemoteConnection.startSync(this);
        } else {
            Log.d(TAG, "WiFi disconnected, delaying sync");
        }
    }

    /**
     * Sets a repeating alarm to notify the user to fill in the daily questionnaire
     * @param context
     */
    public static void startSyncService(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent syncIntent = PendingIntent.getService(context, 0, SyncService.newIntent(context), 0);

        // Set target time
        // Using java.util.Calendar rather than android.icu.util.Calendar for compatability
        // Get target time from preferences
        SharedPreferences preferences = context.getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE);
        long lastSync;
        if (preferences.contains(KEY_LAST_SYNC)) {
            lastSync = preferences.getLong(KEY_LAST_SYNC, 0L);
            // 0L case shouldn't happen, shouldn't be a problem if it does
        } else {
            lastSync = Calendar.getInstance().getTimeInMillis();
            preferences.edit()
                    .putLong(KEY_LAST_SYNC, lastSync)
                    .apply();
        }
        Calendar NOW = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.setTimeInMillis( target.getTimeInMillis() + SYNC_INTERVAL );
        // TODO Use real time
//        target.setTimeInMillis(lastSync + SYNC_INTERVAL);
//        target.set(Calendar.HOUR_OF_DAY, DEFAULT_SYNC_HOUR);
//        target.set(Calendar.MINUTE, DEFAULT_SYNC_MINUTE);

        Log.d(TAG, "Scheduling first sync for " + target.get(Calendar.HOUR_OF_DAY) + ":" + target.get(Calendar.MINUTE)
                + ", repeating every " + SYNC_INTERVAL + "ms");
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, target.getTimeInMillis(), SYNC_INTERVAL, syncIntent);
    }
}