package com.gary.myrent.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.gary.myrent.R;
import com.gary.myrent.app.MyRentApp;
import com.gary.myrent.models.Portfolio;
import com.gary.myrent.models.Residence;

import java.util.ArrayList;

import static com.gary.android.helpers.IntentHelper.startActivityWithData;
import static com.gary.android.helpers.IntentHelper.startActivityWithDataForResult;

public class ResidenceListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private Portfolio portfolio;
    private ResidenceAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_residence_list);

        listView = (ListView) findViewById(R.id.residenceList);
        MyRentApp app = (MyRentApp) getApplication();
        portfolio = app.portfolio;

        adapter = new ResidenceAdapter(this, portfolio.residences);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_residence:
                Residence residence = new Residence();
                portfolio.addResidence(residence);
                startActivityWithDataForResult(this, ResidenceActivity.class, "RESIDENCE_ID", residence.id, 0);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.residencelist, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Residence residence = adapter.getItem(position);
        startActivityWithData(this, ResidenceActivity.class, "RESIDENCE_ID", residence.id);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}


class ResidenceAdapter extends ArrayAdapter<Residence> {
    private Context context;

    public ResidenceAdapter(Context context, ArrayList<Residence> residences) {
        super(context, 0, residences);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_residence, null);
        }
        Residence res = getItem(position);

        TextView geolocation = (TextView) convertView.findViewById(R.id.residence_list_item_geolocation);
        geolocation.setText(res.geolocation);

        TextView dateTextView = (TextView) convertView.findViewById(R.id.residence_list_item_dateTextView);
        dateTextView.setText(res.getDateString());

        CheckBox rentedCheckBox = (CheckBox) convertView.findViewById(R.id.residence_list_item_isrented);
        rentedCheckBox.setChecked(res.rented);

        return convertView;
    }
}