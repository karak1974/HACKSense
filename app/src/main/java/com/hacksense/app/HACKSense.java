package com.hacksense.app;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of App Widget functionality.
 */
public class HACKSense extends AppWidgetProvider {

    // getCurrentTime is for DEBUG only
    private static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) throws ExecutionException, InterruptedException {
        CharSequence widgetText = context.getString(R.string.app_name);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.h_a_c_k_sense);
        views.setTextViewText(R.id.widget_title, widgetText);

        // when
        String when = getCurrentTime();
        views.setTextViewText(R.id.when, when);
        Log.i("HACKSense", "When "+when);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        String responseBody = new HttpRequestTask(context, appWidgetManager, appWidgetId, views).execute().get();
        Log.i("HACKSense", responseBody);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            try {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Log.i("HACKSense", "Update triggered");
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static class HttpRequestTask extends AsyncTask<Void, Void, String> {
        private final Context context;
        private final AppWidgetManager appWidgetManager;
        private final int appWidgetId;
        private final RemoteViews views;

        public HttpRequestTask(Context context, AppWidgetManager appWidgetManager, int appWidgetId, RemoteViews views) {
            this.context = context;
            this.appWidgetManager = appWidgetManager;
            this.appWidgetId = appWidgetId;
            this.views = views;
        }

        @Override
        protected String doInBackground(Void... params) {
            // Perform the GET request
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
                Log.e("HACKSense", "Error making GET request", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Update the widget views with the response body
            if (result != null) {
                Log.i("HACKSense", "Response Body: " + result);
                // You can update the widget views with the response here
                // For example: views.setTextViewText(R.id.response_body, result);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }
}