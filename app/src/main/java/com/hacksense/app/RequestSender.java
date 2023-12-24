package com.hacksense.app;

import static com.hacksense.app.HACKSense.TAG;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestSender extends AsyncTask<Void, Void, String> {
    private final Context context;
    private final AppWidgetManager appWidgetManager;
    private final int appWidgetId;
    private final RemoteViews views;

    public RequestSender(Context context, AppWidgetManager appWidgetManager, int appWidgetId, RemoteViews views) {
        this.context = context;
        this.appWidgetManager = appWidgetManager;
        this.appWidgetId = appWidgetId;
        this.views = views;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            URL url = new URL("https://vsza.hu/hacksense/status.json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error making GET request", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
