package com.xcjh.base_lib.utils.glide;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

import java.security.MessageDigest;

/**
 * @author Administrator
 * 加载自带边框矩形图片
 */
public class RoundTransform implements Transformation<Bitmap> {


    /**
     * mCornerPos
     * 用一个整形表示哪些边角需要加圆角边框
     * 例如：0b1000,表示左上角需要加圆角边框
     * 0b1110 表示左上右上右下需要加圆角边框
     * 0b0000表示不加圆形边框
     */
    private final BitmapPool mBitmapPool;
    private final float mRadius; //圆角半径
    private final float mMargin = 0; //边距
    private final float mBorderWidth;//边框宽度
    private final int mBorderColor;//边框颜色
    private final int mCornerPos; //圆角位置


    @NonNull
    @Override
    public Resource<Bitmap> transform(@NonNull Context context, @NonNull Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = resource.get();

        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap bitmap = mBitmapPool.get(width, height, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);//新建一个空白的bitmap
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));//设置要绘制的图形

        Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//设置边框样式
        borderPaint.setColor(mBorderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(mBorderWidth);

        drawRoundRect(canvas, paint, width, height, borderPaint);
        return BitmapResource.obtain(bitmap, mBitmapPool);
    }

    public RoundTransform(Context context, int radius, Float borderWidth, int borderColor, int position) {
        mBitmapPool = Glide.get(context).getBitmapPool();
        mRadius = Resources.getSystem().getDisplayMetrics().density * radius;
        // mMargin = Resources.getSystem().getDisplayMetrics().density * margin;
        mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth;
        mBorderColor = borderColor;
        mCornerPos = position;
    }

    private void drawRoundRect(Canvas canvas, Paint paint, float width, float height, Paint borderPaint) {
        float right = width - mMargin;
        float bottom = height - mMargin;
        float halfBorder = mBorderWidth / 2;
        Path path = new Path();

        float[] pos = new float[8];
        int shift = mCornerPos;

        int index = 3;

        while (index >= 0) {//设置四个边角的弧度半径
            pos[2 * index + 1] = ((shift & 1) > 0) ? mRadius : 0;
            pos[2 * index] = ((shift & 1) > 0) ? mRadius : 0;
            shift = shift >> 1;
            index--;
        }


        path.addRoundRect(
                new RectF(
                        mMargin + halfBorder,
                        mMargin + halfBorder,
                        right - halfBorder,
                        bottom - halfBorder
                ),
                pos,
                Path.Direction.CW
        );
        //绘制要加载的图形
        canvas.drawPath(path, paint);
        //绘制边框
        if (mBorderWidth>0){
            canvas.drawPath(path, borderPaint);
        }

    }


    public String getId() {

        //这里一定要是设置一个独一无二的ID，要不然重用会导致第二次调用不起效果，最好加上相应的变量参数，保证唯一性
        return "RoundedTransformation(radius=" + mRadius + ", margin=" + mMargin + ", mBorderWidth" + mBorderWidth + ", mBorderColor" + mBorderColor + "mCornerPos" + mCornerPos + ")";
    }


    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}


