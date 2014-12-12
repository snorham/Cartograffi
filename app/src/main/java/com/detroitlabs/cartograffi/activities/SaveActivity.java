package com.detroitlabs.cartograffi.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.detroitlabs.cartograffi.R;
import com.detroitlabs.cartograffi.fragments.CreateFragment;
import com.detroitlabs.cartograffi.fragments.SaveFragment;

public class SaveActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_single_fragment);
        Intent intent = getIntent();

        byte[] byteArrayExtra = getIntent().getByteArrayExtra(CreateFragment.MAP_IMAGE_KEY);
        Bitmap mapImage = BitmapFactory.decodeByteArray(
                byteArrayExtra, 0, byteArrayExtra.length);


        FragmentManager fragMan = getFragmentManager();
        Fragment saveFrag = fragMan.findFragmentById(R.id.container_frame);

        if (saveFrag == null) {
            saveFrag = SaveFragment.newInstance(mapImage);
            fragMan.beginTransaction()
                    .add(R.id.container_frame, saveFrag)
                    .commit();
        }
    }
}

