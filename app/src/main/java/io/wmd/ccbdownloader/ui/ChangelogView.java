package io.wmd.ccbdownloader.ui;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.*;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Typeface;
import android.widget.TextView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangelogView extends TextView {

    private static final Pattern URL_PATTERN = Pattern.compile("https://[^\\s]+");
    private static final Pattern AT_PATTERN = Pattern.compile("@\\S+");

    private OnUrlClickListener listener;

    public ChangelogView(Context context) {
        super(context);
        init();
    }

    public ChangelogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChangelogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
    }

    public void setChangelog(String text) {
        if (text == null) text = "";
        String[] lines = text.split("\n");
        SpannableStringBuilder fullBuilder = new SpannableStringBuilder();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String content = line;
            boolean isBold = false;
            boolean isTitle = false;

            if (line.startsWith("## ")) {
                isTitle = true;
                isBold = true;
                content = line.substring(3);
            } else if (line.startsWith("* ")) {
                isBold = true;
                content = line.substring(2);
            }

            SpannableStringBuilder lineBuilder = new SpannableStringBuilder(content);

            if (lineBuilder.length() > 0) {
                // ## Title
                if (isTitle) {
                    lineBuilder.setSpan(
                            new RelativeSizeSpan(1.5f),
                            0, lineBuilder.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }
                // * Item
                if (isBold) {
                    lineBuilder.setSpan(
                            new StyleSpan(Typeface.BOLD),
                            0, lineBuilder.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }
            }

            Matcher atMatcher = AT_PATTERN.matcher(lineBuilder.toString());
            while (atMatcher.find()) {
                int start = atMatcher.start();
                int end = atMatcher.end();
                lineBuilder.setSpan(
                        new ForegroundColorSpan(0xffff557f),
                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }

            Matcher matcher = URL_PATTERN.matcher(lineBuilder.toString());
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                final String url = matcher.group();

                lineBuilder.setSpan(
                        new ForegroundColorSpan(0xff00ff00),
                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                lineBuilder.setSpan(
                        new UnderlineSpan(),
                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                lineBuilder.setSpan(
                        new ClickableSpan() {
                            @Override public void onClick(View widget) {
                                if (ChangelogView.this.listener != null) listener.click(url);
                            }
                        }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }

            fullBuilder.append(lineBuilder);
            if (i < lines.length - 1) fullBuilder.append("\n");
        }

        setText(fullBuilder);
    }


    public void setOnUrlClickListener(OnUrlClickListener listener) {
        this.listener = listener;
    }

    @FunctionalInterface
    public interface OnUrlClickListener { void click(String url); }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}