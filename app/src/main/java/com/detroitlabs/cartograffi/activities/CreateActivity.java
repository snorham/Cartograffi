package com.detroitlabs.cartograffi.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.detroitlabs.cartograffi.R;
import com.detroitlabs.cartograffi.fragments.CreateFragment;
import com.detroitlabs.cartograffi.fragments.SaveFragment;
import com.detroitlabs.cartograffi.utils.CartograffiUtils;
import com.google.android.gms.maps.GoogleMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CreateActivity extends Activity {
    private CreateFragment createFragment;
    private Menu menu;
    public final static String sharedFilename = "Temp_Shared_File.jpg";
    public final static File sharedFile = new File(SaveFragment.directory, sharedFilename);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_single_fragment);

        if (savedInstanceState != null) return;

        createFragment = new CreateFragment();
        getFragmentManager().beginTransaction().add(R.id.container_frame, createFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_hide:
                createFragment.toggleMapUi();
                return true;

            case R.id.action_save_snapshot:
                menu.setGroupEnabled(0, false);
                saveSnapshot();
                return true;

            case R.id.action_view_snapshots:
                menu.setGroupEnabled(0,false);
                Intent viewSnapsIntent = new Intent(this, ViewSavedActivity.class);
                startActivity(viewSnapsIntent);
                return true;

            case R.id.action_share:
                menu.setGroupEnabled(0, false);
                shareMap();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveSnapshot() {
        GoogleMap.SnapshotReadyCallback snapshotReadyCallback;
        snapshotReadyCallback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                goToSaveScreen(bitmap);
            }
        };
        createFragment.captureMapImage(snapshotReadyCallback);
    }

    public void goToSaveScreen(Bitmap bitmap) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bs);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.container_frame, SaveFragment.newInstance(bitmap));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void shareMap() {
        GoogleMap.SnapshotReadyCallback snapshotReadyCallback;
        SaveFragment.directory.mkdirs();
        snapshotReadyCallback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                shareBitmap(bitmap);
            }
        };
        createFragment.captureMapImage(snapshotReadyCallback);
    }

    public void shareBitmap(Bitmap bitmap) {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {

            if (sharedFile.exists()){
                sharedFile.delete();
            }

            try {
                FileOutputStream fos = new FileOutputStream(sharedFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
                CartograffiUtils.shareImageFile(this, sharedFile);
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "File for sharing not found", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Error accessing file to share", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
