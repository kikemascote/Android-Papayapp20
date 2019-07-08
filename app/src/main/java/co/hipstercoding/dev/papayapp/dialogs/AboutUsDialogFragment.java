package co.hipstercoding.dev.papayapp.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;

import java.util.Objects;

import co.hipstercoding.dev.papayapp.R;



public class AboutUsDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        builder.setView(inflater.inflate(R.layout.dialog_about_us, null));

        return builder.create();
    }
}
