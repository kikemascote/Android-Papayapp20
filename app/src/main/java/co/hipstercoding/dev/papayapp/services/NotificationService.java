package co.hipstercoding.dev.papayapp.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import android.util.Log;

import co.hipstercoding.dev.papayapp.DetailActivity;
import co.hipstercoding.dev.papayapp.R;
import co.hipstercoding.dev.papayapp.data.Food;
import co.hipstercoding.dev.papayapp.utils.DBUtils;
import co.hipstercoding.dev.papayapp.utils.FoodUtils;

import java.util.Date;
import java.util.Objects;

import static co.hipstercoding.dev.papayapp.SectionAdapter.FOOD_ID;
import static co.hipstercoding.dev.papayapp.utils.StaticVarsUtils.EXCEPTION_EXTRA;
import static co.hipstercoding.dev.papayapp.utils.StaticVarsUtils.NOTIFICATION_DELIVERED_DAY;
import static co.hipstercoding.dev.papayapp.utils.StaticVarsUtils.NOTIFICATION_DELIVERED_TODAY;
import static co.hipstercoding.dev.papayapp.utils.StaticVarsUtils.SEND_NOTIFICATION_KEY;
import static co.hipstercoding.dev.papayapp.utils.StaticVarsUtils.VIBRATE_WITH_NOTIFICATION_KEY;


public class NotificationService extends IntentService {

    //for logging purpose
    private final String TAG = this.getClass().getSimpleName();

    public static final String ACTION_CREATE_NOTIFICATION = "co.hipstercoding.dev.papayapp.services.action.create_notification";

    public NotificationService() {
        super("NotificationService");
    }

    public static void startActionCreateNotification(Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_CREATE_NOTIFICATION);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CREATE_NOTIFICATION.equals(action)) {
                handleActionCreateNotification();
            }
        }
    }

    private void handleActionCreateNotification() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        Food[] foods = new DBUtils(this).getAllFoodsArray();


        //create notification for each food that is about to expire
        for (Food food : foods) {

            if (FoodUtils.foodAboutToExpire(food.foodExpireDate, this)) {//get x from settings

                Log.d(TAG, "true for: " + food.foodName);

                NotificationCompat.Builder notificationBuilder;


                if (getVibrationSetting()) {//vibrate at receiving notification
                    notificationBuilder = new NotificationCompat.Builder(this, "1")
                            .setColor(ContextCompat.getColor(this, R.color.customPrimary))// - has a color of R.colorPrimary - use ContextCompat.getColor to get a compatible color
                            .setSmallIcon(FoodUtils.getFoodAsset(food.foodCategory))// -  small icon
                            .setLargeIcon(largeIcon(this, food.foodCategory))// - uses icon returned by the largeIcon helper method as the large icon
                            .setContentTitle(this.getResources().getString(R.string.notification_first_text))// - sets the title
                            .setContentText(food.foodName + " " + this.getResources().getString(R.string.notification_second_text))// - sets the text
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(food.foodName + " " + this.getResources().getString(R.string.notification_second_text)))// - sets the style to NotificationCompat.BigTextStyle().bigText(text)
                            .setDefaults(Notification.DEFAULT_VIBRATE)// - sets the notification defaults to vibrate
                            .setContentIntent(contentIntent(this, food.foodId))// - uses the content intent returned by the contentIntent helper method for the contentIntent
                            .setAutoCancel(true);// - automatically cancels the notification when the notification is clicked
                } else {// not vibrating NotificationCompat.Builder(Context context, String channelId)
                    notificationBuilder = new NotificationCompat.Builder(this, "1")
                            .setColor(ContextCompat.getColor(this, R.color.customPrimary))// - has a color of R.colorPrimary - use ContextCompat.getColor to get a compatible color
                            .setSmallIcon(FoodUtils.getFoodAsset(food.foodCategory))
                            .setLargeIcon(largeIcon(this, food.foodCategory))// - uses icon returned by the largeIcon helper method as the large icon
                            .setContentTitle(this.getResources().getString(R.string.notification_first_text))// - sets the title
                            .setContentText(food.foodName + " " + this.getResources().getString(R.string.notification_second_text))// - sets the text
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(food.foodName + " " + this.getResources().getString(R.string.notification_second_text)))// - sets the style to NotificationCompat.BigTextStyle().bigText(text)
                            .setContentIntent(contentIntent(this, food.foodId))// - uses the content intent returned by the contentIntent helper method for the contentIntent
                            .setAutoCancel(true);// - automatically cancels the notification when the notification is clicked
                }

                // if the build version is greater than JELLY_BEAN, set the notification's priority
                // to PRIORITY_HIGH.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
                }

                //get a NotificationManager
                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                //trigger the notification by calling notify on the NotificationManager.
                //pass in a unique ID of your choosing for the notification and notificationBuilder.build

                if (sharedPreferences.getBoolean(SEND_NOTIFICATION_KEY, false))
                    Objects.requireNonNull(notificationManager).notify(food.foodId, notificationBuilder.build());

            }
        }

        //mark as delivered for preventing multiple instances of alarms
        //Log.d(TAG, "Mark notification as delivered today");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATION_DELIVERED_TODAY, true);
        editor.putInt(NOTIFICATION_DELIVERED_DAY, new Date().getDate());
        editor.commit();

    }

    private boolean getVibrationSetting() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        return sharedPreferences.getBoolean(VIBRATE_WITH_NOTIFICATION_KEY, false);
    }

    // helper method called that takes in a Context and foodCategory as a parameter and
    // returns a Bitmap. This method is necessary to decode a bitmap needed for the notification.
    private static Bitmap largeIcon(Context context, int foodCategory) {
        // Get a Resources object from the context.
        Resources res = context.getResources();

        // Create and return a bitmap using BitmapFactory.decodeResource, passing in the
        // resources object and R.drawable.ic_android
        return BitmapFactory.decodeResource(res, FoodUtils.getFoodAsset(foodCategory));
    }

    private static PendingIntent contentIntent(Context context, int foodId) {

        //Create an intent that opens up the DetailActivity
        Intent startActivityIntent = new Intent(context, DetailActivity.class);
        startActivityIntent.putExtra(FOOD_ID, foodId);
        startActivityIntent.putExtra(EXCEPTION_EXTRA, true);//handle special intent case at DetailActivity

        //Create a PendingIntent using getActivity that:
        // - Take the context passed in as a parameter
        // - Takes an unique integer ID for the pending intent (you can create a constant for
        //   this integer above
        // - Takes the intent to open the DetailActivity you just created; this is what is triggered
        //   when the notification is triggered
        // - Has the flag FLAG_UPDATE_CURRENT, so that if the intent is created again, keep the
        // intent but update the data
        return PendingIntent.getActivity(
                context,
                foodId,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
