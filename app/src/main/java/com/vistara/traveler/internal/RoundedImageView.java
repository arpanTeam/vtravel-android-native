package com.vistara.traveler.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {

    public RoundedImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        Bitmap output = null;
        try {
            if (bmp.getWidth() != radius || bmp.getHeight() != radius)
                sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
            else
                sbmp = bmp;
            output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
                    Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xffa19774;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.parseColor("#BAB399"));
            canvas.drawCircle(sbmp.getWidth() / 2 + 0.1f,
                    sbmp.getHeight() / 2 + 0.1f, sbmp.getWidth() / 2 + 0.1f, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(sbmp, rect, rect, paint);
        }catch(Exception e)
        {
            int a =0;
        }
        return output;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        try {
            Drawable drawable = getDrawable();

            if (drawable == null) {
                return;
            }

            if (getWidth() == 0 || getHeight() == 0) {
                return;
            }
            Bitmap b = ((BitmapDrawable) drawable).getBitmap();
            if (b != null) {
                Bitmap bitmap = b.copy(Config.ARGB_8888, true);

                int w = getWidth(), h = getHeight();

                Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
                canvas.drawBitmap(roundBitmap, 0, 0, null);
            }
        }catch(Exception e)
        {
            int a =0;
        }

    }

}