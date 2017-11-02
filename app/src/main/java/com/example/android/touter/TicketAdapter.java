package com.example.android.touter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victo on 10/28/2017.
 */

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {

    private static final String LOG_TAG = TicketAdapter.class.getSimpleName();
    private List<Ticket> tickets;
    private String ticketVenue, ticketCity, displayTicketInfo;

    public TicketAdapter() {
        //Empty Constructor
    }

    @Override
    public TicketAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_events, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TicketAdapter.ViewHolder holder, final int position) {

        final byte[] ticketImageByte = tickets.get(position).getTicketImage();
        Bitmap ticketImage = BitmapFactory.decodeByteArray(ticketImageByte, 0, ticketImageByte.length);

        ticketVenue = tickets.get(position).getTicketVenue();
        ticketCity = tickets.get(position).getTicketCity();
        displayTicketInfo = ticketVenue + ", " + ticketCity;

        holder.ticketCardTitleView.setText(tickets.get(position).getTicketTitle());
        holder.ticketCardImageView.setImageBitmap(ticketImage);
        holder.ticketCardDateView.setText(tickets.get(position).getTicketDate());
        holder.ticketCardTimeView.setText(tickets.get(position).getTicketTime());
        holder.ticketCardInfoView.setText(displayTicketInfo);

        holder.ticketCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goToDetailActivity = new Intent(v.getContext(), DetailActivity.class);
                goToDetailActivity.putExtra(Integer.toString(R.string.ticket_title), tickets.get(position).getTicketTitle());
                goToDetailActivity.putExtra(Integer.toString(R.string.ticket_image), ticketImageByte);
                goToDetailActivity.putExtra(Integer.toString(R.string.ticket_date), tickets.get(position).getTicketDate());
                goToDetailActivity.putExtra(Integer.toString(R.string.ticket_time), tickets.get(position).getTicketTime());
                goToDetailActivity.putExtra(Integer.toString(R.string.ticket_price_min), tickets.get(position).getTicketPriceMin());
                goToDetailActivity.putExtra(Integer.toString(R.string.ticket_price_max), tickets.get(position).getTicketPriceMax());
                goToDetailActivity.putExtra(Integer.toString(R.string.ticket_city), tickets.get(position).getTicketCity());
                goToDetailActivity.putExtra(Integer.toString(R.string.ticket_venue), tickets.get(position).getTicketVenue());
                v.getContext().startActivity(goToDetailActivity);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (tickets != null) {
            return tickets.size();
        } else {
            return 0;
        }
    }

    public void addAll(List<Ticket> tickets) {
        this.tickets = tickets;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_view_events) CardView ticketCardView;
        @BindView(R.id.card_events_title) TextView ticketCardTitleView;
        @BindView(R.id.card_events_image_url) ImageView ticketCardImageView;
        @BindView(R.id.card_events_date) TextView ticketCardDateView;
        @BindView(R.id.card_events_time) TextView ticketCardTimeView;
        @BindView(R.id.card_events_info_1) TextView ticketCardInfoView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
