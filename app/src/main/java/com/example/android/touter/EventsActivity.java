package com.example.android.touter;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Ticket>> {

    private static final String LOG_TAG = EventsActivity.class.getSimpleName();
    private static final int REQUEST_URL_ID = 0;
    private static final String REQUEST_URL = "https://app.ticketmaster.com/discovery/v2/events.json?countryCode=US&size=20&apikey=sHY0wxNx6GURsbUI2EbuRxHRsjZnqwtu";

    private TicketAdapter ticketAdapter;

    @BindView(R.id.events_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.events_progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        ButterKnife.bind(this);

        //Recycler View
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ticketAdapter = new TicketAdapter();
        recyclerView.setAdapter(ticketAdapter);

        getSupportLoaderManager().initLoader(REQUEST_URL_ID, null, this).forceLoad();
    }


    @Override
    public Loader<List<Ticket>> onCreateLoader(int id, Bundle args) {
        return new TicketLoader(this, REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Ticket>> loader, List<Ticket> data) {
        ticketAdapter.addAll(data);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Ticket>> loader) {
    }
}
