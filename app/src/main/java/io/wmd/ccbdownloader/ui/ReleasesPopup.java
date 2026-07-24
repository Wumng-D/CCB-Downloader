package io.wmd.ccbdownloader.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import io.wmd.ccbdownloader.ActivityManager;
import io.wmd.ccbdownloader.R;
import io.wmd.ccbdownloader.utils.LazyList;
import io.wmd.ccbdownloader.utils.ReleaseItem;
import org.json.JSONException;

public class ReleasesPopup extends PopupWindow {

    Context context;
    DisplayMetrics metrics;

    View root;
    ListView releasesList;
    ReleaseAdapter adapter;

    PopupWindow detailPopup;
    TextView detailTitle;
    ChangelogView detailText;
    Button shareLink, dlButton;

    ReleaseItem selected;

    public ReleasesPopup(Context context, View root) {
        super(
                LayoutInflater.from(context).inflate(R.layout.show_releases, null, false)
        );
        metrics = new DisplayMetrics();
        adapter = new ReleaseAdapter();
        releasesList = getContentView().findViewById(R.id.releases_list);
        releasesList.setAdapter(adapter);

        setOutsideTouchable(true);
        setFocusable(true);
        setAnimationStyle(R.anim.anim_pop);
        setTouchable(true);
        //setBackgroundDrawable(bg);

        this.context = context;
        this.root = root;

        View detailContent = LayoutInflater.from(context).inflate(R.layout.show_item, null, false);
        detailPopup = new PopupWindow(
                detailContent,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );
        detailTitle  = detailContent.findViewById(R.id.detail_title);
        detailText   = detailContent.findViewById(R.id.detail_text);
        shareLink    = detailContent.findViewById(R.id.share_link);
        dlButton     = detailContent.findViewById(R.id.download);
        detailPopup.setAnimationStyle(R.anim.anim_pop);
        detailPopup.setBackgroundDrawable(new ColorDrawable(0xffffffff));
        detailPopup.setTouchable(true);
    }

    public void popup(LazyList items) {
        adapter.setItems(items);

        ActivityManager.INSTANCE.getMetrics(metrics);

        setWidth(  (int)(metrics.widthPixels * 0.90f) );
        setHeight( (int)(metrics.heightPixels * 0.5f) );
        showAtLocation(root, Gravity.CENTER, 0, 0);
    }

    public void showDetail() {
        detailTitle.setText(selected.title);
        detailText.setChangelog(selected.changelog);

        detailPopup.setWidth(  (int)(metrics.widthPixels  * 0.75f) );
        detailPopup.setHeight( (int)(metrics.heightPixels * 0.5f)  );
        detailPopup.showAtLocation(root, Gravity.CENTER, 0, 0);
    }

    public ChangelogView getDetailText() { return detailText; }

    public ReleaseItem getSelectedItem() { return selected; }

    public void setShareLinkClick(View.OnClickListener l) { shareLink.setOnClickListener(l); }

    public void setDownloadClick(View.OnClickListener l) { dlButton.setOnClickListener(l); }

    class ReleaseAdapter extends BaseAdapter {

        LazyList items;

        void setItems(LazyList items) { this.items = items; }

        @Override
        public int getCount() {
            if (items != null) return items.length();
            return 0;
        }

        @Override
        public ReleaseItem getItem(int position) {
            try {
                return items.get(position);
            } catch (JSONException e) {}
            return null;
        }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Button button = (convertView == null)? new Button(context) : (Button) convertView;
            ReleaseItem item = getItem(position); // select
            if (item.title != null) button.setText(item.title);

            button.setOnClickListener(v -> {
                // select when clicked
                selected = item;
                showDetail();
            });
            return button;
        }
    }

}
