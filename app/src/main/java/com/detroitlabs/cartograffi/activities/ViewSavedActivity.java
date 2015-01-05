package com.detroitlabs.cartograffi.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;

import com.detroitlabs.cartograffi.R;
import com.detroitlabs.cartograffi.fragments.ViewSavedFragment;

public class ViewSavedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_single_fragment);

        FragmentManager fragMan = getFragmentManager();
        Fragment viewSavedFrag = fragMan.findFragmentById(R.id.container_frame);

        if (viewSavedFrag == null) {
            viewSavedFrag = new ViewSavedFragment();
            fragMan.beginTransaction()
                    .add(R.id.container_frame, viewSavedFrag)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_saved, menu);
        menu.findItem(R.id.action_share).setEnabled(false).setVisible(false);
        return true;
    }
}

