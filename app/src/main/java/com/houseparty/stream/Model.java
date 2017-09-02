package com.houseparty.stream;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by abhi on 8/22/17.
 */

public class Model {
    private static final String TAG = "Model";

    public static class Item {
        public Person from;
        public Person to;
        public boolean areFriends;
        public long timestamp;
    }

    public static class Person {
        public String id;
        public String name;
    }

    public static Item parseItem(String line) {
        try {
            return parseItem(new JSONObject(line));
        } catch (JSONException ex) {
            Log.w(TAG, "Unable to parse line", ex);
        }
        return null;
    }

    private static Item parseItem(JSONObject json) throws JSONException {
        Item item = new Item();
        item.to = parsePerson(json.getJSONObject("to"));
        item.from = parsePerson(json.getJSONObject("from"));
        item.timestamp = Long.parseLong(json.getString("timestamp"));
        item.areFriends = json.getBoolean("areFriends");
        return item;
    }

    private static Person parsePerson(JSONObject json) throws JSONException {
        Person person = new Person();
        person.id = json.getString("id");
        person.name = json.getString("name");
        return person;
    }
}
