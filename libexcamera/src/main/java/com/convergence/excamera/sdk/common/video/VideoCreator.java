package com.convergence.excamera.sdk.common.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Size;

import com.convergence.excamera.sdk.common.MediaScanner;
import com.convergence.excamera.sdk.common.callback.ImgProvider;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * 基于MediaCodec，MediaMuxer实现实时图片合成视频
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class VideoCreator implements Handler.Callback {

    /**
     * 录像帧率
     */
    public static final int FRAME_STANDARD_RECORD = 24;
    /**
     * 延时摄影帧率
     */
    public static final int FRAME_TIME_LAPSE_RECORD = 30;

    private static final int MSG_WHAT_RUN_TIME = 100;
    private static final int MSG_WHAT_START_SUCCESS = 101;
    private static final int MSG_WHAT_START_FAIL = 102;
    private static final int MSG_WHAT_STOP_SUCCESS = 103;
    private static final int MSG_WHAT_STOP_FAIL = 104;

    public enum State {
        /*
        空闲状态，未进行初始化
         */
        Free,
        /*
        就绪状态，已进行初始化，可以开始运行
         */
        Ready,
        /*
        运行状态
         */
        Running
    }

    private Context context;
    private ImgProvider imgProvider;
    private int frame;
    private OnCreateVideoListener listener;

    private Handler mainHandler;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private Mp4MediaMuxer mp4MediaMuxer;
    private Queue<Frame> frameQueue;
    private State curState;
    private String videoPath;
    private Size videoSize;
    private int timeLapseRate;
    private int runTime;
    private long frameProvideDelay;

    private VideoCreator(Builder builder) {
        this.context = builder.context;
        this.imgProvider = builder.imgProvider;
        this.frame = builder.frame;
        this.listener = builder.listener;
        mp4MediaMuxer = new Mp4MediaMuxer();
        mainHandler = new Handler(this);
        frameQueue = new LinkedTransferQueue<>();
        reset(true);
    }

    /**
     * 配置初始化
     *
     * @param videoPath 视频存储路径
     * @param videoSize 视频分辨率
     */
    public void setup(String videoPath, Size videoSize) {
        setup(videoPath, videoSize, 1);
    }

    /**
     * 配置初始化
     *
     * @param videoPath     视频存储路径
     * @param videoSize     视频分辨率
     * @param timeLapseRate 延时摄影倍率
     */
    public void setup(String videoPath, Size videoSize, int timeLapseRate) {
        this.videoPath = videoPath;
        this.videoSize = videoSize;
        this.timeLapseRate = Math.max(1, timeLapseRate);
        frameProvideDelay = (long) ((float) this.timeLapseRate / frame * 1000);
        backgroundThread = new HandlerThread("VideoCreator");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
        fixPathDir(videoPath);
        mp4MediaMuxer.setup(videoSize, videoPath, frame, new Mp4MediaMuxer.OnSetupListener() {
            @Override
            public void onSetupSuccess() {
                changeState(State.Ready);
                listener.onSetupSuccess();
            }

            @Override
            public void onSetupError(String error) {
                listener.onSetupError(error);
            }
        });
    }

    /**
     * 开始创建视频
     */
    public void start() {
        switch (curState) {
            case Free:
                sendMsg(MSG_WHAT_START_FAIL, "VideoCreator hasn't been initiated");
                break;
            case Running:
                sendMsg(MSG_WHAT_START_FAIL, "VideoCreator already running");
                break;
            case Ready:
                changeState(State.Running);
                mp4MediaMuxer.start();
                mainHandler.postDelayed(timeRunnable, 1000);
                mainHandler.post(frameRunnable);
                backgroundHandler.post(compoundRunnable);
                backgroundHandler.post(decodeRunnable);
                sendMsg(MSG_WHAT_START_SUCCESS);
                break;
            default:
                break;
        }
    }

    /**
     * 停止创建视频
     */
    public void stop() {
        if (isRunning()) {
            if (new File(videoPath).exists()) {
                MediaScannerConnection.scanFile(context.getApplicationContext(), new String[]{videoPath}, null,
                        (path, uri) -> sendMsg(MSG_WHAT_STOP_SUCCESS));
            } else {
                sendMsg(MSG_WHAT_STOP_FAIL, "Video file doesn't exist");
            }
        }
        release();
    }

    /**
     * 释放资源
     */
    private void release() {
        mainHandler.removeCallbacks(timeRunnable);
        mainHandler.removeCallbacks(frameRunnable);
        if (backgroundHandler != null) {
            backgroundHandler.removeCallbacks(compoundRunnable);
            backgroundHandler.removeCallbacks(decodeRunnable);
            backgroundHandler = null;
        }
        if (backgroundThread != null) {
            try {
                backgroundThread.quitSafely();
                backgroundThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                backgroundThread = null;
            }
        }
        mp4MediaMuxer.release();
        reset(false);
    }

    /**
     * 重置
     *
     * @param isInit 是否创建类时重置
     */
    private void reset(boolean isInit) {
        frameQueue.clear();
        runTime = 0;
        if (isInit) {
            curState = State.Free;
        } else {
            changeState(State.Free);
        }
    }

    /**
     * 更改状态
     */
    private void changeState(State state) {
        if (state == curState) {
            return;
        }
        curState = state;
        listener.onStateChange(curState);
    }

    /**
     * 向帧队列中放入图片Bitmap
     */
    private void offerBitmap(Bitmap bitmap) {
        if (!isRunning() || bitmap == null) {
            return;
        }
        long timeUs = System.nanoTime() / 1000 / timeLapseRate;
        frameQueue.offer(new Frame(bitmap, timeUs));
    }

    /**
     * 修复保存路径确保不出错
     */
    private void fixPathDir(String path) {
        //若保存路径目录为空，则创建该目录，防止ErrnoException造成的无法录像
        File file = new File(path);
        File parentFile = file.getParentFile();
        if (!parentFile.exists() || !parentFile.isDirectory()) {
            parentFile.mkdirs();
        }
    }

    private void sendMsg(int what) {
        mainHandler.sendEmptyMessage(what);
    }

    private void sendMsg(int what, Object obj) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = obj;
        mainHandler.sendMessage(msg);
    }

    public State getCurState() {
        return curState;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public Size getVideoSize() {
        return videoSize;
    }

    public int getRunTime() {
        return runTime;
    }

    public long getFrameProvideDelay() {
        return frameProvideDelay;
    }

    public boolean isRunning() {
        return curState == State.Running && backgroundThread != null && backgroundThread.isAlive()
                && backgroundHandler != null;
    }

    /**
     * 读秒线程
     * 如有设置最大时长，则超过最大时长自动停止
     */
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            runTime++;
            mainHandler.sendEmptyMessage(MSG_WHAT_RUN_TIME);
            mainHandler.postDelayed(this, 1000);
        }
    };

    /**
     * 读图线程
     * 定时将DataProvider中数据加入Bitmap队列
     * 尽量不在此线程中做耗时操作
     */
    private Runnable frameRunnable = new Runnable() {
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            offerBitmap(imgProvider.provideBitmap());
            long costTime = System.currentTimeMillis() - startTime;
            if (frameProvideDelay > costTime) {
                mainHandler.postDelayed(this, frameProvideDelay - costTime);
            } else {
                mainHandler.post(this);
            }
        }
    };

    /**
     * 合成线程
     * 轮询Bitmap队列将图片合成到视频文件中
     */
    private Runnable compoundRunnable = new Runnable() {
        @Override
        public void run() {
            while (!frameQueue.isEmpty()) {
                Frame frame = frameQueue.poll();
                mp4MediaMuxer.encodeData(frame.getBitmap(), frame.getTimeUS());
            }
            if (isRunning()) {
                mainHandler.postDelayed(this, 100);
            }
        }
    };

    /**
     * 解码并保存入视频文件线程
     */
    private Runnable decodeRunnable = new Runnable() {

        @Override
        public void run() {
            if (!isRunning()) {
                return;
            }
            mp4MediaMuxer.decodeData();
            mainHandler.post(this);
        }
    };

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_WHAT_RUN_TIME:
                listener.onRunning(runTime);
                break;
            case MSG_WHAT_START_SUCCESS:
                listener.onStartSuccess();
                break;
            case MSG_WHAT_START_FAIL:
                listener.onStartError((String) msg.obj);
                break;
            case MSG_WHAT_STOP_SUCCESS:
                listener.onCreateVideoSuccess(videoPath);
                break;
            case MSG_WHAT_STOP_FAIL:
                listener.onCreateVideoError((String) msg.obj);
                break;
            default:
                break;
        }
        return true;
    }

    public static class Builder {

        private Context context;
        private ImgProvider imgProvider;
        private int frame = FRAME_STANDARD_RECORD;
        private OnCreateVideoListener listener;

        public Builder(Context context, ImgProvider imgProvider, OnCreateVideoListener listener) {
            this.context = context;
            this.imgProvider = imgProvider;
            this.listener = listener;
        }

        public Builder setFrame(int frame) {
            this.frame = frame;
            this.frame = Math.max(frame, 0);
            this.frame = Math.min(frame, 60);
            return this;
        }

        public VideoCreator build() {
            return new VideoCreator(this);
        }
    }

    /**
     * 创建视频监听
     */
    public interface OnCreateVideoListener {

        /**
         * 状态变化
         *
         * @param state 当前状态
         */
        void onStateChange(State state);

        /**
         * 配置初始化成功
         */
        void onSetupSuccess();

        /**
         * 配置初始化出错
         *
         * @param error 错误原因
         */
        void onSetupError(String error);

        /**
         * 开始创建视频成功
         */
        void onStartSuccess();

        /**
         * 开始创建视频出错
         *
         * @param error 错误原因
         */
        void onStartError(String error);

        /**
         * 允许过程中读秒
         *
         * @param runSeconds 当前允许秒数
         */
        void onRunning(int runSeconds);

        /**
         * 创建视频成功
         *
         * @param path 视频路径
         */
        void onCreateVideoSuccess(String path);

        /**
         * 创建视频失败
         *
         * @param error 错误原因
         */
        void onCreateVideoError(String error);
    }

    /**
     * 帧画面数据封装
     */
    private static class Frame {

        private Bitmap bitmap;
        private long timeUS;

        public Frame(Bitmap bitmap, long timeUS) {
            this.bitmap = bitmap;
            this.timeUS = timeUS;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public long getTimeUS() {
            return timeUS;
        }
    }
}
