package com.example.MyLearningApp;


import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;


/**
 * Created by raja.chirala on 26/02/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    Context mContext;
    public AlarmReceiver(){
        //
    }

    public AlarmReceiver(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        DBHelper dbHelper = new DBHelper(context);
        MainActivity ma = new MainActivity();
        TimeZone timeZone = TimeZone.getTimeZone("GMT+5:30");
        final Calendar c = Calendar.getInstance(timeZone);
        c.add(Calendar.MINUTE,1);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        double sun_longitude = ma.getSunLongitude(year, month + 1, day);
        double moon_longitude = ma.getMoonLongitude(year, month + 1, day);
        int tithi = ma.getTithi(sun_longitude, moon_longitude);

        int paksa = ma.getPaksa(tithi);
        String paksa_name = ma.getPaksaName(paksa);
        String masa_name = ma.getMasa(year, month + 1, day, paksa);
        String tithi_name = ma.getTithiName(tithi);

        ArrayList array_list = dbHelper.getTodaysEvents(tithi_name, paksa_name, masa_name);

        Notify("Haribol", "Alert! " + array_list.size() + " Events Today", context);

    }



  //  public void onCreate(Bundle savedInstanceState) {
    //    Notify("Haribol", "You've received new message");
   // }

    private void Notify(String notificationTitle, String notificationMessage, Context context) {
        int mId = 1;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.vaishnavacalendarlogo)
                        .setAutoCancel(true)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationMessage);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        //Vibration
        mBuilder.setVibrate(new long[]{0, 1000});

        //LED
        mBuilder.setLights(Color.GREEN, 3000, 3000);

        //Tone
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.

        mNotificationManager.notify(mId, mBuilder.build());
    }
}
