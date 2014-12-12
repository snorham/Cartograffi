package com.detroitlabs.cartograffi.fragments;


import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.detroitlabs.cartograffi.R;
import com.detroitlabs.cartograffi.adapters.SnapshotListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ViewSavedFragment extends Fragment implements AdapterView.OnItemClickListener {
    private SnapshotListAdapter snapshotListAdapter;

    public ViewSavedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File sharedFile = CreateFragment.sharedFile;
        if(sharedFile.exists()){
            sharedFile.delete();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_view_saved, container, false);

        ActionBar ab = getActivity().getActionBar();
        ab.setTitle(getResources().getString(R.string.title_activity_view_saved));

        snapshotListAdapter = new SnapshotListAdapter(getActivity(),getSavedSnapshotFiles());

        ListView snapshotListView = (ListView) root.findViewById(R.id.view_saved_listview);
        snapshotListView.setAdapter(snapshotListAdapter);
        snapshotListView.setOnItemClickListener(this);

        return root;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File chosenFile = snapshotListAdapter.getItem(position);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if (fragmentTransaction.isEmpty()) {
            fragmentTransaction.replace(R.id.container_frame,ViewSavedDetailFragment.newInstance(chosenFile));
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

    }

    public ArrayList<File> getSavedSnapshotFiles(){
        File[] filesArray = SaveFragment.directory.listFiles();
        ArrayList<File> filesList = new ArrayList<File>(Arrays.asList(filesArray));
        Collections.reverse(filesList);
        return filesList;
    }
}
