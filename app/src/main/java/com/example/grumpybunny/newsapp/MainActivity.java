package com.example.grumpybunny.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/** MainActivity implement LoaderManager for list of Events */

public class MainActivity extends AppCompatActivity implements androidx.loader.app.LoaderManager.LoaderCallbacks<List<Event>> {

    private static final String GUARDIAN_API_URL = "https://content.guardianapis.com/search?";
    private static final String API_KEY = "022f59d8-6c7e-4a78-b922-13cf08eee7fd";
//    private static final String API_KEY = BuildConfig.ApiKey;
    String restartLoader;
    String initLoader;
    private TextView emptyText;
    String filter = "";

    // use ButterKnife to bind the recycler view
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    // initialize adapter
    EventAdapter eventAdapter;

    // initialize LayoutManager
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        // set initial value of informative text fields
        emptyText = findViewById(R.id.emptyText);
        emptyText.setText("No news available");

        // collect intent data to filter the list if selected
        if (getIntent() != null) {
            Intent intent = getIntent();
            filter = intent.getStringExtra("filter");
        }

        // initialize Toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // default action bar title when no topic is selected
        Log.d("MainActivity", "filter = " + filter);
        if (filter == null || filter == "") {
            getSupportActionBar().setTitle("Showing: All stories");
        } else {
            getSupportActionBar().setTitle("Topic: " + filter);
        }

        setUp();



    }
    private void setUp() {
        // initialize recycler view and connect to LayoutManager
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        eventAdapter = new EventAdapter(new ArrayList<>());

        // initialize the Loader
        LoaderAndConnection(initLoader);
    }

    public void LoaderAndConnection(String loader) {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            // since we have connection, hide "no internet connection" TextView
            emptyText.setVisibility(View.GONE);
            LoaderManager loaderManager = getSupportLoaderManager();
            if (loader == initLoader) {

                loaderManager.initLoader(1, null,  this);
            }
            if (loader == restartLoader) {
                getSupportLoaderManager().restartLoader(1, null, this);
            }
        } else if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {
            if (loader == initLoader) {
                // since we have no connection, clear the view and show "no internet connection" TextView
                recyclerView.getRecycledViewPool().clear();
                recyclerView.setAdapter(null);
                emptyText.setVisibility(View.VISIBLE);
                emptyText.setText("No Internet Connection");

            }
            if (loader == restartLoader) {
                // since we have no connection, clear the view and show "no internet connection" TextView
                recyclerView.getRecycledViewPool().clear();
                recyclerView.setAdapter(null);
                emptyText.setVisibility(View.VISIBLE);
                emptyText.setText("No Internet Connection");
            }
        }
    }
    @Override
    public Loader<List<Event>> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri = Uri.parse(GUARDIAN_API_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("api-key", API_KEY);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("q", filter);
        return new EventLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Event>> loader, List<Event> events) {
        if (events != null && !events.isEmpty()) {
            // call the EventAdapter class once all data items are in the array
            eventAdapter.addItems(events);

            // Attach the adapter to the recycler view
            recyclerView.setAdapter(eventAdapter);
            eventAdapter.notifyDatasetchanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {
        eventAdapter.notifyDatasetchanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // define actions of icons on toolbar
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_reload) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("filter", "");
//            getSupportActionBar().setTitle("Showing: All stories");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
