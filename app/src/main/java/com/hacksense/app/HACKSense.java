package com.hacksense.app;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.concurrent.ExecutionException;

public class HACKSense extends AppWidgetProvider {
    public static String TAG = "HACKSense";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) throws ExecutionException, InterruptedException {
        CharSequence widgetText = context.getString(R.string.app_name);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.h_a_c_k_sense);
        views.setTextViewText(R.id.widget_title, widgetText);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        String responseBody = new RequestSender(context, appWidgetManager, appWidgetId, views).execute().get();

        State state = State.fromJson(responseBody);
        if (state != null) {
            // Id
            String id = state.getId();

            // When
            String when = state.getWhen();
            views.setTextViewText(R.id.when, when);

            // What
            String what;
            int color;
            if (state.getWhat()) {
                what = "OPEN";
            } else {
                what = "CLOSED";
            }
            views.setTextViewText(R.id.what, what);

            // Last Checked
            String lastChecked = Utils.getCurrentTime();
            views.setTextViewText(R.id.lastChecked, lastChecked);

            Log.i(TAG, "ID: "+id+" When: "+when+" What: "+what+" Last Checked:"+lastChecked);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(TAG, "Update triggered at: "+Utils.getCurrentTime());
        for (int appWidgetId : appWidgetIds) {
            try {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}