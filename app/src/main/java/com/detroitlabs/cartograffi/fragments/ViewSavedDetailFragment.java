package com.detroitlabs.cartograffi.fragments;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.detroitlabs.cartograffi.R;
import com.detroitlabs.cartograffi.utils.CartograffiUtils;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ViewSavedDetailFragment extends Fragment implements View.OnClickListener {
    public static final String MAP_FILE_KEY = "mapFile";
    private File mapFile;
    private Menu menu;
    private Button deleteButton;

    //FOR LATER
    public static ViewSavedDetailFragment newInstance(File mapFile){
        Bundle args = new Bundle();
        args.putSerializable(MAP_FILE_KEY, mapFile);
        ViewSavedDetailFragment viewDetailFrag = new ViewSavedDetailFragment();
        viewDetailFrag.setArguments(args);
        return viewDetailFrag;
    }

    public ViewSavedDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_share).setEnabled(true).setVisible(true);
        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                return true;

            case R.id.action_share:
                menu.setGroupEnabled(0, false);
                deleteButton.setEnabled(false);
                CartograffiUtils.shareImageFile(getActivity(), mapFile);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_view_saved_detail, container, false);
        getActivity().closeOptionsMenu();
        setHasOptionsMenu(true);

        if (getArguments() != null) {
             mapFile = (File)getArguments().getSerializable(MAP_FILE_KEY);
        }

        ActionBar ab = getActivity().getActionBar();
        if (ab != null) ab.setTitle(mapFile.getName());

        ImageView snapshotImageView = (ImageView)root.findViewById(R.id.view_detail_snapshot_container);
        snapshotImageView.setImageBitmap(CartograffiUtils.getBitmapFromFile(mapFile));

        TextView modifiedTextView = (TextView)root.findViewById(R.id.view_detail_modified_textView);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        modifiedTextView.setText(sdf.format(mapFile.lastModified()));

        deleteButton = (Button)root.findViewById(R.id.view_detail_delete_button);
        deleteButton.setOnClickListener(this);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (menu != null){
            menu.setGroupEnabled(0, true);
        }
        if (deleteButton != null){
            deleteButton.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.view_detail_delete_button:
                deleteConfirmationDialog();
        }
    }

    public void deleteConfirmationDialog(){
        final AlertDialog.Builder deleteConfirmation = new AlertDialog.Builder(getActivity());
        deleteConfirmation.setTitle(R.string.view_detail_delete_confirm_title);
        deleteConfirmation.setMessage(R.string.view_detail_delete_confirm_message);
        deleteConfirmation.setPositiveButton(R.string.view_detail_delete_confirm_positive, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                menu.setGroupEnabled(0, false);
                mapFile.delete();
                getFragmentManager().popBackStack();
            }
        });
        deleteConfirmation.setNegativeButton(R.string.view_detail_delete_confirm_negative, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // dialog is dismisssed
            }
        });
        deleteConfirmation.show();

    }
}
