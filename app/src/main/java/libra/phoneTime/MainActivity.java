package libra.phoneTime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import libra.phoneTime.view.MainListAdapter;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private MainListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.list);
            mAdapter = new MainListAdapter(this);
            mListView.setAdapter(mAdapter);
        }
    }
}
