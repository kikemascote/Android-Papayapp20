package co.hipstercoding.dev.papayapp;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import co.hipstercoding.dev.papayapp.R;

import static co.hipstercoding.dev.papayapp.utils.StaticVarsUtils.TOUR_ACTIVITY_VISITED_KEY;

public class TourActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

// --Commented out by Inspection START (08/07/2019 03:55 AM):
//    //for logging purpose
//    String TAG = this.getClass().getSimpleName();
// --Commented out by Inspection STOP (08/07/2019 03:55 AM)

    private final Context context = this;

    //variables for gradual color transition
    private Integer[] colors = null;
    private Integer[] colorsBar = null;
    private final ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    //variables for static content

    //images that hold state for each dot
    private ImageView mDot1;
    private ImageView mDot2;
    private ImageView mDot3;

    private TextView mDoneTextView;
    private ImageView mArrowImageView;

    private View mColoredBar;

    //for changing status bar color dynamically
    private Window window;

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean tourActivityVisited = sharedPreferences.getBoolean(TOUR_ACTIVITY_VISITED_KEY, false);//false for default

        //checks if the user have been visited tour activity once
        if (tourActivityVisited)
        {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity (intent);
            this.finishActivity (0);
        }

        setContentView(R.layout.activity_tour);

        window = this.getWindow();

        //find views
        TextView mSkipTextView = findViewById(R.id.skip_text);
        mDoneTextView = findViewById(R.id.done_text);
        mArrowImageView = findViewById(R.id.right_arrow);
        mDot1 = findViewById(R.id.dot1);
        mDot2 = findViewById(R.id.dot2);
        mDot3 = findViewById(R.id.dot3);
        mColoredBar = findViewById(R.id.colored_bar);

        //set up listeners
        mSkipTextView.setOnClickListener(gotToMainListener);
        mDoneTextView.setOnClickListener(gotToMainListener);

        mArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
        });

        //set colors and visibility
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPage1Dark));
        mColoredBar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPage1Dark));
        mDoneTextView.setVisibility(View.INVISIBLE);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPage1));
        mViewPager.addOnPageChangeListener(this);

        setUpColors();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tour, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        //gradual color change effect
        if(position < (mSectionsPagerAdapter.getCount() -1) && position < (colors.length - 1)) {
            mViewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]));
            mColoredBar.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colorsBar[position], colorsBar[position + 1]));
            window.setStatusBarColor((Integer) argbEvaluator.evaluate(positionOffset, colorsBar[position], colorsBar[position + 1]));
        } else {
            // the last page color
            mViewPager.setBackgroundColor(colors[colors.length - 1]);
            mColoredBar.setBackgroundColor(colorsBar[colorsBar.length - 1]);
            window.setStatusBarColor(colorsBar[colorsBar.length - 1]);
        }

    }

    @Override
    public void onPageSelected(int position) {

        switch (position) {
            case 0:
                mDoneTextView.setVisibility(View.INVISIBLE);
                mArrowImageView.setVisibility(View.VISIBLE);
                mDot1.setImageResource(R.drawable.selected_dot);
                mDot2.setImageResource(R.drawable.not_selected_dot1);
                mDot3.setImageResource(R.drawable.not_selected_dot1);
                break;
            case 1:
                mDoneTextView.setVisibility(View.INVISIBLE);
                mArrowImageView.setVisibility(View.VISIBLE);
                mDot1.setImageResource(R.drawable.not_selected_dot2);
                mDot2.setImageResource(R.drawable.selected_dot);
                mDot3.setImageResource(R.drawable.not_selected_dot2);
                break;
            case 2:
                mDoneTextView.setVisibility(View.VISIBLE);
                mArrowImageView.setVisibility(View.INVISIBLE);
                mDot1.setImageResource(R.drawable.not_selected_dot3);
                mDot2.setImageResource(R.drawable.not_selected_dot3);
                mDot3.setImageResource(R.drawable.selected_dot);
                break;
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        setVisitToTrue();
    }

    @Override
    protected void onStop() {
        super.onStop();
        setVisitToTrue();
    }

    /**
     * A placeholder fragment containing first page
     */
    static class FirstPageFragment extends Fragment {

        public FirstPageFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_first_page, container, false);
        }
    }

    /**
     * A placeholder fragment containing second page
     */
    static class SecondPageFragment extends Fragment {

        public SecondPageFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_second_page, container, false);
        }
    }

    /**
     * A placeholder fragment containing third page
     */
    static class ThirdPageFragment extends Fragment {

        public ThirdPageFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_third_page, container, false);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            // getItem is called to instantiate the fragment for the given page.
            // Return a corresponding page fragment
            switch (position) {
                case 0:
                    return new FirstPageFragment();
                case 1:
                    return new SecondPageFragment();
                case 2:
                    return new ThirdPageFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    private void setUpColors(){

        Integer color1 = ContextCompat.getColor(this, R.color.colorPage1);
        Integer color2 = ContextCompat.getColor(this, R.color.colorPage2);
        Integer color3 = ContextCompat.getColor(this, R.color.colorPage3);

        Integer colorBar1 = ContextCompat.getColor(this, R.color.colorPage1Dark);
        Integer colorBar2 = ContextCompat.getColor(this, R.color.colorPage2Dark);
        Integer colorBar3 = ContextCompat.getColor(this, R.color.colorPage3Dark);

        Integer[] colors_temp = {color1, color2, color3};
        Integer[] colors_temp_bar = {colorBar1, colorBar2, colorBar3};

        colors = colors_temp;
        colorsBar = colors_temp_bar;

    }

    private void setVisitToTrue() {
        //set true variable for skipping this activity
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(TOUR_ACTIVITY_VISITED_KEY, true);
        editor.commit();
    }

    //onClickListener declaration that will execute an intent that navigate user to mainActivity
    private final View.OnClickListener gotToMainListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setVisitToTrue();

            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);

            finish();//remove from backstack
        }
    };

}
