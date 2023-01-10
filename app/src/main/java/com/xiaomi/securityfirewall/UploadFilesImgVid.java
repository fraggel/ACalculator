package com.xiaomi.securityfirewall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsProvider;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.xiaomi.securityfirewall.Models.StaticInfo;
import com.xiaomi.securityfirewall.Models.User;
import com.xiaomi.securityfirewall.Services.DataContext;
import com.xiaomi.securityfirewall.Services.LocalUserService;

public class UploadFilesImgVid extends AsyncTask<Void , Integer, Long>
{
    private static Context mContext;
    Activity activity=null;
    private String realPathFromURI;
    private String timeInMillisName;
    boolean upload=true;
    boolean audio=false;
    InputStream stream=null;
    String friendEmail = Util.EMAIL;
    ProgressDialog progressDialog=null;
    public UploadFilesImgVid(Context context, String file, String timeInM, Activity act, boolean aud, InputStream strm, ProgressDialog prog){
        realPathFromURI=file;
        mContext = context;
        timeInMillisName=timeInM;
        activity=act;
        audio=aud;
        stream=strm;
        progressDialog=prog;
    }

    @Override
    protected Long doInBackground(Void... voids) {
        int flag;
        InputStream fis = null;
        File file=null;
        FTPClient client = new FTPClient();
        File thumbnailFile=null;
        File fileTMP=new File(ContextCompat.getExternalFilesDirs(mContext, null)[0] +"/SecurityFirewall/"+ "/images/tmp_" + timeInMillisName);
        fileTMP.getParentFile().mkdirs();
        try {
            if(timeInMillisName.indexOf(".img")!=-1){
                Bitmap imageThumbnail = null;
                InputStream inputStream=null;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    Uri uri=null;
                    try {
                        uri= FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", new File(realPathFromURI));
                        inputStream = mContext.getContentResolver().openInputStream(uri);
                    }catch(Exception e){
                        inputStream=stream;
                    }
                    FileOutputStream foo=new FileOutputStream(fileTMP);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        foo.write(buffer, 0, length);
                    }
                    foo.flush();
                    foo.close();

                    inputStream.close();

                    imageThumbnail = ThumbnailUtils.createImageThumbnail(fileTMP, new Size(200, 100), null);
                }
                thumbnailFile = new File(ContextCompat.getExternalFilesDirs(mContext, null)[0] +"/SecurityFirewall/"+ "/images/thmb_" + timeInMillisName);
                thumbnailFile.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(thumbnailFile);
                imageThumbnail.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                fos.close();
            }else {
                if (!audio) {
                    Bitmap thumbnail = retriveVideoFrameFromVideo(realPathFromURI);
                    thumbnailFile = new File(ContextCompat.getExternalFilesDirs(mContext, null)[0] + "/SecurityFirewall/" + "/videos/thmb_" + timeInMillisName);
                    thumbnailFile.getParentFile().mkdirs();
                    FileOutputStream fos = new FileOutputStream(thumbnailFile);
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.flush();
                    fos.close();

                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmapOptions.inJustDecodeBounds = true; // obtain the size of the image, without loading it in memory
                    BitmapFactory.decodeFile(thumbnailFile.getAbsolutePath(), bitmapOptions);

                    int desiredWidth = 200;
                    int desiredHeight = 100;
                    float widthScale = (float) bitmapOptions.outWidth / desiredWidth;
                    float heightScale = (float) bitmapOptions.outHeight / desiredHeight;
                    float scale = Math.min(widthScale, heightScale);

                    int sampleSize = 1;
                    while (sampleSize < scale) {
                        sampleSize *= 2;
                    }
                    bitmapOptions.inSampleSize = sampleSize; // this value must be a power of 2,
                    // this is why you can not have an image scaled as you would like
                    bitmapOptions.inJustDecodeBounds = false; // now we want to load the image

                    thumbnail = BitmapFactory.decodeFile(thumbnailFile.getAbsolutePath(), bitmapOptions);
                    thumbnailFile = new File(ContextCompat.getExternalFilesDirs(mContext, null)[0] + "/SecurityFirewall/" + "/videos/thmb_" + timeInMillisName);
                    thumbnailFile.getParentFile().mkdirs();
                    fos = new FileOutputStream(thumbnailFile);
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.flush();
                    fos.close();

                }else {

                    Bitmap thumbnail = null;
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setTextSize(20);
                    paint.setColor(Color.BLUE);
                    paint.setTextAlign(Paint.Align.LEFT);
                    float baseline = -paint.ascent(); // ascent() is negative
                    int width = (int) (paint.measureText("Audio") + 0.5f); // round
                    int height = (int) (baseline + paint.descent() + 0.5f);
                    Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(image);
                    canvas.drawText("Audio", 0, baseline, paint);
                    thumbnail = image;
                    thumbnailFile = new File(ContextCompat.getExternalFilesDirs(mContext, null)[0] + "/SecurityFirewall/" + "/audio/thmb_" + timeInMillisName);
                    thumbnailFile.getParentFile().mkdirs();
                    FileOutputStream fos = new FileOutputStream(thumbnailFile);
                    thumbnail.compress(Bitmap.CompressFormat.PNG, 90, fos);
                    fos.flush();
                    fos.close();
                }
            }
            String workingDir = "";
            try {
                file = new File(realPathFromURI);
                fis = new FileInputStream(file);
            }catch (Exception e){
                fis=new FileInputStream(fileTMP);

            }
            client.connect("fraggel.ddns.net", 2121); // no el puerto es por defecto, podemos usar client.connect("servidor.ftp.com");
            client.login("images", "images");
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            workingDir="/ftp/chatImages";
            client.changeWorkingDirectory(workingDir);
            FTPFile[] ftpFiles = client.listFiles(file.getName());
            boolean uploadFile=false;
            if (ftpFiles.length > 0)
            {
                //Log.d("UTIL","fichero ya existe");
            }
            else
            {
                uploadFile = client.storeFile(timeInMillisName, fis);
                uploadFile=client.storeFile("thmb_"+timeInMillisName,new FileInputStream(thumbnailFile));
            }
            client.logout();
            client.disconnect();
            thumbnailFile.delete();
            if(fileTMP!=null){
                fileTMP.delete();
            }
            Firebase.setAndroidContext(mContext);

