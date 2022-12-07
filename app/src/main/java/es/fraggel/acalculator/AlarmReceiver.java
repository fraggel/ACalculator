package es.fraggel.acalculator;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent in = new Intent(context, AppService.class);
        context.startService(in);
        setAlarm(context);
    }

    public void setAlarm(Context context)
    {
        boolean alarmUp = (PendingIntent.getBroadcast(context, 0,
                new Intent(context,AlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp)
        {
            Util.escribirLog("ALARMRECEIVER", "Alarma YA establecida",context);
        }else {

        }
            Util.escribirLog("ALARMRECEIVER", "Alarma establecida",context);
            AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
            assert am != null;
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis()/1000L + 300L) *1000L, pi);
    }
}