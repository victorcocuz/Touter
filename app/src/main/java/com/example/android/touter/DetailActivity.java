package com.example.android.touter;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.touter.data.TicketContract.TicketEntry;

import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Intent.ACTION_VIEW;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final int EXISTING_TICKET_LOADER = 0;
    private static final int PICK_IMAGE_REQUEST = 0;
    private static final int BUY_ONE = 1;
    private static final int SELL_ONE = -1;

    String ticketTitle, ticketDate, ticketTime, ticketCity, ticketVenue;
    byte[] ticketImageArray;
    int ticketPriceMin, ticketPriceMax;
    int ticketQuantity, ticketSection, ticketPriceBuy, ticketPriceResale, ticketTotal, ticketProfit;
    String displayTicketSeatValue, displayTicketProfitValue, displayTicketTotalValue, detailInfo;
    Uri currentTicketUri;

    //Bind Overall
    @BindView(R.id.detail_title)
    TextView detailTitleView;
    @BindView(R.id.detail_image)
    ImageView detailImageView;
    @BindView(R.id.detail_info)
    TextView detailInfoView;
    @BindView(R.id.detail_order_total)
    TextView detailTotalView;

    //Bind Ticket Selection
    @BindView(R.id.detail_catalog_buy)
    TextView detailCatalogAdd;
    @BindView(R.id.detail_catalog_sell)
    TextView detailCatalogRemove;
    @BindView(R.id.detail_spinner_section)
    Spinner detailSpinnerSectionView;


    //Bind Order Summary
    @BindView(R.id.detail_summary_ticket_1)
    LinearLayout detailSummaryTicketView1;
    @BindView(R.id.detail_summary_ticket_2)
    LinearLayout detailSummaryTicketView2;
    @BindView(R.id.detail_summary_ticket_3)
    LinearLayout detailSummaryTicketView3;
    @BindView(R.id.detail_summary_ticket_4)
    LinearLayout detailSummaryTicketView4;
    @BindView(R.id.detail_summary_section_1View)
    TextView detailSummarySectionView1;
    @BindView(R.id.detail_summary_section_2)
    TextView detailSummarySectionView2;
    @BindView(R.id.detail_summary_section_3)
    TextView detailSummarySectionView3;
    @BindView(R.id.detail_summary_section_4)
    TextView detailSummarySectionView4;
    @BindView(R.id.detail_summary_value_1)
    TextView detailSummaryValueView1;
    @BindView(R.id.detail_summary_value_2)
    TextView detailSummaryValueView2;
    @BindView(R.id.detail_summary_value_3)
    TextView detailSummaryValueView3;
    @BindView(R.id.detail_summary_value_4)
    TextView detailSummaryValueView4;

    //Bind Reselling Information
    @BindView(R.id.detail_price_reselling)
    EditText detailPriceResaleView;
    @BindView(R.id.detail_total_profit)
    TextView detailProfitView;

    //Bind Contact Supplier
    @BindView(R.id.detail_contact_website)
    TextView detailContactWebsite;
    @BindView(R.id.detail_contact_email)
    TextView detailContactEmail;

    //Setup on touch listener
    private boolean ticketHasChanged = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ticketHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);


        //Initial Visibility
        detailSummaryTicketView1.setVisibility(View.GONE);
        detailSummaryTicketView2.setVisibility(View.GONE);
        detailSummaryTicketView3.setVisibility(View.GONE);
        detailSummaryTicketView4.setVisibility(View.GONE);


        currentTicketUri = getIntent().getData();

        if (currentTicketUri == null) {
            setTitle(R.string.activity_detail_insert);

            //Get extras from intent and set text
            Bundle extras = getIntent().getExtras();
            ticketTitle = extras.getString(Integer.toString(R.string.ticket_title));
            ticketDate = extras.getString(Integer.toString(R.string.ticket_date));
            ticketTime = extras.getString(Integer.toString(R.string.ticket_time));
            ticketPriceMin = extras.getInt(Integer.toString(R.string.ticket_price_min));
            ticketPriceMax = extras.getInt(Integer.toString(R.string.ticket_price_max));
            ticketCity = extras.getString(Integer.toString(R.string.ticket_city));
            ticketVenue = extras.getString(Integer.toString(R.string.ticket_venue));

            //Get image extras from intent and set image
            ticketImageArray = getIntent().getByteArrayExtra(Integer.toString(R.string.ticket_image));
            Bitmap ticketImage = BitmapFactory.decodeByteArray(ticketImageArray, 0, ticketImageArray.length);
            detailImageView.setImageBitmap(ticketImage);

            detailTitleView.setText(ticketTitle);
            detailInfo = ticketDate + ", " + ticketTime + ", " + ticketVenue + ", " + ticketCity;
            detailInfoView.setText(detailInfo);

            invalidateOptionsMenu();
        } else {
            setTitle(R.string.activity_detail_edit);
            getSupportLoaderManager().initLoader(EXISTING_TICKET_LOADER, null, this);
        }

        //Change Image
        detailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
            }
        });

        //Get spinner information
        setupSpinner();

        //Total & Profit
        ticketTotal = ticketPriceBuy * ticketQuantity;
        displayTicketTotalValue = getString(R.string.detail_order_total) + " " + getString(R.string.pound) + String.valueOf(ticketTotal);
        detailTotalView.setText(displayTicketTotalValue);

        detailPriceResaleView.setCursorVisible(false);
        detailPriceResaleView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ticketPriceResale = Integer.parseInt(detailPriceResaleView.getText().toString());
                    ticketProfit = (ticketPriceResale - ticketPriceBuy) * ticketQuantity;
                    displayTicketProfitValue = getString(R.string.detail_total_profit) + " " + getString(R.string.pound) + String.valueOf(ticketProfit);
                    detailProfitView.setText(displayTicketProfitValue);
                }
                return false;
            }
        });

        //Setup onTouchListeners
        detailCatalogAdd.setOnTouchListener(touchListener);
        detailCatalogRemove.setOnTouchListener(touchListener);
        detailSpinnerSectionView.setOnTouchListener(touchListener);
        detailPriceResaleView.setOnTouchListener(touchListener);

        //Setup Quantity Buttons
        detailCatalogAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessInventory(BUY_ONE, ticketQuantity, currentTicketUri);
            }
        });

        detailCatalogRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessInventory(SELL_ONE, ticketQuantity, currentTicketUri);
            }
        });

        detailCatalogAdd.setText(getString(R.string.detail_add_ticket));
        detailCatalogRemove.setText(getString(R.string.detail_remove_ticket));

        //Contact supplier
        detailContactWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.detail_contact_website_url)));
                startActivity(intent);
            }
        });

        detailContactEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.detail_contact_email_url));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Tickets Order");
                intent.putExtra(Intent.EXTRA_TEXT, "I would like to purchase more tickets");
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && requestCode == RESULT_OK) {
            Bitmap imageBitmap;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                detailImageView.setImageBitmap(imageBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void accessInventory(int i, int quantity, Uri currentTicketUri) {
        int ticketInventory = quantity + i;
        if (ticketInventory > 4) {
            Toast.makeText(this, getString(R.string.catalog_large_inventory), Toast.LENGTH_SHORT).show();
        } else if (ticketInventory < 0) {
            Toast.makeText(this, getString(R.string.catalog_negative_inventory), Toast.LENGTH_SHORT).show();
        } else {

            if (ticketInventory == TicketEntry.TICKET_ONE) {
                ticketQuantity = TicketEntry.TICKET_ONE;
                detailSummaryTicketView1.setVisibility(View.VISIBLE);
                detailSummaryTicketView2.setVisibility(View.GONE);
                detailSummaryTicketView3.setVisibility(View.GONE);
                detailSummaryTicketView4.setVisibility(View.GONE);

            } else if (ticketInventory == TicketEntry.TICKET_TWO) {
                ticketQuantity = TicketEntry.TICKET_TWO;
                detailSummaryTicketView1.setVisibility(View.VISIBLE);
                detailSummaryTicketView2.setVisibility(View.VISIBLE);
                detailSummaryTicketView3.setVisibility(View.GONE);
                detailSummaryTicketView4.setVisibility(View.GONE);

            } else if (ticketInventory == TicketEntry.TICKET_THREE) {
                ticketQuantity = TicketEntry.TICKET_THREE;
                detailSummaryTicketView1.setVisibility(View.VISIBLE);
                detailSummaryTicketView2.setVisibility(View.VISIBLE);
                detailSummaryTicketView3.setVisibility(View.VISIBLE);
                detailSummaryTicketView4.setVisibility(View.GONE);

            } else if (ticketInventory == TicketEntry.TICKET_FOUR) {
                ticketQuantity = TicketEntry.TICKET_FOUR;
                detailSummaryTicketView1.setVisibility(View.VISIBLE);
                detailSummaryTicketView2.setVisibility(View.VISIBLE);
                detailSummaryTicketView3.setVisibility(View.VISIBLE);
                detailSummaryTicketView4.setVisibility(View.VISIBLE);
            }
        }
    }

    public void openImageSelector() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select picture"), PICK_IMAGE_REQUEST);
    }

    private void setupSpinner() {

        //Section spinner
        ArrayAdapter spinnerSectionAdapter = ArrayAdapter.createFromResource(this, R.array.detail_spinner_section, android.R.layout.simple_spinner_item);
        spinnerSectionAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        detailSpinnerSectionView.setAdapter(spinnerSectionAdapter);

        detailSpinnerSectionView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);

                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.detail_spinner_section_ga))) {
                        ticketSection = TicketEntry.SECTION_GA;
                    } else if (selection.equals(getString(R.string.detail_spinner_section_lower_tier))) {
                        ticketSection = TicketEntry.SECTION_LOWER_TIER;
                    } else if (selection.equals(getString(R.string.detail_spinner_section_middle_tier))) {
                        ticketSection = TicketEntry.SECTION_MIDDLE_TIER;
                    } else if (selection.equals(getString(R.string.detail_spinner_section_upper_tier))) {
                        ticketSection = TicketEntry.SECTION_UPPER_TIER;
                    }
                }

                //Display ticket section
                detailSummarySectionView1.setText(selection);
                detailSummarySectionView2.setText(selection);
                detailSummarySectionView3.setText(selection);
                detailSummarySectionView4.setText(selection);

                //Display ticket value
                ticketPriceBuy = getSeatValue();
                if (ticketPriceBuy != 0) {
                    displayTicketSeatValue = getString(R.string.pound) + String.valueOf(ticketPriceBuy);
                } else {
                    displayTicketSeatValue = getString(R.string.unknown);
                }

                detailSummaryValueView1.setText(displayTicketSeatValue);
                detailSummaryValueView2.setText(displayTicketSeatValue);
                detailSummaryValueView3.setText(displayTicketSeatValue);
                detailSummaryValueView4.setText(displayTicketSeatValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ticketSection = TicketEntry.SECTION_UNKNOWN;
            }
        });
    }

    private int getSeatValue() {
        int seatValue = 0;
        if (ticketQuantity != TicketEntry.TICKET_UNKNOWN && ticketSection != TicketEntry.SECTION_UNKNOWN) {
            seatValue = ticketPriceMin + (ticketPriceMax - ticketPriceMin) * (ticketSection - 1) / 3;
        }
        return seatValue;
    }

    public void saveTicket() {
        ContentValues values = new ContentValues();
        ticketPriceResale = Integer.valueOf(detailPriceResaleView.getText().toString());

        if (currentTicketUri == null
                && ticketQuantity == TicketEntry.TICKET_UNKNOWN
                && ticketSection == TicketEntry.SECTION_UNKNOWN
                && ticketPriceResale == 0) {
            return;
        }

        if (currentTicketUri == null) {
            values.put(TicketEntry.COLUMN_TICKET_TITLE, ticketTitle);
            values.put(TicketEntry.COLUMN_TICKET_IMAGE, ticketImageArray);
            values.put(TicketEntry.COLUMN_TICKET_DATE, ticketDate);
            values.put(TicketEntry.COLUMN_TICKET_TIME, ticketTime);
            values.put(TicketEntry.COLUMN_TICKET_PRICE_MIN, ticketPriceMin);
            values.put(TicketEntry.COLUMN_TICKET_PRICE_MAX, ticketPriceMax);
            values.put(TicketEntry.COLUMN_TICKET_CITY, ticketCity);
            values.put(TicketEntry.COLUMN_TICKET_VENUE, ticketVenue);
        }

        values.put(TicketEntry.COLUMN_TICKET_QUANTITY, ticketQuantity);
        values.put(TicketEntry.COLUMN_TICKET_SECTION, ticketSection);
        values.put(TicketEntry.COLUMN_TICKET_PRICE_BUY, ticketPriceBuy);
        values.put(TicketEntry.COLUMN_TICKET_PRICE_RESALE, ticketPriceResale);
        values.put(TicketEntry.COLUMN_TICKET_PROFIT, ticketProfit);

        if (currentTicketUri == null) {
            Uri newUri = getContentResolver().insert(TicketEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_ticket_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_ticket_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentTicketUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_ticket_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_ticket_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void deleteTicket() {
        if (currentTicketUri != null) {
            int rowsDeleted = getContentResolver().delete(currentTicketUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_ticket_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_ticket_successful), Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_activity, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentTicketUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_ticket);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_ticket:
                if (
                        ticketSection == TicketEntry.SECTION_UNKNOWN
                        || ticketPriceResale == 0) {
                    Toast.makeText(this, getString(R.string.dialog_incomplete_order), Toast.LENGTH_SHORT).show();
                } else {
                    saveTicket();
                    finish();
                }
                return true;
            case R.id.action_delete_ticket:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!ticketHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    return true;
                }

                //Unsaved changes
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!ticketHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListener);
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

        return new CursorLoader(this, currentTicketUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            ticketDate = cursor.getString(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_DATE));
            ticketTime = cursor.getString(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_TIME));
            ticketCity = cursor.getString(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_CITY));
            ticketVenue = cursor.getString(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_VENUE));
            ticketPriceMin = cursor.getInt(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_PRICE_MIN));
            ticketPriceMax = cursor.getInt(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_PRICE_MAX));
            ticketQuantity = cursor.getInt(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_QUANTITY));
            ticketSection = cursor.getInt(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_SECTION));
            ticketPriceBuy = getSeatValue();

            byte[] byteArray = cursor.getBlob(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_IMAGE));
            Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            detailImageView.setImageBitmap(image);

            detailTitleView.setText(cursor.getString(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_TITLE)));
            detailPriceResaleView.setText(cursor.getString(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_PRICE_RESALE)));

            detailInfo = ticketDate + ", " + ticketTime + ", " + ticketVenue + ", " + ticketCity;
            detailInfoView.setText(detailInfo);

            ticketTotal = ticketPriceBuy * ticketQuantity;
            displayTicketTotalValue = getString(R.string.detail_order_total) + " " + getString(R.string.pound) + String.valueOf(ticketTotal);
            detailTotalView.setText(displayTicketTotalValue);

            displayTicketProfitValue = getString(R.string.detail_total_profit) + " " + getString(R.string.pound) + cursor.getString(cursor.getColumnIndex(TicketEntry.COLUMN_TICKET_PROFIT));
            detailProfitView.setText(displayTicketProfitValue);

            switch (ticketSection) {
                case TicketEntry.SECTION_GA:
                    detailSpinnerSectionView.setSelection(1);
                    break;
                case TicketEntry.SECTION_LOWER_TIER:
                    detailSpinnerSectionView.setSelection(2);
                    break;
                case TicketEntry.SECTION_MIDDLE_TIER:
                    detailSpinnerSectionView.setSelection(3);
                    break;
                case TicketEntry.SECTION_UPPER_TIER:
                    detailSpinnerSectionView.setSelection(4);
                    break;
                case TicketEntry.SECTION_UNKNOWN:
                    detailSpinnerSectionView.setSelection(0);
                    break;
            }

            if (ticketPriceBuy != 0) {
                displayTicketSeatValue = getString(R.string.pound) + String.valueOf(ticketPriceBuy);
            } else {
                displayTicketSeatValue = getString(R.string.unknown);
            }

            detailSummaryValueView1.setText(displayTicketSeatValue);
            detailSummaryValueView2.setText(displayTicketSeatValue);
            detailSummaryValueView3.setText(displayTicketSeatValue);
            detailSummaryValueView4.setText(displayTicketSeatValue);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        detailTitleView.setText("");
        detailInfoView.setText("");
        detailSpinnerSectionView.setSelection(0);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_discard);
        builder.setPositiveButton(R.string.dialog_discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.dialog_keep_editing, new DialogInterface.OnClickListener() {
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

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTicket();
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
