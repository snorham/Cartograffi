package com.detroitlabs.cartograffi.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * Created by Borham on 12/8/14.
 */
public class CartograffiUtils {

    private CartograffiUtils(){}

    public static Bitmap getBitmapFromFile(File mapFile){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(mapFile.getPath(), options);
        return bitmap;
    }

    public static void shareImageFile(Context context, File mapFile){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/jpg");
            Uri shareUri = Uri.fromFile(mapFile);
            sharingIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
            context.startActivity(Intent.createChooser(sharingIntent, "Share image using"));
        }
    }
}
