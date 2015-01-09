package com.detroitlabs.cartograffi.fragments;


import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.detroitlabs.cartograffi.R;
import com.detroitlabs.cartograffi.adapters.SnapshotListAdapter;
import com.detroitlabs.cartograffi.interfaces.OnSnapshotListItemShareListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ViewSavedFragment extends Fragment implements AdapterView.OnItemClickListener,OnSnapshotListItemShareListener {
    private SnapshotListAdapter snapshotListAdapter;
    private ListView snapshotListView;
    private ArrayList<ImageButton> shareButtons;

    public ViewSavedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_view_saved, container, false);

        ActionBar ab = getActivity().getActionBar();
        if (ab != null){
            ab.setTitle(getResources().getString(R.string.title_activity_view_saved));
        }

        ArrayList<File> files = getSavedSnapshotFiles();
        if(SaveFragment.directory.list().length < 1) {
            Toast.makeText(getActivity(), "No saved files", Toast.LENGTH_SHORT).show();
        }else {
            snapshotListAdapter = new SnapshotListAdapter(this, getActivity(), files);
            snapshotListView = (ListView) root.findViewById(R.id.view_saved_listview);
            snapshotListView.setAdapter(snapshotListAdapter);
            snapshotListView.setOnItemClickListener(this);
        }

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shareButtons != null){
            for(ImageButton shareButton: shareButtons){
                shareButton.setEnabled(true);
            }
        }
        if (snapshotListView != null){
            snapshotListView.setEnabled(true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        snapshotListView.setEnabled(false);
        File chosenFile = snapshotListAdapter.getItem(position);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_frame,ViewSavedDetailFragment.newInstance(chosenFile));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void OnSnapshotShare(ArrayList<ImageButton> disabledShareButtons) {
        shareButtons = disabledShareButtons;
        snapshotListView.setEnabled(false);
    }

    public ArrayList<File> getSavedSnapshotFiles(){
        File[] filesArray = SaveFragment.directory.listFiles();
        ArrayList<File> filesList = new ArrayList<File>(Arrays.asList(filesArray));
        Collections.reverse(filesList);
        return filesList;
    }
}