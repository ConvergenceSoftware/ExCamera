package com.convergence.excamera.sdk.common.algorithm;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlend;
import android.util.Log;

import androidx.annotation.NonNull;

import com.convergence.excamera.sdk.ScriptC_PixelOperation;
import com.convergence.excamera.sdk.common.BitmapUtil;
import com.convergence.excamera.sdk.common.MediaScanner;
import com.convergence.excamera.sdk.common.callback.ImgProvider;

/**
 * 图像叠加平均操作类，用于图像去噪
 *
 * @Author WangZiheng
 * @CreateDate 2021-01-27
 * @Organization Convergence Ltd.
 */
public class StackAvgOperator implements Handler.Callback {

    private static final String TAG = "StackAvgOperator";

    private static final int DEFAULT_AMOUNT = 20;
    private static final int DEFAULT_FRAME = 10;

    private static final int MSG_STACK_AVG_START = 100;
    private static final int MSG_STACK_AVG_CANCEL = 101;
    private static final int MSG_STACK_AVG_SUCCESS = 102;
    private static final int MSG_STACK_AVG_ERROR = 103;

    private Context context;
    private ImgProvider imgProvider;
    private OnStackAvgListener onStackAvgListener;
    private int amount;
    private int frame;

    private Handler mainHandler;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private String savePath;
    private long frameProvideDelay;
    private long startTMS;
    private int step = 0;
    private volatile Bitmap tempBitmap;
    private volatile boolean isRunning = false;

    protected StackAvgOperator(Builder builder) {
        this.context = builder.context;
        this.imgProvider = builder.imgProvider;
        this.onStackAvgListener = builder.onStackAvgListener;
        this.amount = builder.amount;
        this.frame = builder.frame;
        init();
    }

    private void init() {
        mainHandler = new Handler(this);
        frameProvideDelay = (long) ((float) 1000 / frame);
    }

    /**
     * 开始运行叠加平均算法
     *
     * @param savePath 最终图片保存路径
     */
    public void start(@NonNull String savePath) {
        if (isRunning) {
            mainHandler.sendMessage(mainHandler.obtainMessage(MSG_STACK_AVG_ERROR, "StackAvg is running now"));
            return;
        }
        this.savePath = savePath;
        tempBitmap = null;
        step = 0;
        isRunning = true;
        backgroundThread = new HandlerThread("StackAvg");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
        backgroundHandler.post(operateRunnable);
        mainHandler.sendEmptyMessage(MSG_STACK_AVG_START);
    }

    /**
     * 取消当前正在运行的叠加平均任务
     */
    public void cancel() {
        mainHandler.sendEmptyMessage(MSG_STACK_AVG_CANCEL);
    }

