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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.content.IntentFilter;

import libra.phoneTime.db.Database;

public class MainService extends Service {
    private final String TAG = this.getClass().getSimpleName();

    private ScreenBroadcastReceiver mScreenReceiver;
    private ShutdownBroadcastReceiver mShutdownReceiver;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "in onCreate");

        Database.setup(this);
        Database.powerOn();

        mScreenReceiver = new ScreenBroadcastReceiver();
        IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenReceiver, screenFilter);

        mShutdownReceiver = new ShutdownBroadcastReceiver();
        IntentFilter shutdownFilter = new IntentFilter();
        shutdownFilter.addAction(Intent.ACTION_SHUTDOWN);
        registerReceiver(mShutdownReceiver, shutdownFilter);
    }
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "in onDestroy");

        unregisterReceiver(mScreenReceiver);
        unregisterReceiver(mShutdownReceiver);

        /* this method wasn't invoked while power off, so invoke Database.screenOff() in shutdown
         * receiver */
    }

    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private final String TAG = this.getClass().getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Screen: " + intent.getAction());
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Database.screenOn();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Database.screenOff();
            }
        }
    }

    private class ShutdownBroadcastReceiver extends BroadcastReceiver {
        private final String TAG = this.getClass().getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Shutdown: " + intent.getAction());
            Database.powerOff();
        }
    }
}
