package es.fraggel.acalculator;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Util {
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
    public static void uploadFile(File file){
        int flag ;
        FileInputStream fis = null;
        FTPSClient client = new FTPSClient();
        try {
            fis=new FileInputStream(file);
            client.connect("192.168.0.42",2121); // no el puerto es por defecto, podemos usar client.connect("servidor.ftp.com");
            client.execPBSZ(0);
            client.execPROT("P");
            client.login("fraggel", "ak47cold");
            client.enterLocalPassiveMode();
            client.enterLocalPassiveMode(); // IMPORTANTE!!!!
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.changeWorkingDirectory("/ftp");
            boolean uploadFile = client.storeFile(file.getName(),fis);
            client.logout();
            client.disconnect();

            if ( uploadFile == false ) {
                throw new Exception("Error al subir el fichero");
            }
        } catch (Exception eFTPClient) {
            eFTPClient.printStackTrace();
            // Gestionar el error, mostrar pantalla, reescalar excepcion... etc...
        } finally {
            try{
                fis.close();
            }catch(Exception e){

            }

        }
    }
}
