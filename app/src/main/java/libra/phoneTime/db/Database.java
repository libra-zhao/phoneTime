package libra.phoneTime.db;

/**
 * Created by Libra Zhao on 2017/2/20.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import libra.phoneTime.exception.NonSetupException;

public class Database extends Object {
    private static final String DB_NAME = "main.db";
    private static final String DB_TABLE = "main";
    private static final String DB_TABLE_SETTING = "setting";
    private static final String DB_COL_INDEX = "index";
    private static final String DB_COL_SCREEN = "screen";
    private static final String DB_COL_MS = "ms";
    private static final String DB_COL_MASK = "mask";
    private static final String DB_COL_KEY = "key";
    private static final String DB_COL_VALUE = "value";
    private static final String SETTING_KEY_INSTALL_DATE = "install_date";
    private static Database mInstance;
    private final String TAG = this.getClass().getSimpleName();
    private final Object mDatabaseSync;
    private SQLiteDatabase mDatabase;
    private ArrayList<ScreenEvent> mDataCache;

    private Database(Context context) {
        mDatabaseSync = new Object();
        _init_db(context);
    }

    public static Database getInstance() throws NonSetupException {
        if (mInstance == null) {
            throw new NonSetupException();
        }
        return mInstance;
    }

    public static void setup(Context context) {
        if (mInstance == null) {
            mInstance = new Database(context);
        }
    }

    public static void screenOn() {
        screenOnOff(ScreenEvent.SCREEN_ON, 0);
    }

    public static void screenOff() {
        screenOnOff(ScreenEvent.SCREEN_OFF, 0);
    }

    public static void powerOn() {
        screenOnOff(ScreenEvent.SCREEN_ON, ScreenEvent.MASK_POWERON);
    }

    public static void powerOff() {
        screenOnOff(ScreenEvent.SCREEN_OFF, ScreenEvent.MASK_POWEROFF);
    }

    public static void screenOnOff(int screen, long mask) {
        try {
            Database database = getInstance();
            database._add_db(screen, System.currentTimeMillis(), mask);
        } catch (NonSetupException e) {
            e.printStackTrace();
        }
    }

    public static long getSeconds() throws NonSetupException {
        Database database = getInstance();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        return getSeconds(database._readList(year, month, day));
    }

    public static long getSeconds(ArrayList<ScreenEvent> list) {
        long lastOn = -1;
        long result = 0;

        if (list.isEmpty()) {
            return 0;
        }

        if (list.get(0).screen == ScreenEvent.SCREEN_OFF) {
            // over night
            lastOn = new Date().getTime();
        }

        for (ScreenEvent event : list) {
            if (event.screen == ScreenEvent.SCREEN_OFF) {
                if (lastOn != -1) {
                    result += event.ms - lastOn;
                }
            } else {
                lastOn = event.ms;
            }
        }

        return result;
    }

    public static int getDayCount() throws NonSetupException {
        Database database = getInstance();
        long installDate = database._readSettingLong(SETTING_KEY_INSTALL_DATE);
        Calendar iDay = Calendar.getInstance();
        iDay.setTimeInMillis(installDate);
        Calendar today = Calendar.getInstance();
        long ms = today.getTimeInMillis() - installDate;
        int day = (int)(ms / (1000 * 3600 * 24)) + 1;
        if (_getMsOfDay(today) < _getMsOfDay(iDay)) {
            day++;
        }
        return day;
    }

    private long _readSettingLong(String key) {
        String value = _readSetting(key);
        return Long.decode(value);
    }

    private String _readSetting(String key) {
        synchronized (mDatabaseSync) {
            String exec = "SELECT " + DB_COL_VALUE + " from " + DB_TABLE_SETTING + " where "
                    + DB_COL_KEY + " = " + key;
            Cursor c = mDatabase.rawQuery(exec, null);
            c.moveToFirst();
            return c.getString(c.getColumnIndex(DB_COL_VALUE));
        }
    }

    private ArrayList<ScreenEvent> _readList(int year, int month, int day) {
        ArrayList<ScreenEvent> list;
        ArrayList<ScreenEvent> result = new ArrayList<>();

        list = _read_db();
        for (ScreenEvent screenEvent : list) {
            if (screenEvent.isDayOf(year, month, day)) {
                result.add(screenEvent);
            }
        }

        return result;
    }

    private void _init_db(Context context) {
        String exec;

        if (context.getDatabasePath(DB_NAME) == null) {
            mDatabase = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
            exec = "CREATE TABLE" + DB_TABLE + "(" + DB_COL_INDEX + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
            exec += DB_COL_SCREEN + " INTEGER, ";
            exec += DB_COL_MS + " INTEGER, ";
            exec += DB_COL_MASK + " INTEGER)";
            mDatabase.execSQL(exec);

            exec = "CREATE TABLE" + DB_TABLE_SETTING + "(" + DB_COL_INDEX + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
            exec += DB_COL_KEY + " STRING, ";
            exec += DB_COL_VALUE + " STRING)";
            mDatabase.execSQL(exec);

            exec = "INSERT INTO " + DB_TABLE_SETTING + " VALUES (NULL, ?, ?)";
            mDatabase.execSQL(exec, new Object[]{SETTING_KEY_INSTALL_DATE, String.valueOf(new Date().getTime())});
        } else {
            mDatabase = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        }
    }

    private void _add_db(int screen, long ms, long mask) {
        synchronized (mDatabaseSync) {
            mDatabase.execSQL("INSERT INTO " + DB_TABLE + " VALUES (NULL, ?, ?, ?)",
                    new Object[]{screen, ms, mask});
            mDataCache = null;
        }
    }

    private ArrayList<ScreenEvent> _read_db() {
        synchronized (mDatabaseSync) {
            if (mDataCache != null) {
                return mDataCache;
            }

            mDataCache = new ArrayList<>();
            Cursor c = mDatabase.rawQuery("SELECT * FROM " + DB_TABLE, null);
            while (c.moveToNext()) {
                int screen = c.getInt(c.getColumnIndex(DB_COL_SCREEN));
                long ms = c.getLong(c.getColumnIndex(DB_COL_MS));
                long mask = c.getLong(c.getColumnIndex(DB_COL_MASK));
                ScreenEvent screenEvent = new ScreenEvent(screen, ms, mask);
                mDataCache.add(screenEvent);
            }
            c.close();
        }

        return mDataCache;
    }

    private static long _getMsOfDay(Calendar calendar) {
        long ms = calendar.get(Calendar.HOUR_OF_DAY);
        ms = ms * 60 + calendar.get(Calendar.MINUTE);
        ms = ms * 60 + calendar.get(Calendar.SECOND);
        ms = ms * 1000 + calendar.get(Calendar.MILLISECOND);
        return ms;
    }
}
