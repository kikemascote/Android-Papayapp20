package co.hipstercoding.dev.papayapp;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import co.hipstercoding.dev.papayapp.R;

import co.hipstercoding.dev.papayapp.data.Food;
import co.hipstercoding.dev.papayapp.data.Section;
import co.hipstercoding.dev.papayapp.dialogs.EditFoodDialogFragment;
import co.hipstercoding.dev.papayapp.utils.DBUtils;
import co.hipstercoding.dev.papayapp.utils.FoodUtils;

import static co.hipstercoding.dev.papayapp.MainActivity.DELETED_FOOD;
import static co.hipstercoding.dev.papayapp.SectionAdapter.FOOD_ID;
import static co.hipstercoding.dev.papayapp.utils.StaticVarsUtils.EXCEPTION_EXTRA;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, EditFoodDialogFragment.DetailActivityNotifyInterface{

// --Commented out by Inspection START (08/07/2019 03:54 AM):
//    //for logging purpose
//    String TAG = this.getClass().getSimpleName();
// --Commented out by Inspection STOP (08/07/2019 03:54 AM)

    private ImageView foodAsset;
    private TextView expirationDateText, sectionText, unitText, categoryText, quantityText, eatAllButton;

    private int foodId = 0;
    private Food food;
    private boolean exception = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        foodAsset = findViewById(R.id.detail_food_asset);
        ImageView removeButton = findViewById(R.id.detail_remove_button);
        ImageView addButton = findViewById(R.id.detail_add_button);
        expirationDateText = findViewById(R.id.detail_expiry_date_text);
        sectionText = findViewById(R.id.detail_section_text);
        unitText = findViewById(R.id.detail_unit_text);
        categoryText = findViewById(R.id.detail_category_text);
        quantityText = findViewById(R.id.detail_quantity_text);
        eatAllButton = findViewById(R.id.detail_eat_all_button);

        removeButton.setOnClickListener(this);
        addButton.setOnClickListener(this);

        Intent intent = getIntent();

        //populate ui
        if(intent.hasExtra(FOOD_ID)) {
            foodId = intent.getIntExtra(FOOD_ID, 0);

            populateUi(foodId);
        }

        if(intent.hasExtra(EXCEPTION_EXTRA)) exception = intent.getBooleanExtra(EXCEPTION_EXTRA, false);
    }

    //handle each click event
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {

            case R.id.detail_eat_all_button:
                new DBUtils(this).deleteFoodById(foodId);

                if (exception) {//handle case if user navigate to this activity from widget or notification
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra(DELETED_FOOD, food);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent();
                    intent.putExtra(DELETED_FOOD, food);
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }

                break;

            case R.id.detail_remove_button:

                if(food.foodUnit == 0) {
                    if(food.foodQuantity > 0) food.foodQuantity -= 1;
                    else Toast.makeText(this, R.string.warning_negative_case, Toast.LENGTH_SHORT).show();
                } else {
                    if(food.foodQuantity > 0) food.foodQuantity -= 0.1;
                    else Toast.makeText(this, R.string.warning_negative_case, Toast.LENGTH_SHORT).show();
                    food.foodQuantity = Math.round( food.foodQuantity * 10.0 ) / 10.0;//avoid floating point arithmetic problem
                }

                new DBUtils(this).updateFood(food);
                populateUi(food.foodId);

                break;

            case R.id.detail_add_button:

                if(food.foodUnit == 0) {
                    food.foodQuantity += 1;
                } else {
                    food.foodQuantity += 0.1;
                    food.foodQuantity = Math.round( food.foodQuantity * 10.0 ) / 10.0;//avoid floating point arithmetic problem
                }

                new DBUtils(this).updateFood(food);
                populateUi(food.foodId);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            DialogFragment newFragment = new EditFoodDialogFragment();
            Bundle foodIdBundle = new Bundle();
            foodIdBundle.putInt(FOOD_ID, foodId);
            newFragment.setArguments(foodIdBundle);
            newFragment.show(getSupportFragmentManager(), String.valueOf(R.string.edit_food_dialog_title));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditFinished(int foodId) {
        populateUi(foodId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateToMain();
    }

    @Override
    public boolean onSupportNavigateUp(){
        navigateToMain();
        finish();
        return true;
    }

    private void populateUi(int foodId) {
        food = new DBUtils(this).getFoodById(foodId);
        Section section = new DBUtils(this).getSectionById(food.sectionId);

        Objects.requireNonNull(getSupportActionBar()).setTitle(food.foodName);

        foodAsset.setImageResource(FoodUtils.getFoodAsset(food.foodCategory));
        expirationDateText.setText(getResources().getString(R.string.expiration_label) + " " + food.foodExpireDate);
        sectionText.setText(getResources().getString(R.string.section_label) + " " + section.sectionName);
        unitText.setText(getResources().getString(R.string.unit_label)  + " " + new FoodUtils(this).getUnitString(food.foodUnit));
        categoryText.setText(getResources().getString(R.string.category_label)  + " " + new FoodUtils(this).getCategoryName(food.foodCategory));
        quantityText.setText(getResources().getString(R.string.quantity_label)  + " " + food.foodQuantity);
        if(food.foodUnit == 0) quantityText.setText(getResources().getString(R.string.quantity_label) + (int) food.foodQuantity);
        eatAllButton.setOnClickListener(this);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}
