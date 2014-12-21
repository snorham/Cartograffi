package com.detroitlabs.cartograffi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.detroitlabs.cartograffi.R;
import com.detroitlabs.cartograffi.interfaces.SnapshopListItemShareListener;
import com.detroitlabs.cartograffi.utils.CartograffiUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Borham on 12/8/14.
 */
public class SnapshotListAdapter extends ArrayAdapter<File> {
    private Context context;
    private SnapshopListItemShareListener shareListener;
    private ArrayList<ImageButton> shareButtons = new ArrayList<ImageButton>();

    public SnapshotListAdapter(SnapshopListItemShareListener shareListener, Context context, ArrayList<File> files) {
        super(context, R.layout.row_item_snapshot, files);
        this.shareListener = shareListener;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_item_snapshot, parent, false);
        }

        final File mapFile = getItem(position);

        ImageView thumbnail = (ImageView) convertView.findViewById(R.id.view_row_thumbnail);
        thumbnail.setImageBitmap(CartograffiUtils.getBitmapFromFile(mapFile));

        TextView filenameTextView = (TextView) convertView.findViewById(R.id.view_row_filename_textview);
        filenameTextView.setText(mapFile.getName());

        TextView modifiedTextView = (TextView) convertView.findViewById(R.id.view_row_modified_textview);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        modifiedTextView.setText(sdf.format(mapFile.lastModified()));

        final ImageButton share = (ImageButton) convertView.findViewById(R.id.view_row_share_button);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ToggleButton)v).setChecked(true);
                for (ImageButton shareButton: shareButtons){
                    shareButton.setEnabled(false);
                }
                shareListener.OnShare(shareButtons);
                CartograffiUtils.shareImageFile(context, mapFile);
            }
        });
        shareButtons.add(share);

        return convertView;
    }
}
