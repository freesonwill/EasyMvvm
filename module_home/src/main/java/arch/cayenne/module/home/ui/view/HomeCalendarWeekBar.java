package arch.cayenne.module.home.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.WeekBar;

import arch.cayenne.module.home.R;

/**
 * @author: ricky.chang
 * @date: 2025/6/24 下午3:46
 * @description:
 */
public class HomeCalendarWeekBar extends WeekBar {

    private int mPreSelectedIndex;

    public HomeCalendarWeekBar(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.home_calendar_week_bar, this, true);
    }

    @Override
    protected void onDateSelected(Calendar calendar, int weekStart, boolean isClick) {
        getChildAt(mPreSelectedIndex).setSelected(false);
        int viewIndex = getViewIndexByCalendar(calendar, weekStart);
        getChildAt(viewIndex).setSelected(true);
        mPreSelectedIndex = viewIndex;
    }

    /**
     * 当周起始发生变化，使用自定义布局需要重写这个方法，避免出问题
     *
     * @param weekStart 周起始
     */
    @Override
    protected void onWeekStartChange(int weekStart) {
        for (int i = 0; i < getChildCount(); i++) {
            ((TextView) getChildAt(i)).setText(getWeekString(i, weekStart));
        }
    }

    /**
     * 或者周文本，这个方法仅供父类使用
     * @param index index
     * @param weekStart weekStart
     * @return 或者周文本
     */
    private String getWeekString(int index, int weekStart) {
        String[] weeks = getContext().getResources().getStringArray(R.array.chinese_week_string_array);

        if (weekStart == 1) {
            return weeks[index];
        }
        if (weekStart == 2) {
            return weeks[index == 6 ? 0 : index + 1];
        }
        return weeks[index == 0 ? 6 : index - 1];
    }
}
