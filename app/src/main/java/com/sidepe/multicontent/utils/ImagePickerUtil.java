package com.sidepe.multicontent.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImagePickerUtil
{

    public static Bitmap getBitmap(Context context, Uri uri)
    {

        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String filePath = cursor.getString(1);
        cursor.close();
        return BitmapFactory.decodeFile(filePath);
    }

    public static void showImagePicker(Context context, int REQUEST_CHOOSE_IMAGE_GALLERY)
    {
        Intent GalIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity) context).startActivityForResult(GalIntent, REQUEST_CHOOSE_IMAGE_GALLERY);
    }

    public static String getStringImage(Bitmap bmp, int IMAGE_MAX_SIZE)
    {
        bmp = getResizedBitmap(bmp, IMAGE_MAX_SIZE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private static Bitmap getResizedBitmap(Bitmap image, int IMAGE_MAX_SIZE)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1)
        {
            width = IMAGE_MAX_SIZE;
            height = (int) (width / bitmapRatio);
        }
        else
        {
            height = IMAGE_MAX_SIZE;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static Bitmap getBitmap(String encodedImage)
    {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return (BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
    }
}