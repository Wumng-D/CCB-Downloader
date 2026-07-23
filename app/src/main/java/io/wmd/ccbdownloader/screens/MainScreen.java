package io.wmd.ccbdownloader.screens;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import io.wmd.ccbdownloader.R;
import io.wmd.ccbdownloader.Screen;
import io.wmd.ccbdownloader.ui.ReleasesPopup;

public class MainScreen implements Screen {

    private Context context;
    public Context getContext() { return context; }

    private View root;
    private PopupWindow exceptionPopup, loadingPopup;
    public ReleasesPopup releasesPopup;
    public TextView exceptionText;
    public Button updateButton, releasesButton;
    public Button updateOtopackButton, releasesOtopackButton;
    public Button updateCCSoundButton, releasesCCSoundButton;

    @Override
    public View show(Context context) {
        this.context = context;

        root = LayoutInflater.from(context).inflate(R.layout.main, null);
        updateButton    = root.findViewById(R.id.update_releases);
        releasesButton  = root.findViewById(R.id.click_releases);

        updateOtopackButton    = root.findViewById(R.id.update_otopack_releases);
        releasesOtopackButton  = root.findViewById(R.id.click_otopack_releases);

        updateCCSoundButton    = root.findViewById(R.id.update_ccsound_releases);
        releasesCCSoundButton  = root.findViewById(R.id.click_ccsound_releases);

        releasesPopup = new ReleasesPopup(context, root);

        return root;
    }

    @Override
    public void initialize(Context context) {
        exceptionText = new TextView(context);
        exceptionText.setTextColor(0xff8b0000);
        exceptionPopup = new PopupWindow(
                exceptionText,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );
        exceptionPopup.setTouchable(true);

        View content = LayoutInflater.from(context).inflate(R.layout.loading, null, false);
        loadingPopup = new PopupWindow(
                content,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );
        loadingPopup.setAnimationStyle(R.anim.anim_pop);
        loadingPopup.setTouchable(false);
    }

    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {}

    @Override
    public void hide() {
        exceptionPopup.dismiss();
        releasesPopup.dismiss();
    }

    public void popupLoading() { loadingPopup.showAtLocation(root, Gravity.CENTER, 0, 0); }

    public void dismissLoading() { loadingPopup.dismiss(); }

    public void showException(String text) {
        exceptionText.setText(text);
        exceptionPopup.showAtLocation(root, Gravity.CENTER, 0, 0);
    }

    public void toast(String info) { Toast.makeText(context, info, Toast.LENGTH_SHORT).show(); }
}
