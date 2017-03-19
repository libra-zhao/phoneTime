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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.io.File;

import libra.phoneTime.exception.NonSetupException;
import libra.phoneTime.lib.ZeroTime;

public class Database extends Object {

    private static final String DB_NAME = "main.db";
    private static final String DB_TABLE = "main";
    private static final String DB_TABLE_SETTING = "setting";
    private static final String DB_COL_ID = "id";
    private static final String DB_COL_SCREEN = "screen";
    private static final String DB_COL_MS = "ms";
    private static final String DB_COL_MASK = "mask";
    private static final String DB_COL_KEY = "key";
    private static final String DB_COL_VALUE = "value";
    private static final String SETTING_KEY_INSTALL_DATE = "install_date";
    private static Database mInstance;
    private final Object mDatabaseSync;
    private SQLiteDatabase mDatabase;
    private ArrayList<ScreenEvent> mDataCache;

    private Database(Context context) {
        mDatabaseSync = new Object();
        _initDB(context);
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

    private static void screenOnOff(int screen, long mask) {
        try {
            Database db = getInstance();
            db._addDB(screen, System.currentTimeMillis(), mask);
        } catch (NonSetupException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ScreenOn> getList() throws NonSetupException {
        return getList(Calendar.getInstance());
    }

    public static ArrayList<ScreenOn> getList(Calendar calendar) throws NonSetupException {
        Database db = getInstance();
        return db._readScreenOns(calendar);
    }

    public static long getSeconds() throws NonSetupException {
        return getSeconds(Calendar.getInstance());
    }

    public static long getSeconds(Calendar calendar) throws NonSetupException {
        Database db = getInstance();
        ArrayList<ScreenOn> list = db._readScreenOns(calendar);
        long result = 0;

        for (ScreenOn so : list) {
            result += so.getTimeSecs();
        }

        return result;
    }

    public static int getDayCount() throws NonSetupException {
        Database db = getInstance();
        long installDate = db._readSettingLong(SETTING_KEY_INSTALL_DATE);
        Calendar iDay = Calendar.getInstance();
        iDay.setTimeInMillis(installDate);
        Calendar today = Calendar.getInstance();
        long ms = today.getTimeInMillis() - installDate;
        int day = (int) (ms / (1000 * 3600 * 24)) + 1;
        if (_getMsOfDay(today) < _getMsOfDay(iDay)) {
            day++;
        }
        return day;
    }

    private ArrayList<ScreenOn> _readScreenOns(Calendar calendar) {
        ArrayList<ScreenEvent> events = _readEvents(calendar);
        ArrayList<ScreenOn> result = new ArrayList<>();
        Calendar lastOn = null;

        if (events.isEmpty()) {
            return result;
        }

        if (events.get(0).screen == ScreenEvent.SCREEN_OFF) {
            // over night
            lastOn = ZeroTime.init(Calendar.getInstance());
        }

        for (ScreenEvent event : events) {
            if (event.screen == ScreenEvent.SCREEN_OFF) {
                if (lastOn != null) {
                    result.add(new ScreenOn(lastOn, event.getCalendar()));
                    lastOn = null;
                }
            } else {
                lastOn = event.getCalendar();
            }
        }

        if (lastOn != null) {
            if (_isToday(calendar)) {
                result.add(new ScreenOn(lastOn, Calendar.getInstance()));
            }
            else {
                result.add(new ScreenOn(lastOn, _getEndTimeOfDay(lastOn)));
            }
        }

        return result;
    }

    private ArrayList<ScreenEvent> _readEvents(Calendar calendar) {
        ArrayList<ScreenEvent> list;
        ArrayList<ScreenEvent> result = new ArrayList<>();

        list = _readDB();
        for (ScreenEvent screenEvent : list) {
            if (screenEvent.isDayOf(calendar)) {
                result.add(screenEvent);
            }
        }

        return result;
    }

    private long _readSettingLong(String key) {
        String value = _readSetting(key);
        return Long.decode(value);
    }

    private String _readSetting(String key) {
        String result;
        synchronized (mDatabaseSync) {
            String exec = "SELECT " + DB_COL_VALUE + " from " + DB_TABLE_SETTING + " where "
                    + DB_COL_KEY + " = \"" + key + "\"";
            Cursor c = mDatabase.rawQuery(exec, null);
            c.moveToFirst();
            result = c.getString(c.getColumnIndex(DB_COL_VALUE));
            c.close();
        }
        return result;
    }

    private void _initDB(Context context) {
        String exec, dbFilePath;
        File dbFile, dbFolder;
        boolean createTable = false;

        dbFolder = context.getExternalFilesDir(null);
        if (dbFolder != null && dbFolder.exists()) {
            dbFilePath = dbFolder.getAbsolutePath() + File.separator + DB_NAME;
            dbFile = new File(dbFilePath);
            if (!dbFile.exists()) {
                createTable = true;
            }
            mDatabase = SQLiteDatabase.openOrCreateDatabase(dbFilePath, null);
        } else {
            // getExternalFilesDir() doesn't work at some phones and some times.
            if (context.getDatabasePath(DB_NAME) == null) {
                createTable = true;
            }
            mDatabase = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        }

        if (createTable) {
            exec = "CREATE TABLE " + DB_TABLE + "(" + DB_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
            exec += DB_COL_SCREEN + " INTEGER, ";
            exec += DB_COL_MS + " INTEGER, ";
            exec += DB_COL_MASK + " INTEGER)";
            mDatabase.execSQL(exec);

            exec = "CREATE TABLE " + DB_TABLE_SETTING + "(" + DB_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
            exec += DB_COL_KEY + " STRING, ";
            exec += DB_COL_VALUE + " STRING)";
            mDatabase.execSQL(exec);

            exec = "INSERT INTO " + DB_TABLE_SETTING + " VALUES (NULL, ?, ?)";
            mDatabase.execSQL(exec, new Object[]{SETTING_KEY_INSTALL_DATE, String.valueOf(new Date().getTime())});
        }
    }

    private void _addDB(int screen, long ms, long mask) {
        synchronized (mDatabaseSync) {
            mDatabase.execSQL("INSERT INTO " + DB_TABLE + " VALUES (NULL, ?, ?, ?)",
                    new Object[]{screen, ms, mask});
            mDataCache = null;
        }
    }

    private ArrayList<ScreenEvent> _readDB() {
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

    private static boolean _isToday(Calendar calendar) {
        Calendar now = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
            return true;
        } else {
            return false;
        }
    }

    private static Calendar _getEndTimeOfDay(Calendar calendar) {
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(calendar.getTimeInMillis());
        result.set(Calendar.HOUR, 23);
        result.set(Calendar.MINUTE, 59);
        result.set(Calendar.SECOND, 59);
        result.set(Calendar.MILLISECOND, 999);
        return result;
    }
}
