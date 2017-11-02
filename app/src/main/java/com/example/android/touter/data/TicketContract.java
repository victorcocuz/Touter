package com.example.android.touter.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by victo on 10/29/2017.
 */

public class TicketContract {

    public TicketContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.touter";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TICKETS = "tickets";

    public static final class TicketEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TICKETS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TICKETS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TICKETS;

        public static final String TABLE_NAME = "tickets";
        public final static String _ID = BaseColumns._ID;
        public static final String COLUMN_TICKET_TITLE = "title";
        public static final String COLUMN_TICKET_IMAGE = "image_url";
        public static final String COLUMN_TICKET_DATE = "date";
        public static final String COLUMN_TICKET_TIME = "time";
        public static final String COLUMN_TICKET_PRICE_MIN = "priceMin";
        public static final String COLUMN_TICKET_PRICE_MAX = "priceMax";
        public static final String COLUMN_TICKET_VENUE = "venue";
        public static final String COLUMN_TICKET_CITY = "city";
        public static final String COLUMN_TICKET_QUANTITY = "tickets";
        public static final String COLUMN_TICKET_SECTION = "section";
        public static final String COLUMN_TICKET_PRICE_BUY = "priceBuy";
        public static final String COLUMN_TICKET_PRICE_RESALE = "priceResale";
        public static final String COLUMN_TICKET_PROFIT = "total";

        public static final int TICKET_UNKNOWN = 0;
        public static final int TICKET_ONE = 1;
        public static final int TICKET_TWO = 2;
        public static final int TICKET_THREE = 3;
        public static final int TICKET_FOUR = 4;

        public static final int SECTION_UNKNOWN = 0;
        public static final int SECTION_GA = 1;
        public static final int SECTION_LOWER_TIER = 2;
        public static final int SECTION_MIDDLE_TIER = 3;
        public static final int SECTION_UPPER_TIER = 4;

        public static boolean isValidQuantity(int quantity) {
            if (quantity == TICKET_UNKNOWN
                    || quantity == TICKET_ONE
                    || quantity == TICKET_TWO
                    || quantity == TICKET_THREE
                    || quantity == TICKET_FOUR) {
                return true;
            }
            return false;
        }

        public static boolean isValidSection(int section) {
            if (section == SECTION_UNKNOWN
                    || section == SECTION_GA
                    || section == SECTION_LOWER_TIER
                    || section == SECTION_MIDDLE_TIER
                    || section == SECTION_UPPER_TIER) {
                return true;
            }
            return false;
        }
    }
}
