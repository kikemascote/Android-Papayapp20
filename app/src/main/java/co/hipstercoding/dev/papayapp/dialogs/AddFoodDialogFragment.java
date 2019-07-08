package co.hipstercoding.dev.papayapp.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import co.hipstercoding.dev.papayapp.R;
import co.hipstercoding.dev.papayapp.data.SectionContract;
import co.hipstercoding.dev.papayapp.utils.DBUtils;
import co.hipstercoding.dev.papayapp.utils.NotifyInterfaceUtils;

import static androidx.appcompat.app.AppCompatActivity.RESULT_OK;


public class AddFoodDialogFragment extends DialogFragment implements TextWatcher, View.OnClickListener {

// --Commented out by Inspection START (08/07/2019 03:50 AM):
//    //for logging purpose
//    String TAG = this.getClass().getSimpleName();
// --Commented out by Inspection STOP (08/07/2019 03:50 AM)

    private AlertDialog dialog;

    private EditText quantityEditText;
    private EditText nameEditText;
    private Spinner unitSpinner;
    private Spinner categorySpinner;
    private Spinner sectionSpinner;
    private ImageView capturedImageView;
    private TextView dateTextView;

    private boolean nameTextValidated;
    private boolean quantityTextValidated;
    private boolean expireDateSettled;

    private Cursor sectionCursor;

    private Context context;

    private final Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date;

    private NotifyInterfaceUtils notifyInterfaceUtils;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        //initialized flags
        nameTextValidated = false;
        quantityTextValidated = false;
        expireDateSettled = false;

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.add_food_dialog, null);

        //find views from resources
        quantityEditText = dialogLayout.findViewById(R.id.edit_text_food_quantity_dialog);
        nameEditText = dialogLayout.findViewById(R.id.edit_text_food_name_dialog);
        unitSpinner = dialogLayout.findViewById(R.id.spinner_unit);
        categorySpinner = dialogLayout.findViewById(R.id.spinner_category);
        sectionSpinner = dialogLayout.findViewById(R.id.spinner_section);
        ImageView calendarImageView = dialogLayout.findViewById(R.id.calendar_image_view);
        ImageView cameraImageView = dialogLayout.findViewById(R.id.camera_image_view);
        capturedImageView = dialogLayout.findViewById(R.id.captured_image);
        dateTextView = dialogLayout.findViewById(R.id.date_text_view);

        //register listeners
        nameEditText.addTextChangedListener(this);
        quantityEditText.addTextChangedListener(this);
        calendarImageView.setOnClickListener(this);
        cameraImageView.setOnClickListener(this);

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel();
            }
        };

        //get string array of sections from db and populate the spinner view with the fetched records
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getSectionsArray());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //the drop down view
        sectionSpinner.setAdapter(spinnerArrayAdapter);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogLayout)
                // Add action buttons
                .setPositiveButton(getResources().getString(R.string.done_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        String foodName = nameEditText.getText().toString();
                        int foodUnit = unitSpinner.getSelectedItemPosition();
                        double foodQuantity = Double.parseDouble(quantityEditText.getText().toString());
                        int foodCategory = categorySpinner.getSelectedItemPosition();

                        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());

                        String foodRegisteredTimestamp = timeStamp + "";

                        String myFormat = "yyyy-MM-dd";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        String foodExpireDate = "0000-00-00";

                        //checks if the user input a date of expiry
                        if(expireDateSettled) foodExpireDate = sdf.format(myCalendar.getTime());


                        int sectionId = getSectionId(sectionSpinner.getSelectedItemPosition());

                        new DBUtils(getActivity()).insertFoodIntoDB(
                               foodName, foodUnit, foodQuantity, foodCategory, foodRegisteredTimestamp, foodExpireDate, sectionId
                        );

                        //notify through interface to host activity for updating the ui
                        notifyInterfaceUtils.onAddFood();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddFoodDialogFragment.this.getDialog().cancel();
                    }
                })
                .setTitle(getResources().getString(R.string.add_food_dialog_title));



        dialog = builder.create();
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){

            case R.id.calendar_image_view:
                dispatchDataPickerDialog();
                break;
            case R.id.camera_image_view:
                dispatchTakePictureIntent();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        handleText();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(extras).get("data");
            capturedImageView.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            // Instantiate the NotifyInterfaceUtils so we can send events to the host
            notifyInterfaceUtils = (NotifyInterfaceUtils) getActivity();
        } catch (ClassCastException e) {
            // The activity didn't implement the interface, throw exception
            throw new ClassCastException(Objects.requireNonNull(getActivity()).toString() + " must implement NotifyInterfaceUtils");
        }
    }

    private String[] getSectionsArray() {
        List<String> nameList = new ArrayList<>();
        sectionCursor = new DBUtils(getActivity()).getAllSections();
        int nameCol = sectionCursor.getColumnIndex(SectionContract.SectionEntry.COLUMN_SECTION_NAME);

        while (sectionCursor.moveToNext()) {
            nameList.add(sectionCursor.getString(nameCol));
        }

        return nameList.toArray(new String[0]);
    }

    private void handleText() {
        nameEditText.getText();
        nameTextValidated = !nameEditText.getText().toString().isEmpty();

        quantityEditText.getText();
        if (!quantityEditText.getText().toString().isEmpty()) {
            //validate that introduced text is a number
            quantityTextValidated = quantityEditText.getText().toString().matches("-?\\d+(\\.\\d+)?");
        } else {
            quantityTextValidated = false;
        }

        if(nameTextValidated && quantityTextValidated) {
            dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
        } else {
            dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
        }
    }

    private void updateDateLabel() {
        Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
        //check that selected date by user is a date after current date
        if(currentTimeStamp.before(myCalendar.getTime())){
            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            dateTextView.setText(sdf.format(myCalendar.getTime()));
            expireDateSettled = true;
        } else {
            Toast.makeText(getActivity(), context.getResources().getText(R.string.warning_invalid_date_input), Toast.LENGTH_SHORT).show();
            dateTextView.setText(context.getResources().getText(R.string.expiration_date));
            expireDateSettled = false;
        }
    }

    private void dispatchDataPickerDialog(){
        new DatePickerDialog(Objects.requireNonNull(getContext()), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private int getSectionId(int position) {
        sectionCursor = new DBUtils(getActivity()).getAllSections();

        if(sectionCursor.move(position + 1)){
            //Log.d(TAG, "Moved");
            int idCol = sectionCursor.getColumnIndex(SectionContract.SectionEntry._ID);
            return sectionCursor.getInt(idCol);
        } else {
            //Log.d(TAG, "Did not Move");
        }

        return 0;
    }


}
