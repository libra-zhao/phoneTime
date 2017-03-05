package libra.phoneTime;

/**
 * Created by Libra Zhao on 2017/1/15.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Setting.isStartUp()) {
            Log.i(TAG, "Boot: " + intent.getAction());
            return;
        }

        Setting.startUp();
        Intent service = new Intent(context, MainService.class);
        context.startService(service);

        Log.i(TAG, "Service launched by intent: " + intent.getAction());
    }
}
