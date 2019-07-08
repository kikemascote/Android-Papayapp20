package co.hipstercoding.dev.papayapp;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import co.hipstercoding.dev.papayapp.R;

import co.hipstercoding.dev.papayapp.utils.DBUtils;

import static android.app.SearchManager.QUERY;

public class SearchResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.search_activity_text);

        recyclerView = findViewById(R.id.rv_search_result);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        //to designate that the contents of the RecyclerView won't change an item's size
        recyclerView.setHasFixedSize(true);

        Intent intent = getIntent();

        //populate ui
        if(intent.hasExtra(QUERY)) {
            String queryString = intent.getStringExtra(QUERY);
            populateUi(queryString);
        }

    }

    private void populateUi(String queryString) {
        SearchFoodAdapter searchFoodAdapter = new SearchFoodAdapter(new DBUtils(this).queryFoods(queryString), this);
        recyclerView.setAdapter(searchFoodAdapter);
    }

}
