package com.xiaomi.securityfirewall;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;

import com.xiaomi.securityfirewall.Models.User;

public class uploadDBBackup extends AsyncTask<String, String, String> {

    private String resp;
    Context context;
    Activity activity=null;
    static long idDownload;
    static String appName;
    static File ficheroBackup=null;
    public uploadDBBackup(Context contextin,File fichBackup,Activity act)
    {
        context = contextin;
        ficheroBackup=fichBackup;
        activity=act;
    }

    @Override
    protected String doInBackground(String... params) {

        try{
            SharedPreferences pref = context.getSharedPreferences("LocalUser",Context.MODE_PRIVATE);
            User user = new User();
            user.FirstName = pref.getString("FirstName",null);
            Util.uploadBackupFile(ficheroBackup,context,user);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, "Copia Subida", Toast.LENGTH_SHORT).show();
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
