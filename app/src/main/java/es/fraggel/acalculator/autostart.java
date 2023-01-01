package es.fraggel.acalculator;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import es.fraggel.acalculator.Models.User;
import es.fraggel.acalculator.Services.LocalUserService;

public class autostart extends BroadcastReceiver
{
    public void onReceive(Context context, Intent arg1)
    {
        if(arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            //TODO ALARM
            /*boolean notificaciones = LocalUserService.getLocalUserFromPreferences(context).Notificaciones;
            User user = LocalUserService.getLocalUserFromPreferences(context);
            if(notificaciones && user.FirstName.equals("Pablo")) {
                Util.setAlarmNow(context);
            }*/

        }
    }


}