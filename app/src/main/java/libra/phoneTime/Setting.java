package libra.phoneTime;

/**
 * Created by Libra Zhao on 2017/2/11.
 */

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
