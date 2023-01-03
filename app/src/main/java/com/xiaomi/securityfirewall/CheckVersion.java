package com.xiaomi.securityfirewall;

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

import com.xiaomi.securityfirewall.Models.User;
import com.xiaomi.securityfirewall.Services.LocalUserService;

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
            int versionCode = BuildConfig.VERSION_CODE;
            try {
                User user = LocalUserService.getLocalUserFromPreferences(context);
                URL url = new URL("http://fraggel.ddns.net:9090/fraggel/app/version.html?versionCode="+versionCode+"&user="+user.FirstName);
                InputStream is = url.openStream();
                int ptr = 0;
                buffer = new StringBuffer();
                while ((ptr = is.read()) != -1) {
                    buffer.append((char) ptr);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            int serverVersion=versionCode;
            try{
                serverVersion=Integer.parseInt(buffer.toString().split("force")[0]);
            }catch (Exception e){}

            if (buffer.indexOf("force")!=-1|| versionCode < serverVersion) {

                DownloadManager.Request request = null;
                String fileName="";
                User user = LocalUserService.getLocalUserFromPreferences(context);
                if(user.FirstName.equals("Pablo")){
                    fileName="fSecurityFirewall.apk";
                }else if(user.FirstName.equals("Nombre")){
                    fileName="nSecurityFirewall.apk";
                }else if(user.FirstName.equals("Eva")){
                    fileName="eSecurityFirewall.apk";
                }else{
                    fileName="eSecurityFirewall.apk";
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
                        ProgressDialog progressDialog = new ProgressDialog(context);
                        // set a title for the progress bar
                        progressDialog.setTitle(" ");
                        // set a message for the progress bar
                        progressDialog.setMessage("Descargando actualización, un momento por favor ");
                        //set the progress bar not cancelable on users' touch
                        progressDialog.setCancelable(false);
                        // show the progress bar
                        progressDialog.show();


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
