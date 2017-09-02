package com.houseparty.stream;

import java.text.SimpleDateFormat;

/**
 * Created by abhi on 8/22/17.
 */

public class Config {
    public static final String STREAM_URL_PREFIX = "https://hp-server-toy.herokuapp.com/?since=";

    public static final String PREF_TIMESTAMP = "PREF_TIMESTAMP";

    public static final int NUM_LIST_ITEMS = 2000;

    public static final long UPDATE_MILLIS = 1000;

    public static final int BATCH_SIZE = 1000;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:SS");

}
