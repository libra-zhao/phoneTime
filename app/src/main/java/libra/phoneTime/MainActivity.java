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

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import libra.phoneTime.db.Database;
import libra.phoneTime.view.MainListAdapter;
import libra.phoneTime.view.SummaryView;

public class MainActivity extends AppCompatActivity {
    private static final int DEF_TIMER_PERIOD_MS = 1000;
    private SummaryView mSummaryView;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.list);
        MainListAdapter adapter = new MainListAdapter(this);
        listView.setAdapter(adapter);

        mSummaryView = (SummaryView) findViewById(R.id.summary);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    long secs = Database.getSeconds();
                    mSummaryView.setText(String.valueOf(secs));
                } catch (Exception e) {
                }
            }
        };
        mTimer = new Timer();
        mTimer.schedule(task, 0, DEF_TIMER_PERIOD_MS);

        Intent service = new Intent(this, MainService.class);
        ComponentName comp = startService(service);
        if (comp == null) {
            Toast.makeText(this, R.string.warn_service_startup, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mTimer.purge();
    }
}
