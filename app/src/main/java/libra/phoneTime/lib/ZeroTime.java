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
package libra.phoneTime.lib;

import java.util.Calendar;

public class ZeroTime {
    private Calendar mCalendar;

    public ZeroTime() {
        mCalendar = init(Calendar.getInstance());
    }

    public ZeroTime(Calendar calendar) {
        // we can not directly modify the input instance.
        calendar = (Calendar)calendar.clone();
        mCalendar = init(calendar);
    }

    public static Calendar init(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public void adjust() {
        mCalendar = init(Calendar.getInstance());
    }

    public long getTimeInMillis() {
        return mCalendar.getTimeInMillis();
    }
}
