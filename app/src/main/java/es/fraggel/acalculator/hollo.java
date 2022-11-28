package es.fraggel.acalculator;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import es.fraggel.acalculator.Services.Tools;

public class hollo extends Activity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(getApplicationContext(),AppService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getBaseContext().startForegroundService(intent);
        } else {
            getBaseContext().startService(intent);
        }
        finish();
    }
}