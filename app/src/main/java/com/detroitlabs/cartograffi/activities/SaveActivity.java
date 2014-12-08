package com.detroitlabs.cartograffi.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
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
        Bitmap mapImage = intent.getParcelableExtra(CreateFragment.MAP_IMAGE_KEY);

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

