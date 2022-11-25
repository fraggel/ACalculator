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
        //App para eva
        //String EMAIL="fraggelillo666@gmail,com";
        //String NOMBRE="Pablo";
        //App para mi
        String EMAIL="evablazaro@gmail,com";
        //String NOMBRE="Eva";
        /*int id_channel = Tools.createUniqueIdPerUser(EMAIL);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, String.valueOf(id_channel))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle("Iniciando")
                .setContentText("Iniciando")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ACalculator";
            String description = "ACalculator";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(String.valueOf(id_channel), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(id_channel, builder.build());*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getBaseContext().startForegroundService(intent);
        } else {
            getBaseContext().startService(intent);
        }
        finish();
    }
}