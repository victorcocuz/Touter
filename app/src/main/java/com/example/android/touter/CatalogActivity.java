package com.example.android.touter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.example.android.touter.data.TicketContract.TicketEntry;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.touter.R.drawable.ticket;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.catalog_list_view)
    ListView listView;
    @BindView(R.id.empty_view)
    LinearLayout emptyView;

    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();
    public TicketCursorAdapter cursorAdapter;
    public static final int TICKET_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_catalog);
        ButterKnife.bind(this);

        //Implement floating action button
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.catalog_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this, EventsActivity.class);
                startActivity(intent);
            }
        });

        //Setup listView & emptyView

        listView.setEmptyView(emptyView);

        cursorAdapter = new TicketCursorAdapter(this, null);
        listView.setAdapter(cursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent goToDetailActivity = new Intent(CatalogActivity.this, DetailActivity.class);

                //Setup URI
                Uri currentTicketUri = ContentUris.withAppendedId(TicketEntry.CONTENT_URI, id);
                goToDetailActivity.setData(currentTicketUri);
                startActivity(goToDetailActivity);
            }
        });


        getSupportLoaderManager().initLoader(TICKET_LOADER, null, this).forceLoad();
    }

    public void insertTicket() {
        ContentValues values = new ContentValues();

        Bitmap image = BitmapFactory.decodeResource(getApplicationContext().getResources(), ticket);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();

        values.put(TicketEntry.COLUMN_TICKET_TITLE, "Event 1");
        values.put(TicketEntry.COLUMN_TICKET_IMAGE, byteArray);
        values.put(TicketEntry.COLUMN_TICKET_DATE, "12 09 2017");
        values.put(TicketEntry.COLUMN_TICKET_TIME, "18 00");
        values.put(TicketEntry.COLUMN_TICKET_PRICE_MIN, 100);
        values.put(TicketEntry.COLUMN_TICKET_PRICE_MAX, 200);
        values.put(TicketEntry.COLUMN_TICKET_VENUE, "Royal Albert hall");
        values.put(TicketEntry.COLUMN_TICKET_CITY, "London");
        values.put(TicketEntry.COLUMN_TICKET_QUANTITY, 3);
        values.put(TicketEntry.COLUMN_TICKET_SECTION, 2);
        values.put(TicketEntry.COLUMN_TICKET_PRICE_BUY, 100);
        values.put(TicketEntry.COLUMN_TICKET_PRICE_RESALE, 120);
        values.put(TicketEntry.COLUMN_TICKET_PROFIT, 60);

        getContentResolver().insert(TicketEntry.CONTENT_URI, values);
    }

    public void deleteAllTickets() {
        getContentResolver().delete(TicketEntry.CONTENT_URI, null, null);
        Toast.makeText(this, getString(R.string.editor_delete_all_tickets), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_pet:
                insertTicket();
                return true;
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                TicketEntry._ID,
                TicketEntry.COLUMN_TICKET_TITLE,
                TicketEntry.COLUMN_TICKET_IMAGE,
                TicketEntry.COLUMN_TICKET_DATE,
                TicketEntry.COLUMN_TICKET_TIME,
                TicketEntry.COLUMN_TICKET_PRICE_MIN,
                TicketEntry.COLUMN_TICKET_PRICE_MAX,
                TicketEntry.COLUMN_TICKET_CITY,
                TicketEntry.COLUMN_TICKET_VENUE,
                TicketEntry.COLUMN_TICKET_QUANTITY,
                TicketEntry.COLUMN_TICKET_SECTION,
                TicketEntry.COLUMN_TICKET_PRICE_BUY,
                TicketEntry.COLUMN_TICKET_PRICE_RESALE,
                TicketEntry.COLUMN_TICKET_PROFIT};

        return new CursorLoader(this, TicketEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllTickets();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
