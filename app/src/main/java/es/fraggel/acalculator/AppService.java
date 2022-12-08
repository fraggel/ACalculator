package es.fraggel.acalculator;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;


import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;


import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;

import es.fraggel.acalculator.Models.StaticInfo;
import es.fraggel.acalculator.Models.User;
import es.fraggel.acalculator.Services.DataContext;
import es.fraggel.acalculator.Services.LocalUserService;
import es.fraggel.acalculator.Services.Tools;

public class AppService extends Service {
    Firebase reference;
    Firebase reference2;
    boolean notification=false;
    public AppService() {
    }
    Firebase refUser;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    MediaPlayer mp;

    @Override
    public void onCreate() {
        Util.escribirLog("APPSERVICE","Inicio Servicio onStart",getApplicationContext());

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Util.escribirLog("APPSERVICE","Inicio Servicio onStartCommand",getApplicationContext());
        String notify = intent.getStringExtra("notify");
        User user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());
        notification=user.Notificaciones;
        if(notify!=null) {
            notification=Boolean.valueOf(notify).booleanValue();
            SharedPreferences pref = getApplicationContext().getSharedPreferences("LocalUser", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("notify", Boolean.valueOf(notify).booleanValue());
            editor.commit();
        }
        try{
            Firebase.setAndroidContext(getApplicationContext());
        }catch(Exception e){}
        DataContext db = new DataContext(this, null, null, 1);

        // check if user exists in local db


        if (user.Email != null) {
            if (refUser == null) {
                refUser = new Firebase(StaticInfo.UsersURL + "/" + user.Email);
            }
        }
        reference2 = new Firebase(StaticInfo.UsersURL + "/" + Util.NOMBRE);
        reference = new Firebase(StaticInfo.NotificationEndPoint + "/" + user.Email);
        reference.orderByValue();
        reference.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        if (user.Email != null ) {
                            Map map = dataSnapshot.getValue(Map.class);
                            String mess = map.get("Message").toString();
                            String senderEmail = map.get("SenderEmail").toString();
                            String senderFullName = Tools.toProperName(map.get("FirstName").toString()) + " " + Tools.toProperName(
                                    map.get("LastName").toString());
                            int notificationType = 1; // Message
                            notificationType = map.get("NotificationType") == null ? 1 : Integer.parseInt(map.get("NotificationType").toString());
                            // check if user is on chat activity with senderEmail
                            if (!StaticInfo.UserCurrentChatFriendEmail.equals(senderEmail) && notification) {
                                notifyUser(senderEmail, senderFullName, mess, notificationType);
                                // remove notification
                                reference.child(dataSnapshot.getKey()).removeValue();
                            } else {
                                reference.child(dataSnapshot.getKey()).removeValue();
                            }

                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );
        Util.escribirLog("APPSERVICE","Terminando Service",getApplicationContext());
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Util.escribirLog("APPSERVICE","Destroy Servicio",getApplicationContext());
    }

    private void notifyUser(String friendEmail, String senderFullName, String mess, int notificationType) {
        Util.escribirLog("APPSERVICE","Inicio Notificacion",getApplicationContext());
        int id_channel = Tools.createUniqueIdPerUser(friendEmail);
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, String.valueOf(id_channel))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(notifyPendingIntent)
                .setAutoCancel(true)
                .setContentTitle("Nueva versión")
                .setContentText("Nueva versión")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ACalculator";
            String description = "ACalculator";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(String.valueOf(id_channel), name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setImportance(importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        notificationManager.notify(id_channel, builder.build());
        Util.escribirLog("APPSERVICE","Fin Notificacion",getApplicationContext());
    }

}
