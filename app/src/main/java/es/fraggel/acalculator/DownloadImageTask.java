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

    public DownloadImageTask(ImageView bmImage, Context context) {
        this.bmImage = bmImage;
        ctx=context;
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
        bmImage.setImageBitmap(result);
    }
}
