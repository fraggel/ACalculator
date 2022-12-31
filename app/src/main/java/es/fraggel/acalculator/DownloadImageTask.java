package es.fraggel.acalculator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    Context ctx=null;
    boolean playing=false;
    boolean video=false;

    public DownloadImageTask(ImageView bmImage, Context context,boolean vid) {
        this.bmImage = bmImage;
        ctx=context;
        video=vid;
    }
    public boolean getPlaying(){
        return playing;
    }
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(20);
            paint.setColor(Color.RED);
            paint.setTextAlign(Paint.Align.LEFT);
            float baseline = -paint.ascent(); // ascent() is negative
            int width = (int) (paint.measureText("ERROR") + 0.5f); // round
            int height = (int) (baseline + paint.descent() + 0.5f);
            Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(image);
            canvas.drawText("ERROR", 0, baseline, paint);
            mIcon11 = image;
            bmImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
                //imgView.setScaleType(ImageView.ScaleType.FIT_XY);
            });
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if(video) {
            Bitmap mutable=result.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap watermarkBitmap = null;
            watermarkBitmap = BitmapFactory.decodeResource(ctx.getResources(), hani.momanii.supernova_emoji_library.R.drawable.emoji_25b6);
// Creating a canvas with mainBitmap
            Canvas canvas = new Canvas(mutable);
// The actual watermarking
            int x = mutable.getWidth() / 2 - (watermarkBitmap.getWidth() / 2);
            int position;
            int y =  position = mutable.getHeight() / 2 - (watermarkBitmap.getHeight() / 2);
            canvas.drawBitmap(watermarkBitmap, x, y, null);
            result=mutable;
        }
        bmImage.setImageBitmap(result);
        playing=true;
    }
}
