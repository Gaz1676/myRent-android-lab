package com.gary.myrent;

import java.util.Random;

public class Residence
{
    private Long id;

    //a latitude longitude pair
    //example "52.4566,-6.5444"
    private String geolocation;

    public Residence()
    {
        id = unsignedLong();
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
}
