package co.hipstercoding.dev.papayapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import co.hipstercoding.dev.papayapp.R;

import co.hipstercoding.dev.papayapp.data.Food;
import co.hipstercoding.dev.papayapp.data.Section;
import co.hipstercoding.dev.papayapp.dialogs.ConfirmDeleteSectionDialogFragment;
import co.hipstercoding.dev.papayapp.dialogs.EditSectionDialogFragment;
import co.hipstercoding.dev.papayapp.dialogs.MoveFoodDialogFragment;
import co.hipstercoding.dev.papayapp.utils.DBUtils;
import co.hipstercoding.dev.papayapp.utils.FoodUtils;
import co.hipstercoding.dev.papayapp.utils.NotifyInterfaceUtils;


public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionAdapterViewHolder> {

    //for logging purpose
    private final String TAG = this.getClass().getSimpleName();

    public static final String FOOD_ID = "foodId";
    public static final String SECTION_ID = "sectionID";
    public static final int REQUEST_FOR_ACTIVITY_CODE = 1;

    private final Section[] sections;
    private final Context context;
    private final int sortOption;
    private final NotifyInterfaceUtils notifyInterfaceUtils;
    private final FragmentManager fragmentManager;

    public SectionAdapter(Section[] sections, Context context, int sortOption, NotifyInterfaceUtils notifyInterfaceUtils, FragmentManager fragmentManager) {
        this.sections = sections;
        this.context = context;
        this.sortOption = sortOption;
        this.notifyInterfaceUtils = notifyInterfaceUtils;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public SectionAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.section_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new SectionAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SectionAdapterViewHolder holder, int position) {
        //solution for duplication of items because each time the recycler view called this method, accumulated items from before instance
        holder.linearLayoutFoodContainer.removeAllViews();
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return sections.length;
    }

    public class SectionAdapterViewHolder extends  RecyclerView.ViewHolder {

        final TextView listItemTitle;
        final ImageView moreOptionButton;
        final LinearLayout linearLayoutFoodContainer;


        SectionAdapterViewHolder(View itemView) {
            super(itemView);
            listItemTitle = itemView.findViewById(R.id.text_view_section_name);
            moreOptionButton = itemView.findViewById(R.id.more_option_button);
            linearLayoutFoodContainer = itemView.findViewById(R.id.linear_layout_food_container);
        }

        void bind(int position) {
            listItemTitle.setText(sections[position].sectionName);
            final int sectionPosition = position;

            moreOptionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(context, view);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_section);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.action_edit_section:
                                    DialogFragment editSectionDialogFragment = new EditSectionDialogFragment();
                                    //pass section id when pressed
                                    Bundle editDialogBundle = new Bundle();
                                    editDialogBundle.putInt(SECTION_ID, sections[sectionPosition].sectionId);
                                    editSectionDialogFragment.setArguments(editDialogBundle);
                                    editSectionDialogFragment.show(fragmentManager, "edit_section");
                                    break;

                                case R.id.action_delete_section:
                                    DialogFragment confirmDialogFragment = new ConfirmDeleteSectionDialogFragment();
                                    //pass section id when pressed
                                    Bundle confirmDialogBundle = new Bundle();
                                    confirmDialogBundle.putInt(SECTION_ID, sections[sectionPosition].sectionId);
                                    confirmDialogFragment.setArguments(confirmDialogBundle);
                                    confirmDialogFragment.show(fragmentManager, "confirm_deletion");
                                    break;

                                case R.id.action_move_food:

                                    if(new DBUtils(context).getFoodsOfSection(sections[sectionPosition].sectionId).length != 0) {
                                        DialogFragment moveFoodDialogFragment = new MoveFoodDialogFragment();
                                        //pass section id when pressed
                                        Bundle moveFoodBundle = new Bundle();
                                        moveFoodBundle.putInt(SECTION_ID, sections[sectionPosition].sectionId);
                                        moveFoodDialogFragment.setArguments(moveFoodBundle);
                                        moveFoodDialogFragment.show(fragmentManager, "move_food");
                                    } else {
                                        Toast.makeText(context, "There are no foods inside this section", Toast.LENGTH_SHORT).show();
                                    }

                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();

                }
            });


            listItemTitle.setTextColor(sections[position].sectionColor);


            //get all foods from the db
            Food[] foodsArray;

            switch (sortOption) {

                case 0: //without sort
                    foodsArray = new DBUtils(context).getAllFoodsArray();
                    break;

                case 1: //sort by expiration date
                    foodsArray = FoodUtils.sortByExpirationDate(context);
                    break;

                case 2://sort by ascending
                    foodsArray = FoodUtils.sortByAscending(context);
                    break;

                case 3://sort by descending
                    foodsArray = FoodUtils.sortByDescending(context);
                    break;

                default:
                    foodsArray = new DBUtils(context).getAllFoodsArray();
                    break;

            }

            //food item creation
            for (Food food : foodsArray) {

                //if the pointed food has the same section id, that means that correspond to the pointed section
                if (food.sectionId == sections[position].sectionId) {

                    //get root view from layout food_list_item.xml and inflate as a LinearLayout view object
                    final LinearLayout rootView;
                    final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    rootView = (LinearLayout) Objects.requireNonNull(inflater).inflate(R.layout.food_list_item, null);

                    //find views
                    ImageView circleBackGround = rootView.findViewById(R.id.image_view_circle_back_ground);
                    ImageView foodImageView = rootView.findViewById(R.id.image_view_food);
                    TextView foodNameTextView = rootView.findViewById(R.id.text_view_food_name);
                    TextView foodQuantityTextView = rootView.findViewById(R.id.text_view_food_quantity);
                    ImageView calendarImageView = rootView.findViewById(R.id.image_view_calendar);
                    TextView expirationDateTextView = rootView.findViewById(R.id.text_view_expiration_date);

                    //set up attributes
                    Drawable circleDrawable = context.getResources().getDrawable(R.drawable.circle_color_1, null);
                    circleDrawable.setColorFilter(new PorterDuffColorFilter(sections[position].sectionColor, PorterDuff.Mode.SRC_IN));
                    circleBackGround.setBackground(circleDrawable);

                    foodImageView.setImageResource(FoodUtils.getFoodAsset(food.foodCategory));

                    foodNameTextView.setText(food.foodName);

                    foodQuantityTextView.setText(food.foodQuantity + " " + new FoodUtils(context).getUnitString(food.foodUnit));
                    if (food.foodUnit == 0)
                        foodQuantityTextView.setText((int) food.foodQuantity + " " + new FoodUtils(context).getUnitString(food.foodUnit));

                    Drawable calendarDrawable = context.getResources().getDrawable(R.drawable.ic_calendar, null).mutate();
                    calendarDrawable.setColorFilter(new PorterDuffColorFilter(sections[position].sectionColor, PorterDuff.Mode.SRC_IN));
                    calendarImageView.setImageDrawable(calendarDrawable);

                    if (food.foodExpireDate.equals("0000-00-00")) {
                        expirationDateTextView.setText(context.getResources().getString(R.string.without_date));
                    } else {
                        expirationDateTextView.setText(food.foodExpireDate);
                    }


                    if (FoodUtils.foodAboutToExpire(food.foodExpireDate, context)) {
                        expirationDateTextView.setTextColor(ContextCompat.getColor(context, R.color.expireText));
                    }

                    //declare final because the array lose the scope inside the listener method definition
                    final int foodId = food.foodId;

                    //handle swipe animation
                    //test
                    //conversion of pixels to corresponding dps
                    //substitute parameters for left, top, right, bottom
                    //allow only right movement
                    //substitute parameters for left, top, right, bottom
                    //if there are more than horizontal 400 dp distance, call erase method
                    //avoid stacking item when users scroll up or down while swiping
                    //Log.d(TAG, "Test listener with section Id: " + foodId);
                    CustomGestureDetector customGestureDetector = new CustomGestureDetector(context) {

                        double distanceSum = 0;
                        int intDistanceSum = 0;

                        final View viewInstance = rootView.findViewById(R.id.image_view_circle_back_ground);
                        //test

                        final Resources resources = context.getResources();
                        //conversion of pixels to corresponding dps
                        final int topBottomDPs = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, resources.getDisplayMetrics());
                        final int leftDPs = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, resources.getDisplayMetrics());
                        final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewInstance.getLayoutParams();

                        @Override
                        void setSumToDefault() {
                            distanceSum = 0;
                            intDistanceSum = 0;

                            params.setMargins(leftDPs, topBottomDPs, 0, topBottomDPs);//substitute parameters for left, top, right, bottom
                            viewInstance.setLayoutParams(params);
                        }

                        @Override
                        public void customScroll(float horizontalDistance, float verticalDistance) {
                            distanceSum += horizontalDistance;
                            intDistanceSum += horizontalDistance * -1;
                            Log.d(TAG, "Vertical Distance : " + verticalDistance);

                            //allow only right movement
                            if (horizontalDistance * -1 > 0) {
                                params.setMargins(intDistanceSum, topBottomDPs, 0, topBottomDPs);//substitute parameters for left, top, right, bottom
                                viewInstance.setLayoutParams(params);
                            }

                            //if there are more than horizontal 400 dp distance, call erase method
                            if (distanceSum <= -400) eraseFood();

                            //avoid stacking item when users scroll up or down while swiping
                            if (verticalDistance > 5 || verticalDistance < -5) {
                                setSumToDefault();
                            }

                        }

                        @Override
                        public void simpleClick() {
                            //Log.d(TAG, "Test listener with section Id: " + foodId);
                            Intent intent = new Intent(context, DetailActivity.class);
                            intent.putExtra(FOOD_ID, foodId);
                            ((Activity) context).startActivityForResult(intent, REQUEST_FOR_ACTIVITY_CODE);
                        }

                        void eraseFood() {
                            DBUtils dbUtils = new DBUtils(context, notifyInterfaceUtils);

                            Food deletedFood = dbUtils.getFoodById(foodId);
                            dbUtils.deleteFoodById(foodId);
                            notifyInterfaceUtils.deleteFood(deletedFood);
                        }
                    };

                    rootView.setOnTouchListener(customGestureDetector);

                    linearLayoutFoodContainer.addView(rootView);

                    //Log.d(TAG, "Corresponding to section: " + sections[position].sectionName + " Child count: " + linearLayoutFoodContainer.getChildCount());

                }

            }

        }
    }

}
