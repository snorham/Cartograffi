package com.detroitlabs.cartograffi.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.detroitlabs.cartograffi.R;
import com.detroitlabs.cartograffi.interfaces.OnColorClickListener;

/**
 * Created by Borham on 12/2/14.
 */
public class ColorsRecyclerAdapter extends RecyclerView.Adapter<ColorsRecyclerAdapter.ViewHolder> {
    private int[] colors;
    private boolean[] selectedStates;
    private OnColorClickListener onColorClickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public Button colorButton;

        public ViewHolder(Button colorButton) {
            super(colorButton);
            this.colorButton = colorButton;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ColorsRecyclerAdapter(OnColorClickListener onColorClickListener, int[] colors, boolean[] selectedStates) {
        this.onColorClickListener = onColorClickListener;
        this.colors = colors;
        this.selectedStates = selectedStates;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        Button colorButton = (Button) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.color_button, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(colorButton);


        return viewHolder;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.colorButton.setBackgroundColor(colors[i]);
        if (selectedStates[i]){
            int colorForText = setTextColorForColorButton(colors[i]);
            viewHolder.colorButton.setTextColor(colorForText);
            viewHolder.colorButton.setText("SELECTED");
            Log.i("iIs", "i is: "+ String.valueOf(i));

        } else {
            viewHolder.colorButton.setText(null);
        }

        viewHolder.colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedStatesOnClick(i);
                notifyDataSetChanged();
                onColorClickListener.onColorClick(i);
            }
        });
    }

    //Method to set the selected state array to true of button is clicked.

    public void setSelectedStatesOnClick(int itemSelected){
        for(int i = 0; i < selectedStates.length; i++){
            if(i == itemSelected){
                selectedStates[i] = true;
            }
            else{
                selectedStates[i] = false;
            }
        }
    }

    //Method to return the proper color for text based on the color of the button.

    public int setTextColorForColorButton(int buttonColor){
        switch (buttonColor){
            case Color.BLACK:
                Log.i("black", "black");
                return Color.WHITE;
            default:
                return Color.BLACK;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return colors.length;
    }
}
