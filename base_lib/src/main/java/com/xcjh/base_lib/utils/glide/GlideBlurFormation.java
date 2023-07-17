package com.xcjh.base_lib.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * @author anti 2019.10.23
 */
public class GlideBlurFormation extends BitmapTransformation {

    private Context context;
    private int blurRadius;

    public GlideBlurFormation(Context context, int blurRadius) {
        this.context = context;
        this.blurRadius = blurRadius < 0 ? 0 : Math.min(blurRadius, 25);
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return BlurBitmapUtil.getInstance().blurBitmap(context, toTransform, this.blurRadius, outWidth, outHeight);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }

}
