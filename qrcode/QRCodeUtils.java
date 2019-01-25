package com.utils.qrcode;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 二维码生成工具
 *
 * @author unknow
 */
public class QRCodeUtils {

    /**
     * 生成二维码位图
     *
     * @param text  内容
     * @param qrLen 二维码边长
     * @return 二维码
     * @throws Exception 数据处理异常
     */
    public static Bitmap createBitmap(String text, int qrLen) throws Exception {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, qrLen, qrLen, hints);

        int[] pixels = new int[qrLen * qrLen];
        for (int y = 0; y < qrLen; y++) {
            for (int x = 0; x < qrLen; x++) {
                if (bitMatrix.get(x, y)) {
                    pixels[y * qrLen + x] = 0xff000000;
                } else {
                    pixels[y * qrLen + x] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(qrLen, qrLen, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, qrLen, 0, 0, qrLen, qrLen);
        return bitmap;
    }

    /**
     * bitmap转为byte数组
     *
     * @param bitmap 位图
     * @return byte[]数据
     */
    public static byte[] getBitmapData(Bitmap bitmap) {
        int w = bitmap.getWidth(), h = bitmap.getHeight();
        int[] pixels = new int[w * h];
        // 取得BITMAP的所有像素点
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);

        byte[] rgb = addBmpRgb888(pixels, w, h);
        byte[] header = addBMPImageHeader(62 + rgb.length);
        byte[] infos = addBMPImageInfosHeader(w, h, rgb.length);
        byte[] colortable = addBMPImageColorTable();

        byte[] bitmapData = new byte[62 + rgb.length];

        System.arraycopy(header, 0, bitmapData, 0, header.length);
        System.arraycopy(infos, 0, bitmapData, 14, infos.length);
        System.arraycopy(colortable, 0, bitmapData, 54, colortable.length);
        System.arraycopy(rgb, 0, bitmapData, 62, rgb.length);

        return bitmapData;
    }

    /**
     * BMP文件头
     *
     * @param size 内容大小
     * @return bgm文件头数据
     */
    private static byte[] addBMPImageHeader(int size) {
        byte[] buffer = new byte[14];
        buffer[0] = 0x42;
        buffer[1] = 0x4D;

        buffer[2] = (byte) size;
        buffer[3] = (byte) (size >> 8);
        buffer[4] = (byte) (size >> 16);
        buffer[5] = (byte) (size >> 24);

        buffer[6] = 0x00;
        buffer[7] = 0x00;
        buffer[8] = 0x00;
        buffer[9] = 0x00;

        // buffer[10] = 0x36;
        buffer[10] = 0x3E;
        buffer[11] = 0x00;
        buffer[12] = 0x00;
        buffer[13] = 0x00;
        return buffer;
    }

    /**
     * BMP文件信息头
     *
     * @param w    宽
     * @param h    高
     * @param size 内容大小
     * @return bmp信息头
     */
    private static byte[] addBMPImageInfosHeader(int w, int h, int size) {

        byte[] buffer = new byte[40];
        buffer[0] = 0x28;
        buffer[1] = 0x00;
        buffer[2] = 0x00;
        buffer[3] = 0x00;

        buffer[4] = (byte) w;
        buffer[5] = (byte) (w >> 8);
        buffer[6] = (byte) (w >> 16);
        buffer[7] = (byte) (w >> 24);

        buffer[8] = (byte) h;
        buffer[9] = (byte) (h >> 8);
        buffer[10] = (byte) (h >> 16);
        buffer[11] = (byte) (h >> 24);

        buffer[12] = 0x01;
        buffer[13] = 0x00;

        buffer[14] = 0x01;
        buffer[15] = 0x00;

        buffer[16] = 0x00;
        buffer[17] = 0x00;
        buffer[18] = 0x00;
        buffer[19] = 0x00;

        buffer[20] = (byte) size;
        buffer[21] = (byte) (size >> 8);
        buffer[22] = (byte) (size >> 16);
        buffer[23] = (byte) (size >> 24);

        //  buffer[24] = (byte) 0xE0;
        //  buffer[25] = 0x01;
        buffer[24] = (byte) 0xC4;
        buffer[25] = 0x0E;
        buffer[26] = 0x00;
        buffer[27] = 0x00;

        //  buffer[28] = 0x02;
        //  buffer[29] = 0x03;
        buffer[28] = (byte) 0xC4;
        buffer[29] = 0x0E;
        buffer[30] = 0x00;
        buffer[31] = 0x00;

        buffer[32] = 0x00;
        buffer[33] = 0x00;
        buffer[34] = 0x00;
        buffer[35] = 0x00;

        buffer[36] = 0x00;
        buffer[37] = 0x00;
        buffer[38] = 0x00;
        buffer[39] = 0x00;
        return buffer;
    }

    private static byte[] addBMPImageColorTable() {
        byte[] buffer = new byte[8];
        buffer[0] = (byte) 0xff;
        buffer[1] = (byte) 0xff;
        buffer[2] = (byte) 0xff;
        buffer[3] = 0x00;
        buffer[4] = 0x00;
        buffer[5] = 0x00;
        buffer[6] = 0x00;
        buffer[7] = 0x00;
        return buffer;
    }

    private static final String WHITE_COLOR = "ffffff";

    private static byte[] addBmpRgb888(int[] b, int w, int h) {
        int len = w * h;
        int bufflen = 0;
        byte[] tmp = new byte[3];
        int index = 0, bitindex = 1;
        String[] str = {"ffffff", "f0ffff", "fff0ff", "f0f0ff", "fffff0", "f0fff0", "fff0f0", "f0f0f0",};
        // 将8字节变成1个字节,不足补0
        if (w * h % 8 != 0) {
            bufflen = w * h / 8 + 1;
        } else {
            bufflen = w * h / 8;
        }
        // BMP图像数据大小，必须是4的倍数，图像数据大小不是4的倍数时用0填充补足
        if (bufflen % 4 != 0) {
            bufflen = bufflen + bufflen % 4;
        }

        byte[] buffer = new byte[bufflen];
        for (int i = len - 1; i >= w; i -= w) {
            // DIB文件格式最后一行为第一行，每行按从左到右顺序
            int end = i;
            int start = i - w + 1;
            for (int j = start; j <= end; j++) {

                tmp[0] = (byte) b[j];
                tmp[1] = (byte) (b[j] >> 8);
                tmp[2] = (byte) (b[j] >> 16);

                int num = 0x00;
                for (int g : tmp) {
                    if (tmp[g] == -1) {
                        num <<= 1;
                    } else {
                        num <<= 1;
                        num |= 1;
                    }
                }
                if (bitindex > 8) {
                    index += 1;
                    bitindex = 1;
                }

                if (WHITE_COLOR.equals(str[num])) {
                    buffer[index] = (byte) (buffer[index] | (0x01 << 8 - bitindex));
                }
                bitindex++;
            }
        }

        return buffer;
    }
}
