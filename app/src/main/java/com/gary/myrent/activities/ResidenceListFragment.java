package com.gary.myrent.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.gary.android.helpers.IntentHelper;
import com.gary.myrent.R;
import com.gary.myrent.app.MyRentApp;
import com.gary.myrent.models.Portfolio;
import com.gary.myrent.models.Residence;

import java.util.ArrayList;

public class ResidenceListFragment extends ListFragment implements OnItemClickListener, AbsListView.MultiChoiceModeListener {
    private ArrayList<Residence> residences;
    private Portfolio portfolio;
    private ResidenceAdapter adapter;
    private ListView listView;
    MyRentApp app;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.app_name);

        app = MyRentApp.getApp();
        portfolio = app.portfolio;
        residences = portfolio.residences;

        adapter = new ResidenceAdapter(getActivity(), residences);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);
        listView = (ListView) v.findViewById(android.R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(this);
        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Residence res = ((ResidenceAdapter) getListAdapter()).getItem(position);
        Intent i = new Intent(getActivity(), ResidencePagerActivity.class);
        i.putExtra(ResidenceFragment.EXTRA_RESIDENCE_ID, res.id);
        startActivityForResult(i, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ResidenceAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.residencelist, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_residence:
                Residence residence = new Residence();
                portfolio.addResidence(residence);

                Intent i = new Intent(getActivity(), ResidencePagerActivity.class);
                i.putExtra(ResidenceFragment.EXTRA_RESIDENCE_ID, residence.id);
                startActivityForResult(i, 0);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Residence residence = adapter.getItem(position);
        IntentHelper.startActivityWithData(getActivity(), ResidencePagerActivity.class, "RESIDENCE_ID", residence.id);
    }


    /* ************ MultiChoiceModeListener methods (begin) *********** */
    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.residence_list_context, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_item_delete_residence:
                deleteResidence(actionMode);
                return true;
            default:
                return false;
        }

    }

    private void deleteResidence(ActionMode actionMode) {
        for (int i = adapter.getCount() - 1; i >= 0; i--) {
            if (listView.isItemChecked(i)) {
                portfolio.deleteResidence(adapter.getItem(i));
            }
        }
        actionMode.finish();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
    }

  /* ************ MultiChoiceModeListener methods (end) *********** */

    class ResidenceAdapter extends ArrayAdapter<Residence> {
        private Context context;

        public ResidenceAdapter(Context context, ArrayList<Residence> residences) {
            super(context, 0, residences);
            this.context = context;
        }

        @SuppressLint("InflateParams")
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
}