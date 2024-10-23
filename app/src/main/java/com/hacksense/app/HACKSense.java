package com.hacksense.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;

public class HACKSense extends AppWidgetProvider {
    public static String TAG = "HACKSense";
    private static final Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    public static volatile @Nullable State state = null;

    private static void requestRedraw(Context context) {

        // make sure this is running on the UI thread
        uiThreadHandler.post(() -> {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, HACKSense.class));

            for (var appWidgetId : appWidgetIds) {
                try {
                    updateAppWidget(context, appWidgetManager, appWidgetId);
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "ERROR", e);
                }
            }
        });
    }

    static void update(Context context) {
        requestRedraw(context);

        ConcurrentRequest.startRequest(response -> {
            HACKSense.state = response;
            requestRedraw(context);
        });
    }


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) throws ExecutionException, InterruptedException {
        CharSequence widgetText = context.getString(R.string.app_name);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.h_a_c_k_sense);
        views.setTextViewText(R.id.widget_title, widgetText);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        // Reload button
        Intent intent = new Intent(context, HACKSense.class);
        intent.setAction("RELOAD");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.reloadButton, pendingIntent);

        State state = HACKSense.state;
        if (state != null) {
            // When
            String when = state.getWhen();
            views.setTextViewText(R.id.when, when);

            // What
            String what;
            if (state.getWhat()) {
                what = "OPEN";
            } else {
                what = "CLOSED";
            }
            views.setTextViewText(R.id.what, what);

            // Last Checked
            String lastChecked = Utils.getCurrentTime();
            views.setTextViewText(R.id.lastChecked, lastChecked);

            Log.d(TAG, "Updated successfully");
        } else {
            Log.d(TAG, "Update failed, no state");
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if ("RELOAD".equals(intent.getAction())) {
            Log.i(TAG, "Update triggered at: "+Utils.getCurrentTime());
            update(context);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.i(TAG, "Widget created");
        update(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        update(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.i(TAG, "Widget deleted");
    }
}