package com.detroitlabs.cartograffi.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.detroitlabs.cartograffi.R;

/**
 * Created by BFineRocks on 1/7/15.
 */
public class DeleteConfirmationDialogFragment extends DialogFragment {

    public interface DeleteConfirmationInterface {
        public void onDialogClick(boolean confirmationClick);
    }

    private DeleteConfirmationInterface confirmationInterface;

    public static DeleteConfirmationDialogFragment newInstance(DeleteConfirmationInterface deleteConfirmationInterface){
        DeleteConfirmationDialogFragment deleteDialog = new DeleteConfirmationDialogFragment();
        deleteDialog.confirmationInterface = deleteConfirmationInterface;
        return deleteDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder deleteConfirmation = new AlertDialog.Builder(getActivity());
        deleteConfirmation.setTitle(R.string.delete_confirm_title);
        deleteConfirmation.setMessage(R.string.delete_confirm_message);
        deleteConfirmation.setPositiveButton(R.string.delete_confirm_positive, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmationInterface.onDialogClick(true);
            }
        });
        deleteConfirmation.setNegativeButton(R.string.delete_confirm_negative, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmationInterface.onDialogClick(false);
            }
        });
        return deleteConfirmation.create();
    }
}
