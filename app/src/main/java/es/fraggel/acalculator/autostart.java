package es.fraggel.acalculator;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class autostart extends BroadcastReceiver
{
    public void onReceive(Context context, Intent arg1)
    {
        if(arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            //setAlarm(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, AppService.class));
            }
        }
    }
    private void setAlarm(Context context) {
        Intent intent = new Intent(context, AppService.class);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getForegroundService(context, 1, intent, 0);
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30000, pendingIntent);
    }

}