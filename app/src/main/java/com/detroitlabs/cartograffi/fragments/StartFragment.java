package com.detroitlabs.cartograffi.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.detroitlabs.cartograffi.R;
import com.detroitlabs.cartograffi.activities.CreateActivity;
import com.detroitlabs.cartograffi.activities.ViewSavedActivity;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class StartFragment extends Fragment implements View.OnClickListener {


    public StartFragment() {
    }

    //FOR LATER
    public static StartFragment newInstance(){
        Bundle args = new Bundle();
        StartFragment startFrag = new StartFragment();
        startFrag.setArguments(args);
        return startFrag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_start, container, false);

        Button createButton = (Button) root.findViewById(R.id.start_create_button);
        createButton.setOnClickListener(this);

        Button viewSavedButton = (Button) root.findViewById(R.id.start_view_saved_button);
        viewSavedButton.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_create_button:
                Intent createIntent = new Intent(getActivity(), CreateActivity.class);
                startActivity(createIntent);
                break;
            case R.id.start_view_saved_button:
                Intent viewSavedIntent = new Intent(getActivity(), ViewSavedActivity.class);
                startActivity(viewSavedIntent);
                break;
        }
    }
}
