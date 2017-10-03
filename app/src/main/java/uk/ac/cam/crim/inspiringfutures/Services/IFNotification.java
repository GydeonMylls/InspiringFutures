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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import uk.ac.cam.crim.inspiringfutures.R;

/**
 * Helper class to standardise notifications for the app
 * <p> Created by  Gideon Mills on 08/09/2017 for InspiringFutures-client. </p>
 */

public class IFNotification {
    private static int sNextId = 1;

    private final Context mContext;
    private final Notification mNotification;
    private final int mId;

    public Notification getNotification() {
        return mNotification;
    }

    /**
     * Create a notification with a specific id. THe notificatoin will have a standard colour, icon, light and vibrate pattern and will use the default notification sound
     * @param context
     * @param resources
     * @param title
     * @param message
     * @param pendingIntent
     * @param id               0 is reserved for daily reminders
     */
    @SuppressWarnings("JavaDoc")
    public IFNotification(Context context, Resources resources, String title, String message, PendingIntent pendingIntent, int id) {
        mContext = context;
        mId = id;

        int primaryColour;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            primaryColour = resources.getColor(R.color.colorPrimary, null);
        } else {
            primaryColour = resources.getColor(R.color.colorPrimary);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
//                .setLargeIcon(
////                        BitmapFactory.decodeResource(resources, R.drawable.ic_inspiringfutures_notification_small)
//
//                )
                .setColor(primaryColour)
                .setSmallIcon(R.drawable.ic_inspiringfutures_notification_small_png)
                .setTicker(title)
                .setContentTitle(title)
                .setContentText(message)

                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLights(primaryColour, 1000, 1000)
                .setVibrate(
                        new long[]{ 0L, 500L }
                )

                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true);

        // Set large icon, converting from bitmap
//        Drawable vectorIc = ContextCompat.getDrawable(this, R.drawable.ic_inspiringfutures_notification_large);
//        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
//            vectorIc = DrawableCompat.wrap(vectorIc).mutate();
//        }
//        Bitmap largeIc = Bitmap.createBitmap(vectorIc.getIntrinsicWidth(), vectorIc.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(largeIc);
//        vectorIc.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//        vectorIc.draw(canvas);
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_inspiringfutures_notification_small));
        //// ic_inspiringfutures_notification_large));//largeIc);

        // TODO Redesign icon according to Android design principles
        // Set notification colour on Lollipop+ devices
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            notificationBuilder.setColor(primaryColour);
//        }

        mNotification = notificationBuilder.build();
    }

    public IFNotification(Context context, Resources resources, String title, String message, PendingIntent pendingIntent) {
        this(context, resources, title, message, pendingIntent, sNextId++);
    }

    public void show() {
        NotificationManagerCompat.from(mContext).notify(mId, mNotification);
    }

}
