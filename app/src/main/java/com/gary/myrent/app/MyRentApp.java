package com.gary.myrent.app;

import android.app.Application;

import com.gary.myrent.models.Portfolio;
import com.gary.myrent.models.PortfolioSerializer;

import static com.gary.android.helpers.LogHelpers.info;

public class MyRentApp extends Application
{
    // maintains list of residences
    public Portfolio portfolio;

    // holds the file name we will use to store the portfolio
    private static final String FILENAME = "portfolio.json";

    // declared a protected MyRentApp field
    protected static MyRentApp app;


    @Override
    public void onCreate()
    {
        super.onCreate();
        PortfolioSerializer serializer = new PortfolioSerializer(this, FILENAME);
        portfolio = new Portfolio(serializer);
        app = this; //  initialized MyRentApp app here
        info(this, "MyRent app launched");
    }

    // provides a getter for MyRentApp app
    public static MyRentApp getApp(){
        return app;
    }
}
