package com.example.grumpybunny.newsapp;

import android.content.Context;
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

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/** MainActivity implement LoaderManager for list of Events */

public class MainActivity extends AppCompatActivity implements androidx.loader.app.LoaderManager.LoaderCallbacks<List<Event>> {

    private static final String GUARDIAN_API_URL = "https://content.guardianapis.com/search?";
    private static final String API_KEY = BuildConfig.ApiKey;
    String restartLoader;
    String initLoader;

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
        setUp();

        // initialize Toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
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

//          emptyText.setVisibility(View.GONE);
            LoaderManager loaderManager = getSupportLoaderManager();
            if (loader == initLoader) {
                //androidx.loader.app.LoaderManager.LoaderCallbacks
                loaderManager.initLoader(1, null,  this);
            }
            if (loader == restartLoader) {
                getSupportLoaderManager().restartLoader(1, null, this);
            }
        } else if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {
            if (loader == initLoader) {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
            if (loader == restartLoader) {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public Loader<List<Event>> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri = Uri.parse(GUARDIAN_API_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("api-key", API_KEY);

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
            LoaderAndConnection(initLoader);
            eventAdapter.notifyDatasetchanged();
        }
        return super.onOptionsItemSelected(item);
    }
}
