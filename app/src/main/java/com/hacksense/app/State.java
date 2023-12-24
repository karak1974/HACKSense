package com.hacksense.app;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation of App Widget functionality.
 */

public class State {
    private String id;
    private String when;
    private boolean what;

    public State(String id, String when, boolean what) {
        this.id = id;
        this.when = when;
        this.what = what;
    }

    public String getId() {
        return id;
    }

    public String getWhen() {
        return when;
    }

    public boolean getWhat() {
        return what;
    }

    // Method to parse JSON string into MyData object
    public static State fromJson(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String id = jsonObject.getString("id");
            String when = jsonObject.getString("when");
            boolean what = jsonObject.getBoolean("what");

            return new State(id, when, what);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
