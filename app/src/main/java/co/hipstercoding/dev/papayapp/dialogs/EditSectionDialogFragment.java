package co.hipstercoding.dev.papayapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Objects;

import co.hipstercoding.dev.papayapp.R;
import co.hipstercoding.dev.papayapp.data.Section;
import co.hipstercoding.dev.papayapp.utils.DBUtils;

import static co.hipstercoding.dev.papayapp.SectionAdapter.SECTION_ID;



public class EditSectionDialogFragment extends DialogFragment implements View.OnClickListener {

    private AlertDialog dialog;

// --Commented out by Inspection START (08/07/2019 03:54 AM):
//    //for logging purpose
//    String TAG = this.getClass().getSimpleName();
// --Commented out by Inspection STOP (08/07/2019 03:54 AM)

    private EditText nameEditText;
    private ImageView colorView1, colorView2, colorView3, colorView4, colorView5;
    private int selectedColor;

    private MainActivityNotifyInterface mainActivityNotifyInterface;

    private DBUtils dbUtils;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        dbUtils = new DBUtils(getActivity());

        Bundle sectionIdBundle = getArguments();
        final int sectionId = Objects.requireNonNull(sectionIdBundle).getInt(SECTION_ID);

        final Section sectionToEdit = dbUtils.getSectionById(sectionId);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogLayout = inflater.inflate(R.layout.add_section_dialog, null);

        //color selection from selected section
        selectedColor = sectionToEdit.sectionColor;

        //find views from resources
        nameEditText = dialogLayout.findViewById(R.id.edit_text_add_section_dialog);
        colorView1 = dialogLayout.findViewById(R.id.color_view_1);
        colorView2 = dialogLayout.findViewById(R.id.color_view_2);
        colorView3 = dialogLayout.findViewById(R.id.color_view_3);
        colorView4 = dialogLayout.findViewById(R.id.color_view_4);
        colorView5 = dialogLayout.findViewById(R.id.color_view_5);

        //fill name field
        nameEditText.setText(sectionToEdit.sectionName);

        selectCorrespondingAsset(sectionToEdit.sectionColor);

