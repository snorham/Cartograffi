package com.detroitlabs.cartograffi.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.detroitlabs.cartograffi.R;
import com.detroitlabs.cartograffi.fragments.SaveMapSnapshotFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Borham on 12/8/14.
 */
public class CartograffiUtils {
    public static final File tempSharedFile = new File(SaveMapSnapshotFragment.directory, "temp_shared_file.jpg");

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
        } else {
            Toast.makeText(context, "External storage not available", Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareBitmap(Context context, Bitmap bitmap) {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {

            if (tempSharedFile.exists()){
                tempSharedFile.delete();
            }
            try {
                FileOutputStream fos = new FileOutputStream(tempSharedFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
                shareImageFile(context, tempSharedFile);
            } catch (FileNotFoundException e) {
                Toast.makeText(context, "File for sharing not found", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(context, "Error accessing file to share", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "External storage not available", Toast.LENGTH_SHORT).show();
        }
    }

    public static int[] getAllColorResources(Context context){
        return new int[]{
                context.getResources().getColor(R.color.Black),
                context.getResources().getColor(R.color.White),
                context.getResources().getColor(R.color.Gray),
                context.getResources().getColor(R.color.Red),
                context.getResources().getColor(R.color.Orange),
                context.getResources().getColor(R.color.Yellow),
                context.getResources().getColor(R.color.Green),
                context.getResources().getColor(R.color.Blue),
                context.getResources().getColor(R.color.Indigo),
                context.getResources().getColor(R.color.Violet)};
    }
}
