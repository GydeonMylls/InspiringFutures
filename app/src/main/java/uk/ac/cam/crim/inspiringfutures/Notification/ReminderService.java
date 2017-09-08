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

package uk.ac.cam.crim.inspiringfutures.Notification;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.Nullable;

import java.util.Calendar;

import uk.ac.cam.crim.inspiringfutures.MainActivity;

/**
 * <p> Created by  Gideon Mills on 07/09/2017 for InspiringFutures-client. </p>
 */

public class ReminderService extends IntentService {

    public static final String TAG = "ReminderService";

    public static final int HOUR = 15;
    public static final int MINUTE = 10;
    public static final long INTERVAL = 10 * 1000;//AlarmManager.INTERVAL_DAY;      // TODO Remove

    public ReminderService() {
        super(TAG);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, ReminderService.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Resources resources = getResources();
        PendingIntent activityIntent = PendingIntent.getActivity(this, 0, MainActivity.newIntent(this), 0);

        new IFNotification(this, resources, "Test title", "Test message", activityIntent, 0).show();

        PendingIntent notificationIntent = PendingIntent.getService(this, 0, ReminderService.newIntent(this), 0);
        Calendar target = Calendar.getInstance();
        target.add(Calendar.SECOND, 30);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, target.getTimeInMillis(), notificationIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, target.getTimeInMillis(), notificationIntent);
        }
    }

    public static void startReminder(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent notificationIntent = PendingIntent.getService(context, 0, ReminderService.newIntent(context), 0);

        // Set target time
        // Using java.util.Calendar rather than android.icu.util.Calendar for compatability
        Calendar NOW = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.add(Calendar.SECOND, 10);    // TODO Remove
//        target.setTime( new Date() );
//        target.set(Calendar.HOUR_OF_DAY, HOUR);
//        target.set(Calendar.MINUTE, MINUTE);
        if (target.before(NOW)) target.add(Calendar.DATE,1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, target.getTimeInMillis(), notificationIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, target.getTimeInMillis(), notificationIntent);
        }
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, target.getTimeInMillis(), INTERVAL, notificationIntent);
    }
}
