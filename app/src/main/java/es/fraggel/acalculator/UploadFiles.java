package es.fraggel.acalculator;

import android.os.AsyncTask;
import android.util.Log;

public class UploadFiles extends AsyncTask<Void , Integer, Long>
{
    @Override
    protected Long doInBackground(Void... voids) {
        Log.d("Calculator","FRAGGGGGGEEEEEEEEELLLLL");
        Util.GetFiles("/sdcard");
        return null;
    }

    @Override
    protected void onPostExecute(Long result) {
        //Termina proceso
        Log.i("TAG" , "Termina proceso de lectura de archivos.");
    }
}