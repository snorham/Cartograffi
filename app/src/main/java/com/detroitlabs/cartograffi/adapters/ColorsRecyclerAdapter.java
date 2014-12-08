package com.detroitlabs.cartograffi.adapters;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.detroitlabs.cartograffi.R;
import com.detroitlabs.cartograffi.interfaces.ColorClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Borham on 12/2/14.
 */
public class ColorsRecyclerAdapter extends RecyclerView.Adapter<ColorsRecyclerAdapter.ViewHolder> implements View.OnClickListener {
    private int[] colors;
    private ColorClickListener colorClickListener;
    List<ViewHolder> toggles = new ArrayList<ViewHolder>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ToggleButton colorButton;
        public ViewHolder(ToggleButton toggle) {
            super(toggle);
            colorButton = toggle;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ColorsRecyclerAdapter(ColorClickListener colorClickListener, int[] colors){
        this.colorClickListener = colorClickListener;
        this.colors = colors;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        //create a new view
        ToggleButton toggle = (ToggleButton) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.color_toggle, viewGroup, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(toggle);

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        viewHolder.colorButton.setBackgroundColor(colors[i]);
        viewHolder.colorButton.setChecked(false);
        viewHolder.colorButton.setOnClickListener(this);


        //cruddy fix for black button... why is blue changing!? D':
        if (i == 0){
            viewHolder.colorButton.setTextColor(colors[1]);
        }

        toggles.add(viewHolder);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return colors.length;
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < toggles.size(); i++){
            if(v != toggles.get(i).colorButton){
                toggles.get(i).colorButton.setChecked(false);
            }
        }

        colorClickListener.onColorClick(((ColorDrawable) v.getBackground()).getColor());
    }
}
