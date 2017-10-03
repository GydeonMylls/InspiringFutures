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
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;

import uk.ac.cam.crim.inspiringfutures.MainActivity;
import uk.ac.cam.crim.inspiringfutures.R;

/**
 * Notifies the user regularly (daily?) to fill out their daily questionnaire, initiate with the startReminderService method
 * <p> Created by  Gideon Mills on 07/09/2017 for InspiringFutures-client. </p>
 */

public class ReminderService extends IntentService {

    public static final String TAG = "ReminderService";

    public static final String KEY_REMINDER_HOUR = "reminder_hour";
    public static final String KEY_REMINDER_MINUTE = "reminder_minute";
    public static final String KEY_REMINDER_INTERVAL = "reminder_interval";

    public static final int DEFAULT_REMINDER_HOUR = 15;
    public static final int DEFAULT_REMINDER_MINUTE = 0;
    public static final long DEFAULT_REMINDER_INTERVAL = 10 * 1000;      //AlarmManager.INTERVAL_DAY;      // TODO Set interval to one day

    public static final long REMINDER_INTERVAL = 60 * 1000;      //AlarmManager.INTERVAL_DAY;      // TODO Set interval to one day

    public ReminderService() {
        super(TAG);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, ReminderService.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "Launching reminder notification");
        Resources resources = getResources();
        PendingIntent activityIntent = PendingIntent.getActivity(this, 0, MainActivity.newIntent(this), 0);

        // TODO Have reminder title and message as properties of the questionnaire
        new IFNotification(this, resources, getString(R.string.reminder_title), getString(R.string.reminder_message), activityIntent, 0).show();
    }

    /**
     * Sets a repeating alarm to notify the user to fill in the daily questionnaire
     * @param context
     */
    public static void startReminderService(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent notificationIntent = PendingIntent.getService(context, 0, ReminderService.newIntent(context), 0);

        // Get target time from preferences
        SharedPreferences preferences = context.getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE);
        int reminderHour;
        int reminderMinute;
        long reminderInterval;
        if (preferences.contains(KEY_REMINDER_HOUR)) {
            reminderHour = preferences.getInt(KEY_REMINDER_HOUR, DEFAULT_REMINDER_HOUR);
            reminderMinute = preferences.getInt(KEY_REMINDER_MINUTE, DEFAULT_REMINDER_MINUTE);
            reminderInterval = preferences.getLong(KEY_REMINDER_INTERVAL, DEFAULT_REMINDER_INTERVAL);
        } else {
            reminderHour = DEFAULT_REMINDER_HOUR;
            reminderMinute = DEFAULT_REMINDER_MINUTE;
            reminderInterval = DEFAULT_REMINDER_INTERVAL;
            preferences.edit()
                    .putInt(KEY_REMINDER_HOUR, reminderHour)
                    .putInt(KEY_REMINDER_MINUTE, reminderMinute)
                    .putLong(KEY_REMINDER_INTERVAL, reminderInterval)
                    .apply();
        }

        // Set target time
        // Using java.util.Calendar rather than android.icu.util.Calendar for compatability
        Calendar NOW = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.setTimeInMillis( target.getTimeInMillis() + REMINDER_INTERVAL );
        // TODO Use real time
//        target.setTime( new Date() );
//        target.set(Calendar.HOUR_OF_DAY, reminderHour);
//        target.set(Calendar.MINUTE, reminderMinute);
//        if (target.before(NOW)) target.add(Calendar.DATE,1);

        Log.d(TAG, "Scheduling first reminder for " + target.get(Calendar.HOUR_OF_DAY) + ":" + target.get(Calendar.MINUTE)
                + ", repeating every " + REMINDER_INTERVAL + "ms");
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, target.getTimeInMillis(), REMINDER_INTERVAL, notificationIntent);
    }
}
