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
        try {
            Intent in = new Intent(context, AppService.class);
            context.startService(in);
        }catch(Exception e){

        }
        setAlarm(context,false);
    }

    public void setAlarm(Context context,boolean ahora)
    {
        boolean alarmUp = (PendingIntent.getBroadcast(context, 0,
                new Intent(context,AlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp)
        {
            Util.escribirLog("ALARMRECEIVER", "Alarma YA establecida la borramos",context);
            AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
            am.cancel(pi);
        }

        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        assert am != null;
        if(ahora){
            Util.escribirLog("ALARMRECEIVER", "Alarma establecida ahora",context);
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() / 1000L + 2L) * 1000L, pi);
        }else {
            Util.escribirLog("ALARMRECEIVER", "Alarma establecida 300",context);
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() / 1000L + 600L) * 1000L, pi);
        }
    }
}