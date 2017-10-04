package com.gary.myrent.models;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

// to perform the actual serialization
// we provide a new model class to save & restore a list of Residences

public class PortfolioSerializer
{
    private Context mContext;
    private String mFilename;

    public PortfolioSerializer(Context c, String f)
    {
        mContext = c;
        mFilename = f;
    }

    public void saveResidences(ArrayList<Residence> residences) throws JSONException, IOException
    {
        // build an array in JSON
        JSONArray array = new JSONArray();
        for (Residence c : residences)
            array.put(c.toJSON());

        // write the file to disk
        Writer writer = null;
        try
        {
            OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        }
        finally
        {
            if (writer != null)
                writer.close();
        }
    }

    public ArrayList<Residence> loadResidences() throws IOException, JSONException
    {
        ArrayList<Residence> residences = new ArrayList<Residence>();
        BufferedReader reader = null;
        try
        {
            // open and read the file into a StringBuilder
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                // line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            // parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            // build the array of residences from JSONObjects
            for (int i = 0; i < array.length(); i++)
            {
                residences.add(new Residence(array.getJSONObject(i)));
            }
        }
        catch (FileNotFoundException e)
        {
            // we will ignore this one, since it happens when we start fresh
        }
        finally
        {
            if (reader != null)
                reader.close();
        }
        return residences;
    }
}
