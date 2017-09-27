package com.gary.myrent.models;

import java.util.Date;
import java.util.Random;

public class Residence
{
    public Long id;
    public Long date;

    //a latitude longitude pair
    //example "52.4566,-6.5444"
    private String geolocation;
    public boolean rented;

    public Residence()
    {
        id = unsignedLong();
        date = new Date().getTime();
    }

    /**
     * Generate a long greater than zero
     * @return Unsigned Long value greater than zero
     */
    private Long unsignedLong() {
        long rndVal = 0;
        do {
            rndVal = new Random().nextLong();
        } while (rndVal <= 0);
        return rndVal;
    }

    public void setGeolocation(String geolocation)
    {
        this.geolocation = geolocation;
    }

    public String getGeolocation()
    {
        return geolocation;
    }

    public String getDateString() {
        return "Registered:" + dateString();
    }

    private String dateString() {
        String dateFormat = "EEE d MMM yyyy H:mm";
        return android.text.format.DateFormat.format(dateFormat, date).toString();
    }

}