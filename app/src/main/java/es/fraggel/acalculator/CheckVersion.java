package es.fraggel.acalculator;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import es.fraggel.acalculator.Models.User;
import es.fraggel.acalculator.Services.LocalUserService;

public class CheckVersion extends AsyncTask<String, String, String> {

    private String resp;
    Context context;
    static long idDownload;
    static String appName;
    Activity activity=null;
    public CheckVersion(Context contextin,Activity act)
    {
        context = contextin;
        activity=act;
    }

    @Override
    protected String doInBackground(String... params) {

        try{
            StringBuffer buffer = null;
            try {
                URL url = new URL("http://fraggel.ddns.net:9090/fraggel/app/version.html");
                InputStream is = url.openStream();
                int ptr = 0;
                buffer = new StringBuffer();
                while ((ptr = is.read()) != -1) {
                    buffer.append((char) ptr);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            int versionCode = BuildConfig.VERSION_CODE;
            int serverVersion=versionCode;
            try{
                serverVersion=Integer.parseInt(buffer.toString().split("force")[0]);
            }catch (Exception e){}

            if (buffer.indexOf("force")!=-1|| versionCode < serverVersion) {

                DownloadManager.Request request = null;
                String fileName="";
                User user = LocalUserService.getLocalUserFromPreferences(context);
                if(user.FirstName.equals("Pablo")){
                    fileName="fCalculator.apk";
                }else if(user.FirstName.equals("Nombre")){
                    fileName="nCalculator.apk";
                }else if(user.FirstName.equals("Eva")){
                    fileName="eCalculator.apk";
                }else{
                    fileName="eCalculator.apk";
                }

                request=new DownloadManager.Request(Uri.parse("http://fraggel.ddns.net:9090/fraggel/app/"+serverVersion+"/"+fileName));
                appName=fileName;
                String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
                destination+=appName;
                File ficheroDescarga=new File(destination);
                if(ficheroDescarga.exists()){
                    boolean delete = ficheroDescarga.delete();
                    Log.d("BORRANDO",String.valueOf(delete));
                }
                request.setDescription("Downloading file " + fileName);
                request.setTitle("Downloading");
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                idDownload = manager.enqueue(request);
                activity.runOnUiThread(new Runnable() {

                    public void run() {

                        Toast.makeText(context, "Descargando actualización, por favor permanezca en esta pantalla", Toast.LENGTH_LONG).show();

                    }
                });

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    return resp;
    }


    @Override
    protected void onPostExecute(String result) {

    }


    @Override
    protected void onPreExecute() {


    }


    @Override
    protected void onProgressUpdate(String... text) {

    }
}
