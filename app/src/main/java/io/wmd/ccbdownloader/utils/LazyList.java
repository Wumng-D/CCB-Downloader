package io.wmd.ccbdownloader.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class LazyList {

    private final ReleaseItem[] items;

    private final JSONArray array;

    public LazyList(String jsonData) throws JSONException {
        array = new JSONArray(jsonData);
        items = new ReleaseItem[array.length()];
    }

    public ReleaseItem get(int i) throws JSONException {
        if (items[i] == null) {
            JSONObject obj = array.getJSONObject(i);
            items[i] = new ReleaseItem();
            items[i].title      = obj.getString("name");
            items[i].changelog  = obj.getString("body");
            items[i].prerelease = obj.getBoolean("prerelease");
            items[i].zipballUrl = obj.getString("zipball_url");
            items[i].assetsJson = obj.getJSONArray("assets").toString();
        }
        return items[i];
    }

    public int length() { return items.length; }

}
