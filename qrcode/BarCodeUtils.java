package com.utils.qrcode;

import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.text.TextPaint;

/**
 * 条码生成工具
 *
 * @author unknow
 */
@SuppressWarnings("AliDeprecation")
public class BarCodeUtils {

    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xffffffff;

    /**
     * 生成条码
     *
     * @param contents      内容
     * @param format        码类型 ，如：BarcodeFormat.CODE_128
     * @param desiredWidth  宽度
     * @param desiredHeight 高度
     * @return 创建带数字的条形码可打印
     */
    @SuppressWarnings("AliDeprecation")
    public static Bitmap create1DCode(String contents, BarcodeFormat format, int desiredWidth, int desiredHeight) {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contents, format, desiredWidth, desiredHeight, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        Bitmap nBitmp = Bitmap.createBitmap(width, height + 30, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(nBitmp);
        ColorMatrix cm = new ColorMatrix();
        // 返回灰度图片
        cm.setSaturation(0);
        Paint paint = new Paint();
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawColor(WHITE);

        c.drawBitmap(bitmap, 0, 0, paint);
        // 绘制文字
        TextPaint textPaint = new TextPaint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(20);
        textPaint.setColor(BLACK);
        textPaint.setStrokeWidth(0.5f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColorFilter(f);

        c.drawText(contents, width / 2, height + 20, textPaint);
        // 保存
        textPaint.setColor(WHITE);
        textPaint.setStrokeWidth(10f);
        c.drawLine(0, 0, width, 0, textPaint);
//		c.save(Canvas.ALL_SAVE_FLAG);
        c.save();
        // 存储
        c.restore();
        return nBitmp;
    }

    /**
     * 生成条形码
     *
     * @param contents      内容
     * @param format        条码类型
     * @param desiredWidth  宽度
     * @param desiredHeight 高度
     * @return 位图
     */
    public static Bitmap createBarCode(String contents, BarcodeFormat format, int desiredWidth, int desiredHeight) {
        BitMatrix result = null;
        try {
            result = new MultiFormatWriter().encode(contents, format, desiredWidth, desiredHeight, null);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        // All are 0, or black, by default
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                // pixels[offset + x] = result.get(x, y) ? WHITE :BLACK ;
                if (result.get(x, y)) {
                    pixels[offset + x] = BLACK;
                } else {
                    pixels[offset + x] = WHITE;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

}
