package com.convergence.excamera.sdk.common;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

/**
 * 系统相册识别图片、视频工具
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class MediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

    private MediaScannerConnection connection;
    private OnScannerListener listener;
    private File scanFile;

    public MediaScanner(Context context) {
        connection = new MediaScannerConnection(context, this);
    }

    public void scanFile(String path, OnScannerListener listener) {
        scanFile(new File(path), listener);
    }

    public void scanFile(File file, OnScannerListener listener) {
        this.scanFile = file;
        this.listener = listener;
        connection.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        if (scanFile != null) {
            connection.scanFile(scanFile.getAbsolutePath(), null);
        }
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        connection.disconnect();
        if (listener != null) {
            listener.onScanDone();
        }
    }

    public interface OnScannerListener {

        void onScanDone();
    }
}
