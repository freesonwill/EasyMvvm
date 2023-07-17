package com.xcjh.tpInfo.view.range_calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.core.content.ContextCompat;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.RangeMonthView;
import com.xcjh.tpInfo.R;

/**
 * 范围选择月视图
 *
 * @author huanghaibin
 * @date 2018/9/13
 */

public class CustomRangeMonthView extends RangeMonthView {

    private int mRadius;
    /**
     * 被选择的起始位置日期背景色
     */
    protected Paint mSelectedStartEndPaint = new Paint();
    /**
     * 被选择的起始位置日期文字色
     */
    protected Paint mSelectTextStartEndPaint = new Paint();

    public CustomRangeMonthView(Context context) {
        super(context);
        mSelectedStartEndPaint.setAntiAlias(true);
        mSelectedStartEndPaint.setStrokeWidth(2);
        mSelectedStartEndPaint.setStyle(Paint.Style.FILL);
        mSelectedStartEndPaint.setColor( ContextCompat.getColor(context, R.color.c_6940f5));

        mSelectTextStartEndPaint.setAntiAlias(true);
        mSelectTextStartEndPaint.setStyle(Paint.Style.FILL);
        mSelectTextStartEndPaint.setTextAlign(Paint.Align.CENTER);
        mSelectTextStartEndPaint.setColor(0xffffffff);
        mSelectTextStartEndPaint.setFakeBoldText(true);
        mSelectTextStartEndPaint.setTextSize(dipToPx(context, 12));
    }
    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2;
        mSchemePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme,
                                     boolean isSelectedPre, boolean isSelectedNext) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        int xx = mItemWidth / 2 - mItemHeight / 2;
        float baselineY = mTextBaseLine + y;
        if (isSelectedPre) {
            if (isSelectedNext) {
                canvas.drawRect(x, y, x + mItemWidth, y + mItemHeight, mSelectedPaint);
            } else {//最后一个，the last
                canvas.drawRect(x, y, cx, y + mItemHeight, mSelectedPaint);
              //  canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
                canvas.drawRoundRect(new RectF(x+xx, y,x+mItemWidth-xx, y+mItemHeight), 12, 12, mSelectedStartEndPaint);
             /*   canvas.drawText(String.valueOf(calendar.getDay()),
                        cx,
                        baselineY,
                        mSelectTextStartEndPaint);*/
            }
        } else {
            if(isSelectedNext){
                canvas.drawRect(cx, y, x + mItemWidth, y + mItemHeight, mSelectedPaint);
            }
            canvas.drawRoundRect(new RectF(x+xx, y,x+mItemWidth-xx, y+mItemHeight), 12, 12, mSelectedStartEndPaint);
            //canvas.drawRect(x, y,x+mItemWidth, y+mItemHeight, mSelectedPaint);
            //
        }

        return false;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y, boolean isSelected) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
       /// canvas.drawCircle(cx, cy, mRadius, mSchemePaint);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine + y;
        int cx = x + mItemWidth / 2;

        boolean isInRange = isInRange(calendar);
        boolean isEnable = !onCalendarIntercept(calendar);

        if (isSelected) {
            boolean isSelectedPre= isSelectPreCalendar(calendar, getSelectedIndex(calendar));
            boolean  isSelectedNext = isSelectNextCalendar(calendar, getSelectedIndex(calendar));
            if (isSelectedPre) {
                if (isSelectedNext) {
                    canvas.drawText(String.valueOf(calendar.getDay()),
                            cx,
                            baselineY,
                            mSelectTextPaint);
                } else {//最后一个，the last
                    canvas.drawText(String.valueOf(calendar.getDay()),
                            cx,
                            baselineY,
                            mSelectTextStartEndPaint);
                }
            } else {
                if(isSelectedNext){
                    canvas.drawText(String.valueOf(calendar.getDay()),
                            cx,
                            baselineY,
                            mSelectTextPaint);
                }
                canvas.drawText(String.valueOf(calendar.getDay()),
                        cx,
                        baselineY,
                        mSelectTextStartEndPaint);
            }
           // Log.e("====", "onDrawText: ===="+ isSelectPreCalendar(calendar, getSelectedIndex(calendar))+"==="+isSelectNextCalendar(calendar, getSelectedIndex(calendar)) );
           /* canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    mSelectTextPaint);*/
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() && isInRange && isEnable? mSchemeTextPaint : mOtherMonthTextPaint);
        } else {
            /// 未选择的普通日期
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() && isInRange && isEnable? mCurMonthTextPaint : mOtherMonthTextPaint);
        }
    }
}
