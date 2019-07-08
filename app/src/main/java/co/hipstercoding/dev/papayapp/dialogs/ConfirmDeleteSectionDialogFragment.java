package co.hipstercoding.dev.papayapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;

import java.util.Objects;

import co.hipstercoding.dev.papayapp.R;

import static co.hipstercoding.dev.papayapp.SectionAdapter.SECTION_ID;


public class ConfirmDeleteSectionDialogFragment extends DialogFragment {

    // --Commented out by Inspection START (08/07/2019 03:54 AM):
//    //for logging purpose
//    String TAG = this.getClass().getSimpleName();
// --Commented out by Inspection STOP (08/07/2019 03:54 AM)
    private MainActivityNotifyInterface mainActivityNotifyInterface;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        Bundle sectionIdBundle = getArguments();
        final int sectionId = Objects.requireNonNull(sectionIdBundle).getInt(SECTION_ID);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_confirm_delete_section, null))
                .setPositiveButton(getResources().getString(R.string.delete_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mainActivityNotifyInterface.onConfirmedDeletion(sectionId);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ConfirmDeleteSectionDialogFragment.this.getDialog().cancel();
                    }
                })
                .setTitle(getResources().getString(R.string.confirm_title));

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the NotifyInterfaceUtils so we can send events to the host
            mainActivityNotifyInterface = (ConfirmDeleteSectionDialogFragment.MainActivityNotifyInterface) getActivity();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(Objects.requireNonNull(getActivity()).toString() + " must implement MainActivityNotifyInterface");
        }
    }

    //helper interface to pass event back to host activity
    public interface MainActivityNotifyInterface {

        void onConfirmedDeletion(int sectionId);

    }
}