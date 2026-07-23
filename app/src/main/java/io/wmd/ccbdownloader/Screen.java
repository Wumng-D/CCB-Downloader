package io.wmd.ccbdownloader;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public interface Screen {
    View show(Context context);
    void initialize(Context context);
    void onResult(int requestCode, int resultCode, Intent data);
    void hide();
}
