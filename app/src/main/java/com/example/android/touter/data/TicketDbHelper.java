package com.example.android.touter.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.touter.data.TicketContract.TicketEntry;

/**
 * Created by victo on 10/29/2017.
 */

public class TicketDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = TicketDbHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "touter.db";
    public static final int DATABASE_VERSION = 1;

    public TicketDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TICKETS_TABLE = "CREATE TABLE " + TicketEntry.TABLE_NAME + " ("
                + TicketEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TicketEntry.COLUMN_TICKET_TITLE + " TEXT NOT NULL, "
                + TicketEntry.COLUMN_TICKET_IMAGE + " BLOB, "
                + TicketEntry.COLUMN_TICKET_DATE + " TEXT NOT NULL, "
                + TicketEntry.COLUMN_TICKET_TIME + " TEXT NOT NULL, "
                + TicketEntry.COLUMN_TICKET_PRICE_MIN + " INTEGER NOT NULL, "
                + TicketEntry.COLUMN_TICKET_PRICE_MAX + " INTEGER NOT NULL, "
                + TicketEntry.COLUMN_TICKET_VENUE + " TEXT NOT NULL, "
                + TicketEntry.COLUMN_TICKET_CITY + " TEXT NOT NULL, "
                + TicketEntry.COLUMN_TICKET_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + TicketEntry.COLUMN_TICKET_SECTION + " INTEGER NOT NULL DEFAULT 0, "
                + TicketEntry.COLUMN_TICKET_PRICE_BUY + " INTEGER NOT NULL DEFAULT 0, "
                + TicketEntry.COLUMN_TICKET_PRICE_RESALE + " INTEGER NOT NULL DEFAULT 0, "
                + TicketEntry.COLUMN_TICKET_PROFIT + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_TICKETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
