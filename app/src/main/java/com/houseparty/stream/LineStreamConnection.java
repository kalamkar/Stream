package com.houseparty.stream;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhi on 8/22/17.
 */

public class LineStreamConnection extends Thread {
    private static final String TAG = "LineStreamConnection";

    public interface Listener {
        void onNewLine(String line);
        void onInterrupted();
    }

    private final String urlString;
    private final Listener listener;

    private boolean isConnected = false;

    public LineStreamConnection(String urlString, Listener listener) {
        this.urlString = urlString;
        this.listener = listener;
    }

    @Override
    public void run() {
        BufferedReader reader;
        HttpURLConnection urlConnection;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inStream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inStream));
            isConnected = true;
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Invalid URL", ex);
            return;
        } catch (IOException ex) {
            Log.e(TAG, "Unable to get input stream", ex);
            return;
        }
        while(isConnected) {
            try {
                String line = reader.readLine();
                listener.onNewLine(line);
            } catch (IOException ex) {
                Log.e(TAG, "Unable to read", ex);
                listener.onInterrupted();
                break;
            }
        }
        try {
            reader.close();
            urlConnection.disconnect();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to close reader", ex);
        }
        close();
    }

    public void close() {
        isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
