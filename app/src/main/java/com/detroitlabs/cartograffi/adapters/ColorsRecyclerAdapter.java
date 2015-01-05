package com.detroitlabs.cartograffi.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.detroitlabs.cartograffi.R;
import com.detroitlabs.cartograffi.interfaces.ColorClickListener;

/**
 * Created by Borham on 12/2/14.
 */
public class ColorsRecyclerAdapter extends RecyclerView.Adapter<ColorsRecyclerAdapter.ViewHolder> {
    private int[] colors;
    private boolean[] selectedStates;
    private ColorClickListener colorClickListener;

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
    public ColorsRecyclerAdapter(ColorClickListener colorClickListener, int[] colors, boolean[] selectedStates) {
        this.colorClickListener = colorClickListener;
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

            //NOT THE RIGHT WAY TO MAKE THE TEXT ON THE BLACK BUTTON WHITE..
            //but I'm leaving it for now and tackling bigger issues
            //ALSO... YELLOW TURNS WHITE!?!?!?
            if (colors[i] == colors[0]) {
                viewHolder.colorButton.setTextColor(colors[1]);
            }
            viewHolder.colorButton.setText("SELECTED");

        } else {
            viewHolder.colorButton.setText(null);
        }

        viewHolder.colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int j = 0; j < selectedStates.length; j++) {
                    selectedStates[j] = false;
                }
                selectedStates[i] = true;

                notifyDataSetChanged();
                colorClickListener.onColorClick(i);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return colors.length;
    }
}
