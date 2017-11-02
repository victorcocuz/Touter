package com.example.android.touter.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.touter.data.TicketContract.TicketEntry;

import static android.R.attr.data;
import static android.R.attr.id;

/**
 * Created by victo on 10/29/2017.
 */

public class TicketProvider extends ContentProvider {

    public static final String LOG_TAG = TicketProvider.class.getSimpleName();


    //Uri Matcher
    private static final int TICKETS = 100;
    private static final int TICKETS_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(TicketContract.CONTENT_AUTHORITY, TicketContract.PATH_TICKETS, TICKETS);
        sUriMatcher.addURI(TicketContract.CONTENT_AUTHORITY, TicketContract.PATH_TICKETS + "/#", TICKETS_ID);
    }

    private TicketDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new TicketDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case TICKETS:
                cursor = database.query(TicketEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case TICKETS_ID:
                selection = TicketEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TicketEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        int match = sUriMatcher.match(uri);
        switch (match) {
            case TICKETS:
                return insertPet(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues values) {
        //Check that fields are not null
        Integer quantity = values.getAsInteger(TicketEntry.COLUMN_TICKET_QUANTITY);
        if (quantity == null || !TicketEntry.isValidQuantity(quantity)) {
            throw new IllegalArgumentException("Ticket requires valid quantity");
        }

        Integer section = values.getAsInteger(TicketEntry.COLUMN_TICKET_SECTION);
        if (section == null || !TicketEntry.isValidSection(section)) {
            throw new IllegalArgumentException("Ticket requires valid section");
        }

        Integer price = values.getAsInteger(TicketEntry.COLUMN_TICKET_PRICE_RESALE);
        if (price == null) {
            throw new IllegalArgumentException("Ticket requires valid price");
        }

        //Insert values in database
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id = database.insert(TicketEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int match = sUriMatcher.match(uri);
        switch (match) {
            case TICKETS:
                return updateTicket(uri, values, selection, selectionArgs);
            case TICKETS_ID:
                selection = TicketEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateTicket(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateTicket(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //Check that fields are not null
        if (values.containsKey(TicketEntry.COLUMN_TICKET_QUANTITY)) {
            Integer quantity = values.getAsInteger(TicketEntry.COLUMN_TICKET_QUANTITY);
            if (quantity == null || !TicketEntry.isValidQuantity(quantity)) {
                throw new IllegalArgumentException("Ticket requires valid quantity");
            }
        }

        if (values.containsKey(TicketEntry.COLUMN_TICKET_SECTION)) {
            Integer section = values.getAsInteger(TicketEntry.COLUMN_TICKET_SECTION);
            if (section == null || !TicketEntry.isValidSection(section)) {
                throw new IllegalArgumentException("Ticket requires valid section");
            }
        }

        if (values.containsKey(TicketEntry.COLUMN_TICKET_PRICE_RESALE)) {
            Integer price = values.getAsInteger(TicketEntry.COLUMN_TICKET_PRICE_RESALE);
            if (price == null) {
                throw new IllegalArgumentException("Ticket requires valid price");
            }
        }

        if (values.size() == 0) {
            return 0;
        }


        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsUpdated = database.update(TicketEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int rowsDeleted;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case TICKETS:
                rowsDeleted = database.delete(TicketEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TICKETS_ID:
                selection = TicketEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TicketEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TICKETS:
                return TicketEntry.CONTENT_LIST_TYPE;
            case TICKETS_ID:
                return TicketEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("/Unknown URI " + uri + "with match " + match);
        }
    }
}

