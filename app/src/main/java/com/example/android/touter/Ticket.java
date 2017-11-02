package com.example.android.touter;

import android.graphics.Bitmap;

/**
 * Created by victo on 10/28/2017.
 */

public class Ticket {

    private String ticketTitle;
    private byte[] ticketImageByte;
    private String ticketDate;
    private String ticketTime;
    private int ticketPriceMin;
    private int ticketPriceMax;
    private String ticketVenue;
    private String ticketCity;

    public Ticket(String ticketTitle, byte[] ticketImageByte, String ticketDate, String ticketTime, int ticketPriceMin, int ticketPriceMax, String ticketVenue, String ticketCity) {
        this.ticketTitle = ticketTitle;
        this.ticketImageByte = ticketImageByte;
        this.ticketDate = ticketDate;
        this.ticketTime = ticketTime;
        this.ticketPriceMin = ticketPriceMin;
        this.ticketPriceMax = ticketPriceMax;
        this.ticketVenue = ticketVenue;
        this.ticketCity = ticketCity;
    }

    public byte[] getTicketImage() {
        return ticketImageByte;
    }

    public String getTicketTitle() {
        return ticketTitle;
    }

    public String getTicketDate() {
        return ticketDate;
    }

    public String getTicketTime() {
        return ticketTime;
    }

    public int getTicketPriceMin() {
        return ticketPriceMin;
    }

    public int getTicketPriceMax() {
        return ticketPriceMax;
    }

    public String getTicketVenue() {
        return ticketVenue;
    }

    public String getTicketCity() {
        return ticketCity;
    }


}
