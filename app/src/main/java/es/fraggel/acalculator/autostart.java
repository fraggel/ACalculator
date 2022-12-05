package es.fraggel.acalculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class autostart extends BroadcastReceiver
{
    public void onReceive(Context context, Intent arg1)
    {
        /*Intent intent = new Intent(context,AppService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }*/
        Log.d("PRUEBAAAA","PRUEBA1");
        Intent intent = new Intent(context,hollo.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}