        //set up listeners to ImageViews
        colorView1.setOnClickListener(this);
        colorView2.setOnClickListener(this);
        colorView3.setOnClickListener(this);
        colorView4.setOnClickListener(this);
        colorView5.setOnClickListener(this);

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                handleText();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing to do
            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogLayout)
                // Add action buttons
                .setPositiveButton(getResources().getString(R.string.edit_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //new DBUtils(getActivity()).insertSectionIntoDB(, selectedColor);

                        String sectionName = nameEditText.getText().toString();
                        Section sectionToUpdate = new Section(sectionToEdit.sectionId, sectionName, selectedColor);

                        dbUtils.updateSection(sectionToUpdate);

                        //notify through interface to host activity for updating the ui
                        mainActivityNotifyInterface.onEditFinished();
                        //Log.d(TAG, "Inserted: " + nameEditText.getText().toString() + "and" + selectedColor);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditSectionDialogFragment.this.getDialog().cancel();
                    }
                })
                .setTitle(getResources().getString(R.string.add_section_dialog_title));

        dialog = builder.create();
        return dialog;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {

            case R.id.color_view_1:
                selectedColor = ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.colorOption1);
                colorView1.setImageResource(R.drawable.selected_circle_color_1);
                colorView2.setImageResource(R.drawable.circle_color_2);
                colorView3.setImageResource(R.drawable.circle_color_3);
                colorView4.setImageResource(R.drawable.circle_color_4);
                colorView5.setImageResource(R.drawable.circle_color_5);
                break;

            case R.id.color_view_2:
                selectedColor = ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.colorOption2);
                colorView1.setImageResource(R.drawable.circle_color_1);
                colorView2.setImageResource(R.drawable.selected_circle_color_2);
                colorView3.setImageResource(R.drawable.circle_color_3);
                colorView4.setImageResource(R.drawable.circle_color_4);
                colorView5.setImageResource(R.drawable.circle_color_5);
                break;

            case R.id.color_view_3:
                selectedColor = ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.colorOption3);
                colorView1.setImageResource(R.drawable.circle_color_1);
                colorView2.setImageResource(R.drawable.circle_color_2);
                colorView3.setImageResource(R.drawable.selected_circle_color_3);
                colorView4.setImageResource(R.drawable.circle_color_4);
                colorView5.setImageResource(R.drawable.circle_color_5);
                break;

            case R.id.color_view_4:
                selectedColor = ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.colorOption4);
                colorView1.setImageResource(R.drawable.circle_color_1);
                colorView2.setImageResource(R.drawable.circle_color_2);
                colorView3.setImageResource(R.drawable.circle_color_3);
                colorView4.setImageResource(R.drawable.selected_circle_color_4);
                colorView5.setImageResource(R.drawable.circle_color_5);
                break;

            case R.id.color_view_5:
                selectedColor = ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.colorOption5);
                colorView1.setImageResource(R.drawable.circle_color_1);
                colorView2.setImageResource(R.drawable.circle_color_2);
                colorView3.setImageResource(R.drawable.circle_color_3);
                colorView4.setImageResource(R.drawable.circle_color_4);
                colorView5.setImageResource(R.drawable.selected_circle_color_5);
                break;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the NotifyInterfaceUtils so we can send events to the host
            mainActivityNotifyInterface = (EditSectionDialogFragment.MainActivityNotifyInterface) getActivity();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(Objects.requireNonNull(getActivity()).toString() + " must implement MainActivityNotifyInterface");
        }
    }

    private void selectCorrespondingAsset(int color) {

        if (ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.colorOption1) == color) {

            selectedColor = ContextCompat.getColor(getActivity(), R.color.colorOption1);
            colorView1.setImageResource(R.drawable.selected_circle_color_1);
            colorView2.setImageResource(R.drawable.circle_color_2);
            colorView3.setImageResource(R.drawable.circle_color_3);
            colorView4.setImageResource(R.drawable.circle_color_4);
            colorView5.setImageResource(R.drawable.circle_color_5);

        } else if(ContextCompat.getColor(getActivity(), R.color.colorOption2) == color) {

            selectedColor = ContextCompat.getColor(getActivity(), R.color.colorOption2);
            colorView1.setImageResource(R.drawable.circle_color_1);
            colorView2.setImageResource(R.drawable.selected_circle_color_2);
            colorView3.setImageResource(R.drawable.circle_color_3);
            colorView4.setImageResource(R.drawable.circle_color_4);
            colorView5.setImageResource(R.drawable.circle_color_5);

        } else if(ContextCompat.getColor(getActivity(), R.color.colorOption3) == color) {

            selectedColor = ContextCompat.getColor(getActivity(), R.color.colorOption3);
            colorView1.setImageResource(R.drawable.circle_color_1);
            colorView2.setImageResource(R.drawable.circle_color_2);
            colorView3.setImageResource(R.drawable.selected_circle_color_3);
            colorView4.setImageResource(R.drawable.circle_color_4);
            colorView5.setImageResource(R.drawable.circle_color_5);

        } else if(ContextCompat.getColor(getActivity(), R.color.colorOption4) == color) {

            selectedColor = ContextCompat.getColor(getActivity(), R.color.colorOption4);
            colorView1.setImageResource(R.drawable.circle_color_1);
            colorView2.setImageResource(R.drawable.circle_color_2);
            colorView3.setImageResource(R.drawable.circle_color_3);
            colorView4.setImageResource(R.drawable.selected_circle_color_4);
            colorView5.setImageResource(R.drawable.circle_color_5);

        } else if(ContextCompat.getColor(getActivity(), R.color.colorOption5) == color) {

            selectedColor = ContextCompat.getColor(getActivity(), R.color.colorOption5);
            colorView1.setImageResource(R.drawable.circle_color_1);
            colorView2.setImageResource(R.drawable.circle_color_2);
            colorView3.setImageResource(R.drawable.circle_color_3);
            colorView4.setImageResource(R.drawable.circle_color_4);
            colorView5.setImageResource(R.drawable.selected_circle_color_5);

        }

    }

    private void handleText() {
        nameEditText.getText();
        if (!nameEditText.getText().toString().isEmpty()) {
            dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
        } else {
            dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
        }
    }

    //helper interface to pass event back to host activity
    public interface MainActivityNotifyInterface {

        void onEditFinished();

    }
}
