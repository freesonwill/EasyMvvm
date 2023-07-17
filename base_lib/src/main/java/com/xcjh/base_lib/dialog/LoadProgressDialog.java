package com.xcjh.base_lib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.xcjh.base_lib.R;


/**
 * @author anti
 * 2020.08.07
 * TODO 2022.11.23 修改为 gif 动画
 */
public class LoadProgressDialog extends Dialog {

    private Context context;
    private String title = "";

    public LoadProgressDialog(Context context) {
        super(context, R.style.loadDialogStyle);
        this.context = context;
        initView();
      //  initView2();
    }

    public LoadProgressDialog(Context context, String title) {
        super(context, R.style.loadDialogStyle);
        this.context = context;
        this.title = title;
        initView();
       // initView2();
    }

    public LoadProgressDialog(Context context, boolean cancelable) {
        super(context, R.style.loadDialogStyle);
        this.context = context;
        initView();
       // initView2();
        setCancelable(cancelable);
        setCanceledOnTouchOutside(cancelable);
    }

    private void initView() {
        setContentView(R.layout.loadingbar);
        if (!TextUtils.isEmpty(title)) {
            TextView titleTxt = findViewById(R.id.text);
            titleTxt.setText(title);
        }
    }

    private void initView2() {
        setContentView(R.layout.loadingbargif);
        ImageView imageView = (ImageView) findViewById(R.id.loadImgView);
        Glide.with(context).asGif().load(R.drawable.google_add_loading).into(imageView);
        // 停止gif 动画
        setOnDismissListener(dialogInterface -> {
            stopGif(imageView);
        });
        // 停止gif 动画
        setOnCancelListener(dialogInterface -> {
            stopGif(imageView);
        });
        // 启动 gif 动画
        setOnShowListener(dialogInterface -> {
            showGif(imageView);
        });
    }

    private void stopGif(ImageView imageView) {
        if (imageView != null) {
            if (imageView.getDrawable() instanceof GifDrawable) {
                GifDrawable drawable = (GifDrawable)imageView.getDrawable();
                if (drawable != null && drawable.isRunning()) {
                    drawable.stop();
                }
            }
        }
    }

    private void showGif(ImageView imageView) {
        if (imageView != null) {
            if (imageView.getDrawable() instanceof GifDrawable) {
                GifDrawable drawable = (GifDrawable)imageView.getDrawable();
                if (drawable != null && !drawable.isRunning()) {
                    drawable.start();
                }
            }
        }
    }

}