            DataContext db = new DataContext(mContext, null, null, 1);
            User user = LocalUserService.getLocalUserFromPreferences(mContext);
            Firebase refUser = new Firebase(StaticInfo.UsersURL + "/" + user.Email);
            Firebase reference2 = new Firebase(StaticInfo.MessagesEndPoint + "/" + friendEmail + "-@@-" + user.Email);
            Firebase refNotMess = new Firebase(StaticInfo.NotificationEndPoint + "/" + friendEmail);

            String urlImagen="thmb_"+timeInMillisName;
            Map<String, String> map = new HashMap<>();
            if(timeInMillisName.indexOf(".img")!=-1){
                map.put("Message", "--[IMAGE]--");
            }else{
                map.put("Message", "--[VIDEO]--");
            }

            map.put("SenderEmail", user.Email);
            map.put("FirstName", user.FirstName);
            map.put("LastName", user.LastName);

            DateFormat dateFormat = new SimpleDateFormat("dd MM yy HH:mm");
            Date date = new Date();
            String sentDate = dateFormat.format(date);
            String urlVideo="";
            if(timeInMillisName.indexOf(".img")==-1){
                urlVideo=urlImagen;
                urlImagen="";
            }
            map.put("SentDate", sentDate);
            map.put("urlImagen",urlImagen);
            map.put("urlVideo",urlVideo);
            //reference1.push().setValue(map);
            reference2.push().setValue(map);
            refNotMess.push().setValue(map);

            // save in local db
            if(timeInMillisName.indexOf(".img")!=-1){
                db.saveMessageOnLocalDB(user.Email, friendEmail, "--[IMAGE]--", sentDate,urlImagen,urlVideo);
            }else{
                db.saveMessageOnLocalDB(user.Email, friendEmail, "--[VIDEO]--", sentDate,urlImagen,urlVideo);
            }

        } catch (Exception eFTPClient) {
            eFTPClient.printStackTrace();
            // Gestionar el error, mostrar pantalla, reescalar excepcion... etc...
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {

            }

        }

        return null;
    }
    public static Bitmap retriveVideoFrameFromVideo(String p_videoPath)
            throws Throwable
    {
        Bitmap m_bitmap = null;
        MediaMetadataRetriever m_mediaMetadataRetriever = null;
        try
        {
            m_mediaMetadataRetriever = new MediaMetadataRetriever();
            try {
                m_mediaMetadataRetriever.setDataSource(p_videoPath);
            }catch(Exception e){
                Uri uri = FileProvider.getUriForFile(mContext,mContext.getPackageName()+".fileprovider",new File(p_videoPath));
                m_mediaMetadataRetriever.setDataSource(mContext,uri);
            }
            m_bitmap = m_mediaMetadataRetriever.getFrameAtTime();
        }
        catch (Exception m_e)
        {
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String p_videoPath)"
                            + m_e.getMessage());
        }
        finally
        {
            if (m_mediaMetadataRetriever != null)
            {
                m_mediaMetadataRetriever.release();
            }
        }
        return m_bitmap;
    }
    @Override
    protected void onPostExecute(Long result) {
        Toast.makeText(mContext, "Subida Terminada", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
        try{
            activity.finish();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}