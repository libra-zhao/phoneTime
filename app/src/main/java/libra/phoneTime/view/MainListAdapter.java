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
package libra.phoneTime.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.Calendar;

import libra.phoneTime.R;
import libra.phoneTime.db.Database;
import libra.phoneTime.exception.NonSetupException;

public class MainListAdapter implements ListAdapter {
    private Context mContext;

    public MainListAdapter(Context context) {
        mContext = context;
    }

    public int getCount() {
        try {
            return Database.getDayCount();
        } catch (NonSetupException e) {
            return 1;
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView text;
        ProgressView progress;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null);
        }

        text = (TextView)convertView.findViewById(R.id.text);
        progress = (ProgressView) convertView.findViewById(R.id.progress);

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -position);
            text.setText(String.valueOf(Database.getSeconds(calendar)));
        } catch (Exception e) {
            e.printStackTrace();
            text.setText("");
        }

        return convertView;
    }
    public int getItemViewType(int position) {
        return 0;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public boolean isEmpty() {
        return getCount() == 0 ? true : false;
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public boolean isEnabled(int position) {
        return true;
    }

    public void registerDataSetObserver(DataSetObserver observer) {
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public boolean hasStableIds() {
        return false;
    }
}