    /**
     * 释放资源并重置参数
     */
    private void release() {
        if (backgroundHandler != null) {
            backgroundHandler.removeCallbacks(operateRunnable);
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
        tempBitmap = null;
        step = 0;
        isRunning = false;
        Log.d(TAG, "Release");
    }

    /**
     * 按照权重比例合成图像
     *
     * @param bitmap 当前获取的图像Bitmap
     * @return 合成是否成功
     */
    private boolean compoundImg(Bitmap bitmap) {
        float beta = ((float) step - 1) / (float) step;
        float alpha = 1.0f - beta;

        RenderScript renderScript = RenderScript.create(context);
        ScriptC_PixelOperation scaleScriptIn = new ScriptC_PixelOperation(renderScript);
        ScriptC_PixelOperation scaleScriptOut = new ScriptC_PixelOperation(renderScript);
        ScriptIntrinsicBlend blendScript = ScriptIntrinsicBlend.create(renderScript, Element.U8_4(renderScript));

        Allocation inputAllocation = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation outputAllocation = Allocation.createFromBitmap(renderScript, tempBitmap);

        scaleScriptIn.set_alpha(beta);
        scaleScriptIn.forEach_scale(inputAllocation, inputAllocation);

        scaleScriptOut.set_alpha(alpha);
        scaleScriptOut.forEach_scale(outputAllocation, outputAllocation);

        blendScript.forEachAdd(inputAllocation, outputAllocation);
        outputAllocation.copyTo(tempBitmap);
        return true;
    }

    /**
     * 判断当前是否正在运行
     */
    public boolean isRunning() {
        return isRunning && backgroundThread != null && backgroundThread.isAlive();
    }

    private Runnable operateRunnable = new Runnable() {
        @Override
        public void run() {
            if (step >= amount) {
                //最后一次保存图像
                long compoundEndTMS = System.currentTimeMillis();
                Log.d(TAG, "Compound cost time : " + (compoundEndTMS - startTMS) + " ms");
                Bitmap resultBitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, true);
                boolean isSaved = BitmapUtil.saveBitmap(resultBitmap, savePath);
                long saveEndTMS = System.currentTimeMillis();
                Log.d(TAG, "Save cost time : " + (saveEndTMS - compoundEndTMS) + " ms");
                Log.d(TAG, "Total cost time : " + (saveEndTMS - startTMS) + " ms");
                if (isSaved) {
                    new MediaScanner(context).scanFile(savePath, null);
                    mainHandler.sendMessage(mainHandler.obtainMessage(MSG_STACK_AVG_SUCCESS, resultBitmap));
                } else {
                    mainHandler.sendMessage(mainHandler.obtainMessage(MSG_STACK_AVG_ERROR, "Save image fail"));
                }
                return;
            }
            Bitmap bitmap = imgProvider.provideBitmap();
            if (bitmap == null) {
                release();
                mainHandler.sendMessage(mainHandler.obtainMessage(MSG_STACK_AVG_ERROR, "Bitmap is null"));
                return;
            }
            if (step == 0) {
                tempBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                startTMS = System.currentTimeMillis();
            }
            step++;
            long compoundStartTMS = System.currentTimeMillis();
            boolean compoundResult = compoundImg(bitmap);
            long compoundCostTMS = System.currentTimeMillis() - compoundStartTMS;
            Log.d(TAG, "Compound Step " + step + " : " + compoundCostTMS + " ms");
            if (compoundResult) {
                if (compoundCostTMS >= frameProvideDelay) {
                    backgroundHandler.post(this);
                } else {
                    backgroundHandler.postDelayed(this, frameProvideDelay - compoundCostTMS);
                }
            } else {
                release();
                mainHandler.sendMessage(mainHandler.obtainMessage(MSG_STACK_AVG_ERROR, "CompoundImg fail"));
            }
        }
    };

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case MSG_STACK_AVG_START:
                Log.d(TAG, "Start");
                if (onStackAvgListener != null) {
                    onStackAvgListener.onStackAvgStart();
                }
                break;
            case MSG_STACK_AVG_CANCEL:
                boolean isCancelable = isRunning();
                release();
                Log.d(TAG, "Cancel");
                if (isCancelable && onStackAvgListener != null) {
                    onStackAvgListener.onStackAvgCancel();
                }
                break;
            case MSG_STACK_AVG_SUCCESS:
                release();
                Bitmap resultBitmap = (Bitmap) msg.obj;
                Log.d(TAG, "Success");
                if (onStackAvgListener != null) {
                    onStackAvgListener.onStackAvgSuccess(resultBitmap, savePath);
                }
                break;
            case MSG_STACK_AVG_ERROR:
                release();
                String error = (String) msg.obj;
                Log.d(TAG, "Error : " + error);
                if (onStackAvgListener != null) {
                    onStackAvgListener.onStackAvgError(error);
                }
                break;
            default:
                break;
        }
        return false;
    }

    public static class Builder {

        private Context context;
        private ImgProvider imgProvider;
        private OnStackAvgListener onStackAvgListener;
        private int amount = DEFAULT_AMOUNT;
        private int frame = DEFAULT_FRAME;

        public Builder(Context context, ImgProvider imgProvider) {
            this.context = context;
            this.imgProvider = imgProvider;
        }

        public Builder setOnStackAvgListener(OnStackAvgListener onStackAvgListener) {
            this.onStackAvgListener = onStackAvgListener;
            return this;
        }

        public Builder setAmount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder setFrame(int frame) {
            this.frame = frame;
            return this;
        }

        public StackAvgOperator build() {
            return new StackAvgOperator(this);
        }
    }

    public interface OnStackAvgListener {

        /**
         * 叠加平均开始
         */
        void onStackAvgStart();

        /**
         * 叠加平均取消
         */
        void onStackAvgCancel();

        /**
         * 叠加平均成功
         *
         * @param bitmap 最终结果Bitmap
         * @param path   最终结果保存路径
         */
        void onStackAvgSuccess(Bitmap bitmap, String path);

        /**
         * 叠加平均出错
         *
         * @param error 错误信息
         */
        void onStackAvgError(String error);
    }
}
