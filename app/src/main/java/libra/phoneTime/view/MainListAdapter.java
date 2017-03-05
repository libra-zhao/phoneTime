package libra.phoneTime.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import libra.phoneTime.R;

/**
 * Created by Libra Zhao on 2017/3/4.
 */

public class MainListAdapter implements ListAdapter {
    private Context mContext;

    public MainListAdapter(Context context) {
        mContext = context;
    }

    public int getCount() {
//        try {
//            return Database.getDayCount();
//        } catch (NonSetupException e) {
//            return 1;
//        }
        return 1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView text;
        ProgressView progress;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null);
        }

        text = (TextView)convertView.findViewById(R.id.text);
       // progress = (ProgressView) convertView.findViewById(R.id.progress);

        text.setText("dddddd");

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
