package libra.phoneTime;

/**
 * Created by Libra Zhao on 2017/1/15.
 */

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
