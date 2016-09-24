package com.sharpdroid.registroelettronico;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class onBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPref = context.getSharedPreferences("Dati", Context.MODE_PRIVATE);
        if (sharedPref.getBoolean("notifichevoti", true) ||
                sharedPref.getBoolean("notificheagenda", true) ||
                sharedPref.getBoolean("notifichescrutini", true)) {
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent2 = new Intent(context, Notifiche.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, MainActivity.CONTROLLO_VOTI_ID, intent2, 0);

            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                    AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
        }
    }
}
