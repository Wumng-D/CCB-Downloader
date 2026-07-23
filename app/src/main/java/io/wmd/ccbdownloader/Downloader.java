package io.wmd.ccbdownloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.OkDownloadProvider;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Downloader {

    Context context;

    Downloader(Context context) {
        this.context = context;

    }

    public void addTask(String fileName, String url) {
        DownloadTask task = new DownloadTask.Builder(url, context.getExternalFilesDir(null).getParentFile())
                .setFilename(fileName)
                .setMinIntervalMillisCallbackProcess(30)
                .setPassIfAlreadyCompleted(false)
                .setPriority(10)
                .build();
        //task.enqueue(listener);
    }

    class Listener implements DownloadListener {

        private TextView taskText;

        private ProgressBar progressBar;

        private boolean holdView = false;

        public void setView(View taskView) {
            taskText    = taskView.findViewById(R.id.task_text);
            progressBar = taskView.findViewById(R.id.task_progress);
            holdView    = true;
        }

        @Override
        public void taskStart(DownloadTask task) {

        }

        @Override
        public void connectTrialStart(DownloadTask task, Map<String, List<String>> requestHeaderFields) {

        }

        @Override
        public void connectTrialEnd(DownloadTask task, int responseCode, Map<String, List<String>> responseHeaderFields) {

        }

        @Override
        public void downloadFromBeginning(DownloadTask task, BreakpointInfo info, ResumeFailedCause cause) {

        }

        @Override
        public void downloadFromBreakpoint(DownloadTask task, BreakpointInfo info) {

        }

        @Override
        public void connectStart(DownloadTask task, int blockIndex, Map<String, List<String>> requestHeaderFields) {

        }

        @Override
        public void connectEnd(DownloadTask task, int blockIndex, int responseCode, Map<String, List<String>> responseHeaderFields) {

        }

        @Override
        public void fetchStart(DownloadTask task, int blockIndex, long contentLength) {

        }

        @Override
        public void fetchProgress(DownloadTask task, int blockIndex, long increaseBytes) {

        }

        @Override
        public void fetchEnd(DownloadTask task, int blockIndex, long contentLength) {

        }

        @Override
        public void taskEnd(DownloadTask task, EndCause cause, Exception realCause) {

        }
    }

    class TaskAdapter extends BaseAdapter {

        ArrayList<Listener> listeners;

        @Override
        public int getCount() {
            return listeners.size();
        }

        @Override
        public Listener getItem(int position) {
            return listeners.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Listener listener = getItem(position);
            View result = (convertView != null)? convertView :
                    LayoutInflater.from(Downloader.this.context).inflate(R.layout.download_task, null);

            return result;
        }
    }
}
