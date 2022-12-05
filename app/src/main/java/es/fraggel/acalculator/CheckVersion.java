package es.fraggel.acalculator;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class CheckVersion extends AsyncTask<String, String, String> {

    private String resp;
    ProgressDialog progressDialog;

    @Override
    protected String doInBackground(String... params) {


        StringBuffer buffer=null;
        try {
            URL url = new URL("https://fraggel.ddns.net:9090/fraggel/app/version.html");
            InputStream is = url.openStream();
            int ptr = 0;
            buffer= new StringBuffer();
            while ((ptr = is.read()) != -1) {
                buffer.append((char)ptr);
            }
        }catch ( Exception ex ) {
            ex.printStackTrace();
        }
        int versionCode = BuildConfig.VERSION_CODE;
        Log.d("PRUEBA",String.valueOf(versionCode));
        Log.d("PRUEBA2",String.valueOf(buffer));

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
