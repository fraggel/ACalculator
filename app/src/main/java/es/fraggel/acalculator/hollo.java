package es.fraggel.acalculator;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import es.fraggel.acalculator.Services.Tools;

public class hollo extends Activity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("PRUEBAAAA","PRUEBA2");
        Intent intent = new Intent(getApplicationContext(),AppService.class);
        getApplicationContext().startService(intent);
        finish();
    }
}