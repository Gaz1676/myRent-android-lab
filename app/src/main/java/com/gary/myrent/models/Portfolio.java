package com.gary.myrent.models;

import android.util.Log;

import java.util.ArrayList;

import static com.gary.android.helpers.LogHelpers.info;

// this class will be equipped with the capability to use the serializer to save | restore the Residences it is managing.
// it will use the PortfolioSerializer class to do this.

public class Portfolio {
    public ArrayList<Residence> residences;
    // introduced the serializer as a member of the class
    private PortfolioSerializer serializer;

    // revised the constructor to take a serializer when it is being initialised
    public Portfolio(PortfolioSerializer serializer) {
        this.serializer = serializer;
        try {
            residences = serializer.loadResidences();
        } catch (Exception e) {
            info(this, "Error loading residences: " + e.getMessage());
            residences = new ArrayList<Residence>();
        }
    }

    // introduced a new method to save all the residences to disk
    public boolean saveResidences() {
        try {
            serializer.saveResidences(residences);
            info(this, "Residences saved to file");
            return true;
        } catch (Exception e) {
            info(this, "Error saving residences: " + e.getMessage());
            return false;
        }
    }

    // method to add a residence to the list
    public void addResidence(Residence residence) {
        residences.add(residence);
    }

    public Residence getResidence(Long id) {
        Log.i(this.getClass().getSimpleName(), "Long parameter id: " + id);

        for (Residence res : residences) {
            if (id.equals(res.id)) {
                return res;
            }
        }
        return null;
    }
}
