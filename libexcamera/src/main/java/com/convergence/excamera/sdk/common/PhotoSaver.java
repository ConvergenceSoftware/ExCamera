package com.convergence.excamera.sdk.common;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import java.util.LinkedList;
import java.util.Queue;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 图片保存封装类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-30
 * @Organization Convergence Ltd.
 */
public class PhotoSaver implements Handler.Callback {

    private static final int MSG_SAVE_PHOTO_SUCCESS = 100;
    private static final int MSG_SAVE_PHOTO_FAIL = 101;

    private Handler handler;
    private Queue<String> saveTaskQueue;
    private Observable<Bitmap> observable;
    private ObservableEmitter<Bitmap> emitter;
    private OnPhotoSaverListener listener;

    private boolean isRunning = false;

    public PhotoSaver(OnPhotoSaverListener listener) {
        this.listener = listener;
        handler = new Handler(this);
        saveTaskQueue = new LinkedList<>();
        observable = Observable.create(emitter -> PhotoSaver.this.emitter = emitter);
        observable = observable.subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io());
    }

    public void run() {
        isRunning = true;
        observable.subscribe(bitmap -> {
            if (!isRunning) {
                return;
            }
            if (!saveTaskQueue.isEmpty()) {
                String path = saveTaskQueue.poll();
                boolean result = BitmapUtil.saveBitmap(bitmap, path);
                if (result) {
                    Message message = new Message();
                    message.what = MSG_SAVE_PHOTO_SUCCESS;
                    message.obj = path;
                    handler.sendMessage(message);
                } else {
                    handler.sendEmptyMessage(MSG_SAVE_PHOTO_FAIL);
                }
            } else {
                handler.sendEmptyMessage(MSG_SAVE_PHOTO_FAIL);
            }
        });
    }

    public void release() {
        isRunning = false;
        saveTaskQueue.clear();
    }

    public void addTask(String path) {
        if (!isRunning) {
            return;
        }
        saveTaskQueue.offer(path);
    }

    public void provideFrame(Bitmap bitmap) {
        if (!isRunning || saveTaskQueue.isEmpty()) {
            return;
        }
        emitter.onNext(bitmap);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SAVE_PHOTO_SUCCESS:
                listener.onSavePhotoSuccess((String) msg.obj);
                break;
            case MSG_SAVE_PHOTO_FAIL:
                listener.onSavePhotoFail();
                break;
            default:
                break;
        }
        return false;
    }

    public interface OnPhotoSaverListener {

        /**
         * 保存图片成功
         *
         * @param path 保存路径
         */
        void onSavePhotoSuccess(String path);

        /**
         * 保存图片失败
         */
        void onSavePhotoFail();
    }
}
