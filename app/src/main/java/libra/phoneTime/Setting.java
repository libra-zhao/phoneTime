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
package libra.phoneTime;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;
import android.app.Activity;

import libra.phoneTime.exception.NonSetupException;

public class Setting {
    private final String TAG = this.getClass().getSimpleName();

    private static final String SP_NAME = "setting";
    private static Setting mInstance;
    private String mStartUpTime;
    private static boolean mStartUp;
    private SharedPreferences mSP;
    private Editor mEditor;

    public static Setting getInstance() throws NonSetupException {
        if (mInstance == null) {
            throw new NonSetupException();
        }
        return mInstance;
    }

    public static void setup(Context context) {
        if (mInstance == null) {
            mInstance = new Setting(context);
        }
    }

    public static void startUp() {
        mStartUp = true;
    }

    public static boolean isStartUp() {
        return mStartUp;
    }

    private Setting(Context context) {
        mSP = context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE);
        mEditor = mSP.edit();
    }

    private void _write(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    private long _readLong(String key, long def) {
       return mSP.getLong(key, def);
    }
}
