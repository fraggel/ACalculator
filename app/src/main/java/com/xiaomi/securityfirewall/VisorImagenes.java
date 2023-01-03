package com.xiaomi.securityfirewall;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.Touch;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

public class VisorImagenes extends AppCompatActivity {
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ImageView mImageView;
    boolean onpause=false;
    String value =null;
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        try {
            mScaleGestureDetector.onTouchEvent(motionEvent);
        }catch(Exception e){}
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visor_imagenes);
        getSupportActionBar().hide();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
                String value = extras.getString("key");
                TouchImageView mImageView=(TouchImageView)findViewById(R.id.imageView);
                mImageView.setImageURI(Uri.parse(value));
        }
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            mImageView.setScaleX(mScaleFactor);
            mImageView.setScaleY(mScaleFactor);
            return true;
        }
    }
    public void btn_Click(View view) {

            mImageView=(TouchImageView)findViewById(R.id.imageView);
            float rotacion=mImageView.getRotation()+90;
            if(rotacion==360){
                rotacion=0;
            }
        BitmapDrawable drawable = (BitmapDrawable)mImageView.getDrawable();
        Bitmap myImg = drawable.getBitmap();

        Matrix matrix = new Matrix();
        matrix.postRotate(rotacion);
        Bitmap rotated = Bitmap.createBitmap(myImg, 0, 0, myImg.getWidth(), myImg.getHeight(),
                matrix, true);

        mImageView.setImageBitmap(rotated);


    }

    @Override
    protected void onPause() {
        /*TouchImageView mImageView=(TouchImageView)findViewById(R.id.imageView);
        mImageView.setVisibility(View.INVISIBLE);*/
        super.onPause();
    }

    @Override
    protected void onStop() {
        TouchImageView mImageView=(TouchImageView)findViewById(R.id.imageView);
        mImageView.setVisibility(View.INVISIBLE);
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        TouchImageView mImageView=(TouchImageView)findViewById(R.id.imageView);
        mImageView.setVisibility(View.INVISIBLE);
        super.onDestroy();
        finish();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        TouchImageView mImageView=(TouchImageView)findViewById(R.id.imageView);
        mImageView.setVisibility(View.VISIBLE);
        super.onRestoreInstanceState(savedInstanceState, persistentState);

    }
}
