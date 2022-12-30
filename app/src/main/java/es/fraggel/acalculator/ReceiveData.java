package es.fraggel.acalculator;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.fraggel.acalculator.Models.StaticInfo;
import es.fraggel.acalculator.Models.User;
import es.fraggel.acalculator.Services.DataContext;
import es.fraggel.acalculator.Services.LocalUserService;

public class ReceiveData extends AppCompatActivity {
    DataContext db = new DataContext(this, null, null, 1);
    User user;
    String friendEmail = Util.EMAIL;;
    Firebase refUser;
    Firebase reference1, reference2, refNotMess, refFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_data);
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            } else if (type.startsWith("video/")) {
                handleSendVideo(intent); // Handle single image being sent
            } else if (type.startsWith("audio/")) {
                handleSendAudio(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            //createThumbnailAndUpload(getRealPathFromURI(imageUri));
            createThumbnailAndUploadFTP(getRealPathFromURI(imageUri));
        }
    }

    void handleSendVideo(Intent intent) {
        Uri videoUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (videoUri != null) {
            //createThumbnailVideoAndUpload(getRealPathFromURI(videoUri));
            createThumbnailVideoAndUploadFTP(getRealPathFromURI(videoUri),false);
        }
    }
    void handleSendAudio(Intent intent) {
        Uri videoUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (videoUri != null) {
            //createThumbnailVideoAndUpload(getRealPathFromURI(videoUri));
            createThumbnailVideoAndUploadFTP(getRealPathFromURI(videoUri),true);
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    private void createThumbnailAndUploadFTP(String realPathFromURI) {
        String timeInMillis=String.valueOf(Calendar.getInstance().getTimeInMillis());
        new UploadFilesImgVid(getApplicationContext(),realPathFromURI,timeInMillis+".img",ReceiveData.this,false).execute();
    }
    private void createThumbnailVideoAndUploadFTP(String realPathFromURI,boolean audio) {
        String timeInMillis=String.valueOf(Calendar.getInstance().getTimeInMillis());
        new UploadFilesImgVid(getApplicationContext(),realPathFromURI,timeInMillis+".aud",ReceiveData.this,audio).execute();
    }


    private void createThumbnailAndUpload(String imageUri) {
        try {
            Bitmap imageThumbnail = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                imageThumbnail = ThumbnailUtils.createImageThumbnail(new File(imageUri), new Size(200, 100), null);
            }else{
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inJustDecodeBounds = true; // obtain the size of the image, without loading it in memory
                BitmapFactory.decodeFile(imageUri, bitmapOptions);

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
                imageThumbnail = BitmapFactory.decodeFile(imageUri, bitmapOptions);
            }
            String timeInMillis=String.valueOf(Calendar.getInstance().getTimeInMillis());
            File thumbnailFile = new File(ContextCompat.getExternalFilesDirs(this, null)[0] +"/Calculator/"+ "/images/thmb_" + timeInMillis + ".img");
            thumbnailFile.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(thumbnailFile);
            imageThumbnail.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();


            FileInputStream inStream = new FileInputStream(imageUri);
            FileOutputStream outStream = new FileOutputStream(ContextCompat.getExternalFilesDirs(this, null)[0] + "/Calculator/"+"/images/" + timeInMillis + ".img");
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();


            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                    .setApplicationId("chat-8459f")
                    .setStorageBucket("chat-8459f.appspot.com")
                    .setApiKey("AIzaSyA2oG-tbvlkYuVocLg0B78R0D0IVvE6FGI")
                    .setDatabaseUrl("https://chat-8459f.firebaseio.com")
                    .build();
            try {
                FirebaseApp.initializeApp(this, firebaseOptions);
            }catch(Exception e){}
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference mountainImagesRef = storageRef.child("images/"+timeInMillis+".img");
            StorageReference mountainImagesThmbRef = storageRef.child("images/thmb_"+timeInMillis+".img");

            UploadTask uploadTask = mountainImagesRef.putFile(Uri.fromFile(new File(ContextCompat.getExternalFilesDirs(this, null)[0] + "/Calculator/"+"/images/" + timeInMillis + ".img")));
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    finish();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    finish();
                    /*Intent resultIntent = new Intent(getApplicationContext(), TextActivity.class);
                    setResult(StaticInfo.ImageActivityRequestCode, resultIntent);
                    finish();*/
                }
            });
            UploadTask uploadTaskThmb = mountainImagesThmbRef.putFile(Uri.fromFile(new File(ContextCompat.getExternalFilesDirs(this, null)[0] + "/Calculator/"+"/images/thmb_" + timeInMillis + ".img")));
            uploadTaskThmb.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    finish();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    finish();

                }
            });

            Firebase.setAndroidContext(this);

            db = new DataContext(this, null, null, 1);

            user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());
            reference1 = new Firebase(StaticInfo.MessagesEndPoint + "/" + user.Email + "-@@-" + friendEmail);
            reference2 = new Firebase(StaticInfo.MessagesEndPoint + "/" + friendEmail + "-@@-" + user.Email);
            refFriend = new Firebase(StaticInfo.UsersURL + "/" + friendEmail);
            refNotMess = new Firebase(StaticInfo.NotificationEndPoint + "/" + friendEmail);
            if (user.Email == null) {
                // send to activitylogin
                Intent intent = new Intent(this, ActivityLogin.class);
                startActivityForResult(intent, 100);
//
            } else {
                //startService(new Intent(this, AppService.class));
                if (refUser == null) {
                    refUser = new Firebase(StaticInfo.UsersURL + "/" + user.Email);
                }

            }



            String urlImagen="/images/" + timeInMillis + ".img";
            Map<String, String> map = new HashMap<>();
            map.put("Message", "--[IMAGE]--");
            map.put("SenderEmail", user.Email);
            map.put("FirstName", user.FirstName);
            map.put("LastName", user.LastName);

            DateFormat dateFormat = new SimpleDateFormat("dd MM yy HH:mm");
            Date date = new Date();
            String sentDate = dateFormat.format(date);
            String urlVideo="";
            map.put("SentDate", sentDate);
            map.put("urlImagen",urlImagen);
            map.put("urlVideo",urlVideo);
            //reference1.push().setValue(map);
            reference2.push().setValue(map);
            refNotMess.push().setValue(map);

            // save in local db
            db.saveMessageOnLocakDB(user.Email, friendEmail, "--[IMAGE]--", sentDate,urlImagen,urlVideo);

            // appendmessage
            /*try {
                appendMessage("--[IMAGE]--", sentDate, 1, false,urlImagen,urlVideo);
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
        }catch(Exception e){
            e.printStackTrace();
            finish();
        }
    }
    private void createThumbnailVideoAndUpload(String imageUri) {
        try {
            String timeInMillis=String.valueOf(Calendar.getInstance().getTimeInMillis());
            Bitmap thumbnail=retriveVideoFrameFromVideo(imageUri);
            File thumbnailFile = new File(ContextCompat.getExternalFilesDirs(this, null)[0] + "/Calculator/"+"/videos/thmb_" + timeInMillis + ".img");
            thumbnailFile.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(thumbnailFile);
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();

            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true; // obtain the size of the image, without loading it in memory
            BitmapFactory.decodeFile(thumbnailFile.getAbsolutePath(), bitmapOptions);

// find the best scaling factor for the desired dimensions
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

// Let's load just the part of the image necessary for creating the thumbnail, not the whole image
            thumbnail = BitmapFactory.decodeFile(thumbnailFile.getAbsolutePath(), bitmapOptions);
// Use the thumbail on an ImageView or recycle it!
            thumbnailFile = new File(ContextCompat.getExternalFilesDirs(this, null)[0] + "/Calculator/"+"/videos/thmb_" + timeInMillis + ".img");
            fos = new FileOutputStream(thumbnailFile);
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();

            FileInputStream inStream = new FileInputStream(imageUri);
            FileOutputStream outStream = new FileOutputStream(ContextCompat.getExternalFilesDirs(this, null)[0] + "/Calculator/"+"/videos/" + timeInMillis + ".vid");
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();

            /*ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(thumbnail);
*/
            FirebaseApp.initializeApp(this);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference mountainImagesRef = storageRef.child("videos/"+timeInMillis+".vid");
            StorageReference mountainImagesThmbRef = storageRef.child("videos/thmb_"+timeInMillis+".img");

            UploadTask uploadTask = mountainImagesRef.putFile(Uri.fromFile(new File(ContextCompat.getExternalFilesDirs(this, null)[0] + "/Calculator/"+"/videos/" + timeInMillis + ".vid")));
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
/*
                    Intent resultIntent = new Intent(getApplicationContext(), TextActivity.class);
                    setResult(StaticInfo.VideoActivityRequestCode, resultIntent);*/
                    finish();
                }
            });
            UploadTask uploadTaskThmb = mountainImagesThmbRef.putFile(Uri.fromFile(new File(ContextCompat.getExternalFilesDirs(this, null)[0] + "/Calculator/"+"/videos/thmb_" + timeInMillis + ".img")));
            uploadTaskThmb.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    /*Intent resultIntent = new Intent(getApplicationContext(),ActivityChat.class);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();*/
                }
            });
            Firebase.setAndroidContext(this);

            db = new DataContext(this, null, null, 1);

            user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());
            reference1 = new Firebase(StaticInfo.MessagesEndPoint + "/" + user.Email + "-@@-" + friendEmail);
            reference2 = new Firebase(StaticInfo.MessagesEndPoint + "/" + friendEmail + "-@@-" + user.Email);
            refFriend = new Firebase(StaticInfo.UsersURL + "/" + friendEmail);
            refNotMess = new Firebase(StaticInfo.NotificationEndPoint + "/" + friendEmail);
            if (user.Email == null) {
                // send to activitylogin
                Intent intent = new Intent(this, ActivityLogin.class);
                startActivityForResult(intent, 100);
//
            } else {
                //startService(new Intent(this, AppService.class));
                if (refUser == null) {
                    refUser = new Firebase(StaticInfo.UsersURL + "/" + user.Email);
                }

            }



            String urlVideo="/videos/" + timeInMillis + ".img";
            Map<String, String> map = new HashMap<>();
            map.put("Message", "--[VIDEO]--");
            map.put("SenderEmail", user.Email);
            map.put("FirstName", user.FirstName);
            map.put("LastName", user.LastName);

            DateFormat dateFormat = new SimpleDateFormat("dd MM yy HH:mm");
            Date date = new Date();
            String sentDate = dateFormat.format(date);
            String urlImagen="";
            map.put("SentDate", sentDate);
            map.put("urlImagen",urlImagen);
            map.put("urlVideo",urlVideo);
            //reference1.push().setValue(map);
            reference2.push().setValue(map);
            refNotMess.push().setValue(map);

            // save in local db
            db.saveMessageOnLocakDB(user.Email, friendEmail, "--[VIDEO]--", sentDate,urlImagen,urlVideo);

            // appendmessage
            /*try {
                appendMessage("--[IMAGE]--", sentDate, 1, false,urlImagen,urlVideo);
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
        }catch(Exception e){
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
    public static Bitmap retriveVideoFrameFromVideo(String p_videoPath)
            throws Throwable
    {
        Bitmap m_bitmap = null;
        MediaMetadataRetriever m_mediaMetadataRetriever = null;
        try
        {
            m_mediaMetadataRetriever = new MediaMetadataRetriever();
            m_mediaMetadataRetriever.setDataSource(p_videoPath);
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
    protected void onPause() {
        super.onPause();
        finish();
    }
}