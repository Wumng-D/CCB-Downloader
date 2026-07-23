package io.wmd.ccbdownloader.controllers;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import io.wmd.ccbdownloader.ActivityManager;
import io.wmd.ccbdownloader.utils.LazyList;
import io.wmd.ccbdownloader.screens.MainScreen;
import io.wmd.ccbdownloader.ui.ReleasesPopup;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class MainController {

    ActivityManager manager = ActivityManager.INSTANCE;

    private final String api_ccb_url     = "https://api.github.com/repos/" +
            "CrimsonCrossBunker/Cataclysm-Cleanwater-Bomb/releases";
    private final String api_otopack_url = "https://api.github.com/repos/" +
            "Kenan2000/Otopack-Mods-Updates/releases";
    private final String api_ccsound_url = "https://api.github.com/repos/" +
            "Fris0uman/CDDA-Soundpacks/releases";
    private final String proxy_url = "https://gh-proxy.org/";
    private final String listCCBPath = "ccb_releases_list.json";
    private final String listOtopackPath = "otopack_releases_list.json";
    private final String listCCSoundPath = "otopack_releases_list.json";

    private File listCCBFile, listOtopackFile, listCCSoundFile;

    private OkHttpClient client;

    private MainScreen screen;

    public MainController(MainScreen screen, OkHttpClient client) {
        this.screen = screen;
        this.client = client;
    }

    /**
     * the method should be called after Screen.show() called.
     */
    public void process() {
        listCCBFile = new File(manager.getInternalDir(), listCCBPath);
        listOtopackFile = new File(manager.getInternalDir(), listOtopackPath);
        listCCSoundFile = new File(manager.getInternalDir(), listCCSoundPath);

        screen.updateButton.setOnClickListener(v -> {
            try { updateReleases(listCCBFile, api_ccb_url); } catch (IOException e) {
                screen.showException(e.toString());
            }
        });
        screen.releasesButton.setOnClickListener(v -> {
            try { showCCBReleases(listCCBFile); } catch (IOException e) {
                screen.showException(e.toString());
            }
        });

        screen.updateOtopackButton.setOnClickListener(v -> {
            try { updateReleases(listOtopackFile, api_otopack_url); } catch (IOException e) {
                screen.showException(e.toString());
            }
        });
        screen.releasesOtopackButton.setOnClickListener(v -> {
            try { showOtopackReleases(listOtopackFile); } catch (IOException e) {
                screen.showException(e.toString());
            }
        });

        screen.updateCCSoundButton.setOnClickListener(v -> {
            try { updateReleases(listCCSoundFile, api_ccsound_url); } catch (IOException e) {
                screen.showException(e.toString());
            }
        });
        screen.releasesCCSoundButton.setOnClickListener(v -> {
            try { showCCSoundReleases(listCCSoundFile); } catch (IOException e) {
                screen.showException(e.toString());
            }
        });
    }

    private void startActivity(String url) {
        if (!url.isEmpty()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, proxy_url + url);
            intent.setType("text/plain");
            manager.startActivity(Intent.createChooser(intent, null));
        } else {
            screen.toast("没有找到适合设备的文件");
        }
    }

    private void showCCBReleases(File cacheFile) throws IOException {
        if (!cacheFile.exists()) { screen.toast("请更新列表"); return; }

        String jsonData = loadCache(cacheFile);
        if (jsonData.isEmpty()) {  screen.toast("请更新列表"); return; }

        ReleasesPopup popup = screen.releasesPopup;
        popup.setShareLinkClick(v -> {
            String data = popup.getSelectedItem().assetsJson;
            startActivity(parseAndroidAssetUrl(data));
        });

        try { popup.popup(new LazyList(jsonData)); } catch (JSONException e) {
            screen.toast(e.toString());
        }
    }

    private void showOtopackReleases(File cacheFile) throws IOException {
        if (!cacheFile.exists()) { screen.toast("请更新列表"); return; }

        String jsonData = loadCache(cacheFile);
        if (jsonData.isEmpty()) {  screen.toast("请更新列表"); return; }

        ReleasesPopup popup = screen.releasesPopup;
        popup.setShareLinkClick(v -> {
            String url = popup.getSelectedItem().zipballUrl;
            startActivity(url);
        });

        try { popup.popup(new LazyList(jsonData)); } catch (JSONException e) {
            screen.toast(e.toString());
        }
    }

    private void showCCSoundReleases(File cacheFile) throws IOException {
        if (!cacheFile.exists()) { screen.toast("请更新列表"); return; }

        String jsonData = loadCache(cacheFile);
        if (jsonData.isEmpty()) {  screen.toast("请更新列表"); return; }

        ReleasesPopup popup = screen.releasesPopup;
        popup.setShareLinkClick(v -> {
            String data = popup.getSelectedItem().assetsJson;
            startActivity(parseCCSoundUrl(data));
        });

        try { popup.popup(new LazyList(jsonData)); } catch (JSONException e) {
            screen.toast(e.toString());
        }
    }

    private void updateReleases(File cacheFile, String apiUrl) throws IOException {
        if (canUpdate(cacheFile)) {
            screen.popupLoading();
            getUrlAsync(apiUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    manager.runOnUiThread(() -> {
                        screen.dismissLoading();
                        screen.showException(e.toString());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful())
                        saveCache(cacheFile, response.body().string());
                    manager.runOnUiThread(screen::dismissLoading);
                }
            });

        } else { screen.toast( "请1分钟后再试"); }
    }

    public void getUrlAsync(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(callback);
    }

    private String parseAndroidAssetUrl(String data) {
        try {
            JSONArray array = new JSONArray(data);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String assetName = obj.getString("name");
                if (assetName.contains("android")) {
                    if (checkABI(assetName)) return obj.getString("browser_download_url");
                }
            }
        } catch (JSONException e) {
            screen.showException(e.toString());
        }
        return "";
    }

    private String parseCCSoundUrl(String data) {
        try {
            JSONArray array = new JSONArray(data);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String assetName = obj.getString("name");
                if (assetName.equals("CC-Sounds.zip")) return obj.getString("browser_download_url");
            }
        } catch (JSONException e) {
            screen.showException(e.toString());
        }
        return "";
    }

    private boolean checkABI(String name) {
        return (Build.SUPPORTED_ABIS[0].equals("arm64-v8a") && name.contains("x64"))
                || (Build.SUPPORTED_ABIS[0].equals("armeabi-v7a") && name.contains("x32"));
    }

    private boolean canUpdate(File cacheFile) throws IOException {
        if (!cacheFile.exists()) {
            cacheFile.createNewFile();
            return true;
        }

        long deltaTime = System.currentTimeMillis() - cacheFile.lastModified();
        return Math.abs(deltaTime) > 60_000;
    }

    private void saveCache(File file, String jsonData) throws IOException {
        try(FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(jsonData.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String loadCache(File file) throws IOException {
        String jsonData = "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            int byteData;
            while ((byteData = bis.read()) != -1) { baos.write(byteData); }
        }

        try {
            jsonData = baos.toString("UTF-8");
        } catch (UnsupportedEncodingException e) { return ""; }
        return jsonData;
    }


}
