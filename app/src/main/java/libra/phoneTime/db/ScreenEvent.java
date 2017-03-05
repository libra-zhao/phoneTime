package libra.phoneTime.db;

import java.util.Calendar;

/**
 * Created by Libra Zhao on 2017/2/20.
 */

public class ScreenEvent extends Object {
    public static final int SCREEN_ON = 1;
    public static final int SCREEN_OFF = 2;
    public static final long MASK_POWERON = 1;
    public static final long MASK_POWEROFF = 2;

    public final int screen;
    public final long ms;
    public final long mask;
    private Calendar mCalendar;

    public ScreenEvent(int screen, long ms) {
        this(screen, ms, 0);
    }

    public ScreenEvent(int screen, long ms, long mask) {
        this.screen = screen;
        this.ms = ms;
        this.mask = mask;

        mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(ms);
    }

    public boolean isDayOf(int year, int month, int day) {
        if (mCalendar.get(Calendar.YEAR) == year && mCalendar.get(Calendar.MONTH) == month &&
                mCalendar.get(Calendar.DAY_OF_MONTH) == day) {
            return true;
        }
        else {
            return false;
        }
    }
}
