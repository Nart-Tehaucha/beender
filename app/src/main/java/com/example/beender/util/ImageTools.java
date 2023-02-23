package com.example.beender.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class ImageTools {
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        int quality = 90; // initial compression quality
        int targetSize = 500 * 1024; // target file size in bytes
        while (outputStream.toByteArray().length > targetSize) {
            // Reduce the image dimensions by half
            Bitmap compressedImage = Bitmap.createScaledBitmap(image, image.getWidth() / 2, image.getHeight() / 2, true);
            outputStream.reset();
            // Compress the image with the current quality
            compressedImage.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            quality -= 10; // Decrease the compression quality by 10
        }
        byte[] compressedData = outputStream.toByteArray();
        Bitmap compressedImage = BitmapFactory.decodeByteArray(compressedData, 0, compressedData.length);
        return compressedImage;
    }

}
