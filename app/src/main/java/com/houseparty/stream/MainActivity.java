package com.houseparty.stream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements LineStreamConnection.Listener,
        CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "MainActivity";

    private ListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private LineStreamConnection connection;
    private boolean isFiltered;
    private long lastJsonTimestamp = 0;
    private Timer updateTimer;
    private final List<Model.Item> batch = new ArrayList<Model.Item>(Config.BATCH_SIZE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ListAdapter();
        recyclerView.setAdapter(adapter);

        ((ToggleButton) findViewById(R.id.friends)).setOnCheckedChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkChangeReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        if (connection != null) {
            connection.close();
        }
        if (updateTimer != null) {
            updateTimer.cancel();
            updateTimer = null;
        }
        unregisterReceiver(networkChangeReceiver);
        super.onPause();
    }

    @Override
    public void onNewLine(String line) {
        final Model.Item item = Model.parseItem(line);
        if (item == null) {
            Log.w(TAG, String.format("Unable to parse %s", line));
            return;
        }
        lastJsonTimestamp = item.timestamp;
        if (!isFiltered || (isFiltered && item.areFriends)) {
            batch.add(item);
        }
        if (batch.size() >= Config.BATCH_SIZE) {
            Log.d(TAG, String.format("Batch size too big, updating list."));
            updateList();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        isFiltered = checked;
        adapter.clearNonFriends();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onInterrupted() {
        if (connection != null) {
            connection.close();
        }
        if (updateTimer != null) {
            updateTimer.cancel();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Connection interrupted",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    synchronized private void updateList() {
        final List<Model.Item> chunk = new ArrayList<Model.Item>(batch);
        batch.clear();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Model.Item item : chunk) {
                    adapter.addItem(item);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private final BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (isOnline() && (connection == null || !connection.isConnected())) {
                Toast.makeText(MainActivity.this, "Online! starting connection.",
                        Toast.LENGTH_LONG).show();
                startConnection(
                        lastJsonTimestamp > 0 ? lastJsonTimestamp : System.currentTimeMillis());
            }
        }
    };

    private void startConnection(long timestamp) {
        String url = Config.STREAM_URL_PREFIX + Long.toString(timestamp);
        connection = new LineStreamConnection(url, MainActivity.this);
        connection.start();
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, String.format("Starting update"));
                updateList();
            }
        }, 0, Config.UPDATE_MILLIS);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }
}
