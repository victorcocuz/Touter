package com.example.android.touter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.touter.data.TicketContract.TicketEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.id;

/**
 * Created by victo on 10/29/2017.
 */

public class TicketCursorAdapter extends CursorAdapter {

    private static final String LOG_TAG = TicketCursorAdapter.class.getSimpleName();
    private static final int BUY_ONE = 1;
    private static final int SELL_ONE = -1;
    private String ticketDate, ticketTime, ticketCity, ticketVenue, displayTicketInfo1, displayTicketInfo2, ticketBuy, ticketSell, displayTicketBuy, displayTicketSell, ticketInventory, displayTicketInventory;

    @BindView(R.id.card_catalog_title)
    TextView ticketTitleView;
    @BindView(R.id.card_catalog_image)
    ImageView ticketImageView;
    @BindView(R.id.card_catalog_info_1)
    TextView ticketInfoView1;
    @BindView(R.id.card_catalog_info_2)
    TextView ticketInfoView2;
    @BindView(R.id.card_catalog_buy)
    TextView ticketBuyView;
    @BindView(R.id.card_catalog_sell)
    TextView ticketSellView;
    @BindView(R.id.card_catalog_inventory)
    TextView ticketInventoryView;

    public TicketCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.card_view_catalog, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ButterKnife.bind(this, view);

        ticketDate = cursor.getString(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_DATE));
        ticketTime = cursor.getString(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_TIME));
        ticketVenue = cursor.getString(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_VENUE));
        ticketCity = cursor.getString(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_CITY));
        ticketBuy = cursor.getString(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_PRICE_BUY));
        ticketSell = cursor.getString(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_PRICE_RESALE));
        ticketInventory = cursor.getString(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_QUANTITY));

        displayTicketInfo1 = ticketDate + ", " + ticketTime;
        displayTicketInfo2 = ticketVenue + ", " + ticketCity;
        displayTicketBuy = view.getResources().getString(R.string.catalog_buy_for) + " " + view.getResources().getString(R.string.pound) + ticketBuy;
        displayTicketSell = view.getResources().getString(R.string.catalog_sell_for) + " " + view.getResources().getString(R.string.pound) + ticketSell;
        displayTicketInventory = ticketInventory + " " + view.getResources().getString(R.string.catalog_inventory);

        ticketInfoView1.setText(displayTicketInfo1);
        ticketInfoView2.setText(displayTicketInfo2);
        ticketBuyView.setText(displayTicketBuy);
        ticketSellView.setText(displayTicketSell);
        ticketInventoryView.setText(displayTicketInventory);

        byte[] byteArray = cursor.getBlob(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_IMAGE));
        Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ticketImageView.setImageBitmap(image);

        ticketTitleView.setText(cursor.getString(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_TITLE)));

        //Setup URI
        final Uri currentTicketUri = ContentUris.withAppendedId(TicketEntry.CONTENT_URI, cursor.getInt(cursor.getColumnIndex(TicketEntry._ID)));

        //Setup Buy One and Sell One Functionality
        final int quantity = cursor.getInt(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_QUANTITY));

        ticketBuyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessInventory(BUY_ONE, quantity, context, currentTicketUri, v);
            }
        });

        ticketSellView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessInventory(SELL_ONE, quantity, context, currentTicketUri, v);
            }
        });
    }

    public void accessInventory(int i, int quantity, Context context, Uri currentTicketUri, View v) {
        int ticketInventory = quantity + i;
        if(ticketInventory > 4) {
            Toast.makeText(v.getContext(), v.getContext().getString(R.string.catalog_large_inventory), Toast.LENGTH_SHORT).show();
        } else if (ticketInventory < 0) {
            Toast.makeText(v.getContext(), v.getContext().getString(R.string.catalog_negative_inventory), Toast.LENGTH_SHORT).show();
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TicketEntry.COLUMN_TICKET_QUANTITY, ticketInventory);
            context.getContentResolver().update(currentTicketUri, contentValues, null, null);
        }
    }
}
