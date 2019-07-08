package co.hipstercoding.dev.papayapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import co.hipstercoding.dev.papayapp.R;
import co.hipstercoding.dev.papayapp.data.Food;
import co.hipstercoding.dev.papayapp.data.SectionContract;
import co.hipstercoding.dev.papayapp.utils.DBUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static co.hipstercoding.dev.papayapp.SectionAdapter.SECTION_ID;


public class MoveFoodDialogFragment extends DialogFragment {

    // --Commented out by Inspection START (08/07/2019 03:55 AM):
//    //for logging purpose
//    String TAG = this.getClass().getSimpleName();
// --Commented out by Inspection STOP (08/07/2019 03:55 AM)
    private MainActivityNotifyInterface mainActivityNotifyInterface;

    private Spinner sectionSpinner;

    private DBUtils dbUtils;

    //holds foods to be moved
    private final List<Food> foodsToMove = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        dbUtils = new DBUtils(getActivity());

        Bundle sectionIdBundle = getArguments();
        final int sectionId = Objects.requireNonNull(sectionIdBundle).getInt(SECTION_ID);

        Food[] foodsOfSection = dbUtils.getFoodsOfSection(sectionId);


        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogLayout = inflater.inflate(R.layout.dialog_move_food, null);

        LinearLayout checkBoxContainer = dialogLayout.findViewById(R.id.food_checkboxes_container);
        sectionSpinner = dialogLayout.findViewById(R.id.move_food_spinner_section);


        //populate checkboxes
        for (Food food:foodsOfSection) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(food.foodName);
            final Food foodToMove = food;
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if(checked) {
                        foodsToMove.add(foodToMove);
                    } else {
                        foodsToMove.remove(foodToMove);
                    }
                }
            });
            checkBoxContainer.addView(checkBox);
        }

        //get string array of sections from db and populate the spinner view with the fetched records
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getSectionsArray());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //the drop down view
        sectionSpinner.setAdapter(spinnerArrayAdapter);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogLayout)
                .setPositiveButton(getResources().getString(R.string.move_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        for (Food foodToUpdate: foodsToMove) {
                            foodToUpdate.sectionId = getSectionId(sectionSpinner.getSelectedItemPosition());
                            dbUtils.updateFood(foodToUpdate);
                        }

                        mainActivityNotifyInterface.onMoved();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MoveFoodDialogFragment.this.getDialog().cancel();
                    }
                })
                .setTitle(getResources().getString(R.string.move_food_title));

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the NotifyInterfaceUtils so we can send events to the host
            mainActivityNotifyInterface = (MoveFoodDialogFragment.MainActivityNotifyInterface) getActivity();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(Objects.requireNonNull(getActivity()).toString() + " must implement MainActivityNotifyInterface");
        }
    }

    //helper interface to pass event back to host activity
    public interface MainActivityNotifyInterface {

        void onMoved();

    }

    private String[] getSectionsArray() {
        List<String> nameList = new ArrayList<>();
        Cursor sectionCursor = dbUtils.getAllSections();
        int nameCol = sectionCursor.getColumnIndex(SectionContract.SectionEntry.COLUMN_SECTION_NAME);

        while (sectionCursor.moveToNext()) {
            nameList.add(sectionCursor.getString(nameCol));
        }

        return nameList.toArray(new String[0]);
    }

    private int getSectionId(int position) {
        Cursor sectionCursor = new DBUtils(getActivity()).getAllSections();

        if(sectionCursor.move(position + 1)){
            int idCol = sectionCursor.getColumnIndex(SectionContract.SectionEntry._ID);
            return sectionCursor.getInt(idCol);
        }

        return 0;
    }
}