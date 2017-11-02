package com.example.android.touter;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

/**
 * Created by victo on 10/28/2017.
 */

public class TicketLoader extends AsyncTaskLoader<List<Ticket>> {

    String url;

    public TicketLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<Ticket> loadInBackground() {
        return QueryUtils.ExtractTicket(url);
    }
}
