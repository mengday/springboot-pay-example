package com.example.pay.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Map;

/**
 * summary.
 * <p>
 * detailed description
 *
 * @author Mengday Zhang
 * @version 1.0
 * @since 2018/6/18
 */
public class PayUtil {

    /**
     * 根据url生成二位图片对象
     *
     * @param codeUrl
     * @return
     * @throws WriterException
     */
    public static BufferedImage getQRCodeImge(String codeUrl) throws WriterException {
        Map<EncodeHintType, Object> hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF8");
        int width = 256;
        BitMatrix bitMatrix = (new MultiFormatWriter()).encode(codeUrl, BarcodeFormat.QR_CODE, width, width, hints);
        BufferedImage image = new BufferedImage(width, width, 1);
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < width; ++y) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? -16777216 : -1);
            }
        }

        return image;
    }
}
