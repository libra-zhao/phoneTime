/*
 * Copyright (C) 2017 by Libra Zhao <libra.zhao@foxmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libra.phoneTime.db;

import java.util.Calendar;

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

    public boolean isDayOf(Calendar calendar) {
        if (mCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                mCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                mCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }
        else {
            return false;
        }
    }
}
