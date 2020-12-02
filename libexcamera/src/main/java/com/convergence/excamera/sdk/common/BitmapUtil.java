package com.convergence.excamera.sdk.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static androidx.core.math.MathUtils.clamp;

/**
 * Bitmap操作工具类
 *
 * @Author WangZiheng
 * @CreateDate 2020-11-06
 * @Organization Convergence Ltd.
 */
public class BitmapUtil {

    /**
     * 保存图片至指定路径
     *
     * @param bitmap 图片Bitmap
     * @param path   保存路径
     * @return 是否成功保存
     */
    public static boolean saveBitmap(Bitmap bitmap, String path) {
        File file = new File(path);
        File dir = file.getParentFile();
        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将View保存为Bitmap
     */
    public static Bitmap createViewBitmap(View view) {
        if (view.getDrawingCache() != null) {
            view.destroyDrawingCache();
        }
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    /**
     * 通过RenderScript将YUV字节流转化为Bitmap
     *
     * @param context 上下文
     * @param nv21    YUV字节流
     * @param width   Bitmap宽度
     * @param height  Bitmap高度
     */
    public static Bitmap getBitmapByNV21(Context context, byte[] nv21, int width, int height) {
        Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        if (nv21 == null || nv21.length == 0) {return outputBitmap;}
        RenderScript renderScript = RenderScript.create(context);
        ScriptIntrinsicYuvToRGB yuvScript = ScriptIntrinsicYuvToRGB.create(renderScript, Element.U8_4(renderScript));

        Type yuvType = new Type.Builder(renderScript, Element.U8(renderScript))
                .setX(nv21.length)
                .create();
        Type rgbaType = new Type.Builder(renderScript, Element.RGBA_8888(renderScript))
                .setX(width)
                .setY(height)
                .create();

        Allocation inputAllocation = Allocation.createTyped(renderScript, yuvType, Allocation.USAGE_SCRIPT);
        Allocation outputAllocation = Allocation.createTyped(renderScript, rgbaType, Allocation.USAGE_SCRIPT);

        inputAllocation.copyFrom(nv21);
        yuvScript.setInput(inputAllocation);
        yuvScript.forEach(outputAllocation);
        outputAllocation.copyTo(outputBitmap);

        return outputBitmap;
    }

    public static byte[] fetchNV12(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int size = w * h;
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);

        byte[] nv21 = new byte[size * 3 / 2];

        final int frameSize = w * h;

        int yIndex = 0;
        int uvIndex = frameSize;

        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {

                a = (pixels[index] & 0xff000000) >> 24; // a is not used obviously
                R = (pixels[index] & 0xff0000) >> 16;
                G = (pixels[index] & 0xff00) >> 8;
                B = (pixels[index] & 0xff) >> 0;
//                R = (argb[index] & 0xff000000) >>> 24;
//                G = (argb[index] & 0xff0000) >> 16;
//                B = (argb[index] & 0xff00) >> 8;

                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                V = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128; // Previously U
                U = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128; // Previously V

                nv21[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) {
                    nv21[uvIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                    nv21[uvIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                }

                index++;
            }
        }

        return nv21;
    }
}
