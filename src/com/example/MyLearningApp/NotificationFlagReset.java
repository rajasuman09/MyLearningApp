package com.example.MyLearningApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Raja.Chirala on 10/04/2016.
 */
public class NotificationFlagReset extends BroadcastReceiver {
    Context mContext;
    public NotificationFlagReset(){
        //
    }

    public NotificationFlagReset(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.resetNotificationFlag();
    }
}
