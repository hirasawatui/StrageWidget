package com.app.hirasawatui.stragewidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.Settings;
import android.widget.RemoteViews;

public class StorageWidget extends AppWidgetProvider {

    enum DirectoryType {
        INTERNAL_MEMORY,
        EXTERNAL_MEMORY
    }
    private static int PROGRESS_MAX = 10000;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.storage_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
        DirectoryType type = DirectoryType.EXTERNAL_MEMORY;
        long[] storage  = new long[3];
        boolean isStorageGetSuccess = getStorageByte(type, storage);
        String memMessage = "";
        long usedPercentage = 0;

        if (isStorageGetSuccess) {
            long total = (storage[0] / 1024 / 1024);
            long used =(storage[1] / 1024 / 1024);
            long free =(storage[2] / 1024 / 1024);

            memMessage = "total:" + Long.toString(total) + "MB\n" +
                    "used:" +  Long.toString(used) + "MB\n" +
                    "free:" + Long.toString(free) + "MB\n";

            usedPercentage = (used * PROGRESS_MAX) / total;
        } else {
            memMessage = "no data";
        }

        views.setTextViewText(R.id.textView, memMessage);
        views.setOnClickPendingIntent(R.id.textView, onClickText(context));
        views.setProgressBar(R.id.storage_progress, PROGRESS_MAX, (int) usedPercentage, false);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * getStorageByte
     * @param type {DirectoryType}
     * @param storage {long[3]}
     *          long[0] = total,
     *          long[1] = used,
     *          long[2] = usable
     * @return {boolean}
     */
    private static boolean getStorageByte(DirectoryType type, long[] storage) {
        if(type.equals(DirectoryType.EXTERNAL_MEMORY)) {
            String state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state) ||
                    Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

                storage[0] = Environment.getExternalStorageDirectory().getTotalSpace();
                storage[2] = Environment.getExternalStorageDirectory().getUsableSpace();
                storage[1] = storage[0] - storage[2];
                return true;
            }
        }else if(type.equals(DirectoryType.INTERNAL_MEMORY)){
            storage[0] = Environment.getDataDirectory().getTotalSpace();
            storage[2] = Environment.getDataDirectory().getUsableSpace();
            storage[1] = storage[0] - storage[2];
            return true;
        }
        return false;
    }

    public static PendingIntent onClickText(Context context){
        Intent intent = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
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

