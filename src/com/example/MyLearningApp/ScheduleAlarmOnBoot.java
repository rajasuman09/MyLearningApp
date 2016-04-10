package com.example.MyLearningApp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Raja.Chirala on 10/04/2016.
 */
public class ScheduleAlarmOnBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            DBHelper myDbHelper = new DBHelper(context);
            MainActivity mainActivity = new MainActivity();
            mainActivity.scheduleAlarm(myDbHelper, true);
            mainActivity.scheduleNotificationFlagReset(myDbHelper, true);

        }
    }

}
