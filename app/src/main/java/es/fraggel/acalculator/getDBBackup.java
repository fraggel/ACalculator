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

public class getDBBackup extends AsyncTask<String, String, String> {

    private String resp;
    Context context;
    Activity activity=null;
    static long idDownload;
    static String appName;
    public getDBBackup(Context contextin,Activity act)
    {
        context = contextin;
        activity=act;
    }

    @Override
    protected String doInBackground(String... params) {

        try{
            SharedPreferences pref = context.getSharedPreferences("LocalUser",Context.MODE_PRIVATE);
            User user = new User();
            user.FirstName = pref.getString("FirstName",null);
            Util.downloadBackupFile(context,user);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, "Copia Restaurada", Toast.LENGTH_SHORT).show();
        try{
            activity.finish();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onPreExecute() {

    }


    @Override
    protected void onProgressUpdate(String... text) {

    }
}
