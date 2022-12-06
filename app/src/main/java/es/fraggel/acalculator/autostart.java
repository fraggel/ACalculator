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
            context.startService(new Intent(context,AppService.class));
            Util.setAlarm(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //context.startForegroundService(new Intent(context, AppService.class));
            }
        }
    }


}