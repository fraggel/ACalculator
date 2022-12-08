package es.fraggel.acalculator;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;

public class DonwloadCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){
            Toast.makeText(context,"Instalando actualización", Toast.LENGTH_SHORT).show();
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
            String fileName = CheckVersion.appName;
            destination += fileName;
            final Uri uri = FileProvider.getUriForFile(context,context.getPackageName()+".fileprovider",new File(destination));

            Intent intent2 = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent2.setDataAndType(uri, "application/vnd.android.package-archive");
            intent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent2);
            /*Intent intent2 = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent2.setData(uri);
            intent2.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);*/
        }
    }
}
