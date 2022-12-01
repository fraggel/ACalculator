package es.fraggel.acalculator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

import es.fraggel.acalculator.Models.StaticInfo;

public class MediaSelection extends AppCompatActivity {
    public static final int PICK_IMAGE = 1;
    public static final int PICK_VIDEO = 2;
    public static final int TAKE_IMAGE = 3;
    public static final int TAKE_VIDEO = 4;
    long timeInMillis=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_selection);
        Intent intent = getIntent();
        timeInMillis= intent.getLongExtra("timeinmillis",-1);
        ImageView imgSelect = (ImageView) findViewById(R.id.imageView2);
        ImageView videoSelect = (ImageView) findViewById(R.id.imageView3);
        ImageView takePicture = (ImageView) findViewById(R.id.imageView4);
        ImageView takeVideo = (ImageView) findViewById(R.id.imageView5);
        imgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Selecciona archivo"), PICK_IMAGE);
            }
        });
        videoSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Selecciona archivo"), PICK_VIDEO);
            }
        });
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(ContextCompat.getExternalFilesDirs(getApplicationContext(), null)[0]+"/Calculator/"+"/images/", "temp.img");
                f.getParentFile().mkdirs();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, TAKE_IMAGE);
            }
        });
        takeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                File f = new File(Environment.getExternalStorageDirectory()+"/Calculator/"+"/videos/", "temp.vid");
                f.getParentFile().mkdirs();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                startActivityForResult(intent, TAKE_VIDEO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_IMAGE && resultCode == Activity.RESULT_OK) {
            File f = new File(ContextCompat.getExternalFilesDirs(this, null)[0] +"/Calculator/"+ "/images/");
            f.mkdirs();
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.img")) {
                    f = temp;
                    break;
                }
            }
            createThumbnailAndUpload(f.getAbsolutePath());
            super.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            File f = new File(ContextCompat.getExternalFilesDirs(this, null)[0] + "/Calculator/"+"/images/");
            f.mkdirs();
            Uri data1 = data.getData();
            createThumbnailAndUpload(RealPathUtil.getRealPathFromURI(this,data1));
            super.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == TAKE_VIDEO && resultCode == Activity.RESULT_OK) {
            File f = new File(ContextCompat.getExternalFilesDirs(this, null)[0] + "/Calculator/"+"/videos/");
            f.mkdirs();
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.vid")) {
                    f = temp;
                    break;
                }
            }
            createThumbnailVideoAndUpload(f.getAbsolutePath());
            super.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == PICK_VIDEO && resultCode == Activity.RESULT_OK) {
            File f = new File(ContextCompat.getExternalFilesDirs(this, null)[0] + "/Calculator/"+"/videos/");
            f.mkdirs();
            Uri data1 = data.getData();
            createThumbnailVideoAndUpload(RealPathUtil.getRealPathFromURI(this,data1));
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void createThumbnailAndUpload(String imageUri) {
        try {
            ImageView img=(ImageView)findViewById(R.id.imageView6);
            TextView txt=(TextView)findViewById(R.id.textView6);
            img.setVisibility(View.VISIBLE);
            txt.setVisibility(View.VISIBLE);
            Bitmap imageThumbnail = ThumbnailUtils.createImageThumbnail(new File(imageUri), new Size(200, 100), null);
            File thumbnailFile = new File(ContextCompat.getExternalFilesDirs(this, null)[0] +"/Calculator/"+ "/images/thmb_" + timeInMillis + ".img");
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
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Intent resultIntent = new Intent(getApplicationContext(), TextActivity.class);
                    setResult(StaticInfo.ImageActivityRequestCode, resultIntent);
                    finish();
                }
            });
            UploadTask uploadTaskThmb = mountainImagesThmbRef.putFile(Uri.fromFile(new File(ContextCompat.getExternalFilesDirs(this, null)[0] + "/Calculator/"+"/images/thmb_" + timeInMillis + ".img")));
            /*uploadTaskThmb.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Intent resultIntent = new Intent(getApplicationContext(),TextActivity.class);
                    setResult(StaticInfo.ImageActivityRequestCode, resultIntent);
                    finish();
                }
            });*/
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void createThumbnailVideoAndUpload(String imageUri) {
        try {
            ImageView img=(ImageView)findViewById(R.id.imageView6);
            TextView txt=(TextView)findViewById(R.id.textView6);
            img.setVisibility(View.VISIBLE);
            txt.setVisibility(View.VISIBLE);
/*
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true; // obtain the size of the image, without loading it in memory
            BitmapFactory.decodeFile(imageUri, bitmapOptions);

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
            Bitmap thumbnail = BitmapFactory.decodeFile(imageUri, bitmapOptions);
*/
// Save the thumbnail




            Bitmap thumbnail=retriveVideoFrameFromVideo(imageUri);
            File thumbnailFile = new File(ContextCompat.getExternalFilesDirs(this, null)[0] + "/Calculator/"+"/videos/thmb_" + timeInMillis + ".img");
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

                    Intent resultIntent = new Intent(getApplicationContext(), TextActivity.class);
                    setResult(StaticInfo.VideoActivityRequestCode, resultIntent);
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
}
