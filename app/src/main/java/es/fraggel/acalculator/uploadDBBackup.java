package es.fraggel.acalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.File;

import es.fraggel.acalculator.Models.User;

public class uploadDBBackup extends AsyncTask<String, String, String> {

    private String resp;
    Context context;
    static long idDownload;
    static String appName;
    static File ficheroBackup=null;
    public uploadDBBackup(Context contextin,File fichBackup)
    {
        context = contextin;
        ficheroBackup=fichBackup;
    }

    @Override
    protected String doInBackground(String... params) {

        try{
            SharedPreferences pref = context.getSharedPreferences("LocalUser",Context.MODE_PRIVATE);
            User user = new User();
            user.FirstName = pref.getString("FirstName",null);
            if(!user.FirstName.equals("Pablo")) {
                Util.uploadBackupFile(ficheroBackup,context,user);
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
