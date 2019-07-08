package co.hipstercoding.dev.papayapp.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import co.hipstercoding.dev.papayapp.R;
import co.hipstercoding.dev.papayapp.data.Food;
import co.hipstercoding.dev.papayapp.utils.DBUtils;
import co.hipstercoding.dev.papayapp.utils.FoodUtils;

import java.util.ArrayList;
import java.util.List;

import static co.hipstercoding.dev.papayapp.utils.StaticVarsUtils.EXCEPTION_EXTRA;
import static co.hipstercoding.dev.papayapp.utils.StaticVarsUtils.FOOD_ID;


public class GridWidgetService extends RemoteViewsService {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }

    class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        final Context mContext;
        Food[] foods;

        GridRemoteViewsFactory(Context applicationContext) {
            mContext = applicationContext;
            List<Food> foodList = new ArrayList<>();

            //only add food that is about to expire according to the setting
            for (Food food: new DBUtils(mContext).getAllFoodsArray()) {
                if(FoodUtils.foodAboutToExpire(food.foodExpireDate, mContext)) foodList.add(food);
            }

            foods = getProperFoods();
        }

        //called on start and when notifyAppWidgetViewDataChanged is called
        @Override
        public void onCreate() {

        }

        //called on start and when notifyAppWidgetViewDataChanged is called
        @Override
        public void onDataSetChanged() {
            Log.d(TAG, "Called onDataSetChanged method");
            //update the food array
            foods = getProperFoods();
        }

        @Override
        public void onDestroy() {
            foods = null;
        }

        @Override
        public int getCount() {
            if (foods == null) return 0;
            return foods.length;
        }

        /**
         * This method acts like the onBindViewHolder method in an Adapter
         *
         * @param position The current position of the item in the GridView to be displayed
         * @return The RemoteViews object to display for the provided position
         */
        @Override
        public RemoteViews getViewAt(int position) {
            if (foods == null || foods.length == 0) return null;

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.food_widget);

            //set text with retrieved data from cursor
            views.setTextViewText(R.id.food_name_widget, foods[position].foodName);
            views.setImageViewResource(R.id.food_asset_widget, FoodUtils.getFoodAsset(foods[position].foodCategory));

            // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
            Intent fillInIntent = new Intent();

            //you can put something extra at this point for each item individually
            fillInIntent.putExtra(FOOD_ID, foods[position].foodId);
            fillInIntent.putExtra(EXCEPTION_EXTRA, true);

            //set listener at widget layout text layout element
            views.setOnClickFillInIntent(R.id.food_asset_widget, fillInIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        // Treat all items in the GridView the same
        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        Food[] getProperFoods() {
            List<Food> foodList = new ArrayList<>();

            //only add food that is about to expire according to the setting
            for (Food food: new DBUtils(mContext).getAllFoodsArray()) {
                if(FoodUtils.foodAboutToExpire(food.foodExpireDate, mContext)) foodList.add(food);
            }

            return foodList.toArray(new Food[0]);
        }
    }
}
