package com.detroitlabs.cartograffi.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.detroitlabs.cartograffi.R;
import com.detroitlabs.cartograffi.fragments.CreateFragment;
import com.detroitlabs.cartograffi.fragments.SaveFragment;


public class CreateActivity extends Activity {
    private Menu menu;
    CreateFragment createFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_single_fragment);

        FragmentManager fragMan = getFragmentManager();
        if(savedInstanceState == null){
            createFrag = new CreateFragment();
            fragMan.beginTransaction()
                    .add(R.id.container_frame, createFrag)
                    .commit();
        }

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
            case R.id.action_view_snapshots:
                menu.setGroupEnabled(0,false);
                SaveFragment.directory.mkdirs();
                return viewSnapshotsSelected();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean viewSnapshotsSelected(){
        if(SaveFragment.directory.list().length < 1){
            Toast.makeText(this, "No saved files", Toast.LENGTH_SHORT).show();
            menu.setGroupEnabled(0, true);
            return false;
        }
        else {
            Intent viewSnapsIntent = new Intent(this, ViewSavedActivity.class);
            startActivity(viewSnapsIntent);
            return true;
        }
    }
}
