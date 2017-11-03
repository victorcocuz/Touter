package com.example.android.touter;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.touter.R.id.priceRange;

/**
 * Created by victo on 10/28/2017.
 */

public class QueryUtils {

    private final static String LOG_TAG = QueryUtils.class.getSimpleName();

    public static List<Ticket> ExtractTicket(String urlRequest) {

        URL url = getURL(urlRequest);
        String jsonResponse = null;

        try {
            jsonResponse = getHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing InputStream ", e);
        }

        List<Ticket> tickets = ExtractDataFromJSON(jsonResponse);

        return tickets;
    }

    public static URL getURL(String urlRequest) {
        URL url = null;
        try {
            url = new URL(urlRequest);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "URL cannot be created" + e);
        }
        return url;
    }

    public static String getHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error receiving response code :" + httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not retreive JSON Response ", e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    public static String readFromInputStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    public static List<Ticket> ExtractDataFromJSON(String jsonResponse) {

        List<Ticket> tickets = new ArrayList<>();

        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        try {
            JSONObject jsonTicket = new JSONObject(jsonResponse);
            JSONObject jsonEmbedded = jsonTicket.getJSONObject("_embedded");
            JSONArray jsonEvents = jsonEmbedded.getJSONArray("events");
            for (int i = 0; i < jsonEvents.length(); i++) {
                JSONObject jsonEvent = jsonEvents.getJSONObject(i);

                String ticketTitle = null;
//                String ticketImageUrl = null;
//                Bitmap ticketImage = null;
                String ticketVenue = null;
                String ticketCity = null;
                int ticketPriceMin = 0;
                int ticketPriceMax = 0;
                String ticketDate = null;
                String ticketTime = null;

                //Event title
                if (jsonEvent.has("name")) {
                    ticketTitle = jsonEvent.getString("name");
                }

                //Event image
//                JSONArray jsonImages = jsonEvent.getJSONArray("images");
//                JSONObject jsonImage = jsonImages.getJSONObject(0);
//                if (jsonImage.has("url")) {
//                    ticketImageUrl = jsonImage.getString("url");
//                }
//
//                byte[] ticketImageByte = null;
//
//                try {
//                    InputStream is = new BufferedInputStream(getURL(ticketImageUrl).openConnection().getInputStream());
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    byte[] buffer = new byte[1024];
//                    int read = 0;
//
//                    while ((read = is.read(buffer, 0, buffer.length)) != -1) {
//                        baos.write(buffer, 0, read);
//                    }
//                    baos.flush();
//                    ticketImageByte = baos.toByteArray();
//
//                } catch (IOException e) {
//                    Log.e(LOG_TAG, "Could not convert url to bitmap ", e);
//                }

                //Event date & time
                JSONObject jsonDates = jsonEvent.getJSONObject("dates");
                JSONObject jsonStart = jsonDates.getJSONObject("start");
                if (jsonStart.has("localDate") && jsonStart.has("localTime")) {
                    ticketDate = jsonStart.getString("localDate");
                    ticketTime = jsonStart.getString("localTime");
                }


                //Event price range
                if (jsonEvent.has("priceRanges")) {
                    JSONArray priceRanges = jsonEvent.getJSONArray("priceRanges");
                    JSONObject priceRange = priceRanges.getJSONObject(0);

                    if (priceRange.has("min") && priceRange.has("max")) {
                        ticketPriceMin = priceRange.getInt("min");
                        ticketPriceMax = priceRange.getInt("max");
                    }
                } else {
                    continue;
                }

                //Event location
                JSONObject jsonEmbeddedEvents = jsonEvent.getJSONObject("_embedded");
                JSONArray jsonVenues = jsonEmbeddedEvents.getJSONArray("venues");
                JSONObject jsonVenue = jsonVenues.getJSONObject(0);
                if (jsonVenue.has("name")) {
                    ticketVenue = jsonVenue.getString("name");
                }

                JSONObject jsonCity = jsonVenue.getJSONObject("city");
                if (jsonCity.has("name")) {
                    ticketCity = jsonCity.getString("name");
                }


                tickets.add(new Ticket(ticketTitle, ticketDate, ticketTime, ticketPriceMin, ticketPriceMax, ticketVenue, ticketCity));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Extract data from JSON", e);
        }

        return tickets;
    }
}
