package es.fraggel.acalculator;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.FirebaseOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Util {
    //App para eva
    //public static String EMAIL="fraggelillo666@gmail,com";
    //public static String NOMBRE="Pablo";
    //App para mi
    //public static String EMAIL="eva@gmail,com";
    //public static String NOMBRE="Eva";
    //App pruebas
    public static String EMAIL="pruebas@gmail,com";
    public static String NOMBRE="Pruebas";

    public static String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.VIBRATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
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
}
