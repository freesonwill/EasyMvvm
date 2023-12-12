package com.xcjh.app.view.balldetail;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.xcjh.app.R;

/**
 * 比赛详情中的播放界面
 * @author Administrator
 */
public class MatchVideoPlayer extends StandardGSYVideoPlayer {

    private Context mContext;

    //数据源
    private int mSourcePosition = 0;
    private ImageView mCoverImage;
    public MatchVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public MatchVideoPlayer(Context context) {
        super(context);
    }

    public MatchVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mContext = context;
        initData();
    }

    private void initData() {
        Debuger.disable();
        setViewShowState(mTitleTextView, GONE);
        setViewShowState(mBackButton, GONE);
        setViewShowState(mLockScreen, GONE);
        //setThumbImageView(findViewById(R.id.ThumbImageView));
        mCoverImage = (ImageView) findViewById(R.id.thumbImage);
        setViewShowState(mThumbImageViewLayout, VISIBLE);
        setViewShowState(mLoadingProgressBar, VISIBLE);
        setViewShowState(mFullscreenButton, GONE);

   /*     OrientationUtils orientationUtils = new OrientationUtils((Activity) mContext, this, getOrientationOption());
        getFullscreenButton().setOnClickListener(view -> {
                  //  GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
                    if (orientationUtils.getIsLand() != 1) {
                        //直接横屏
                        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
                        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
                        orientationUtils.resolveByClick();
                    }
                    startWindowFullscreen(mContext, false, false);
                }
        );*/

    }

    /**
     *  设置播放URL
     *
     */
    public boolean setUp(String url) {
        //全屏裁减显示
       // GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
       // GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_16_9);
        Glide.with(this).load(url).into(mCoverImage);
        return setUp(url, false, "");
    }



    @Override
    public int getLayoutId() {
        return R.layout.match_video_player;
    }


    @Override
    protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
        mChangePosition = false;
        mChangeVolume = false;
        mBrightness = false;
    }

}
