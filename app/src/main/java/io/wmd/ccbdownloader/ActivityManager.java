package io.wmd.ccbdownloader;

import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;

import java.io.File;

public class ActivityManager {

    public final static ActivityManager INSTANCE = new ActivityManager();

    private Activity activeActivity;

    public void setActiveActivity(Activity activeActivity) { this.activeActivity = activeActivity; }

    public boolean runOnUiThread(Runnable runnable) {
        if (activeActivity != null) {
            activeActivity.runOnUiThread(runnable);
            return true;
        }
        return false;
    }

    public void getMetrics(DisplayMetrics metrics) {
        if (activeActivity != null) activeActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    public File getInternalDir() {
        if (activeActivity != null) return activeActivity.getFilesDir();
        return new File("");
    }

    public void startActivity(Intent intent) {
        if (activeActivity != null) activeActivity.startActivity(intent);
    }

    private ActivityManager() {}
}
