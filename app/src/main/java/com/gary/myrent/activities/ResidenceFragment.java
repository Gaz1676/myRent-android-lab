package com.gary.myrent.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;

import com.gary.myrent.R;
import com.gary.myrent.app.MyRentApp;
import com.gary.myrent.models.Portfolio;
import com.gary.myrent.models.Residence;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.gary.android.helpers.ContactHelper.getContact;
import static com.gary.android.helpers.ContactHelper.getEmail;
import static com.gary.android.helpers.ContactHelper.sendEmail;
import static com.gary.android.helpers.IntentHelper.navigateUp;

public class ResidenceFragment extends Fragment implements TextWatcher,
        OnCheckedChangeListener,
        OnClickListener,
        DatePickerDialog.OnDateSetListener
{
    public static   final String  EXTRA_RESIDENCE_ID = "myrent.RESIDENCE_ID";
    private static  final int     REQUEST_CONTACT = 1;

    private EditText geolocation;
    private CheckBox rented;
    private Button   dateButton;
    private Button   tenantButton;
    private Button   reportButton;

    private Residence   residence;
    private Portfolio   portfolio;

    private String emailAddress = "";
    // This field is initialized in `onActivityResult`.
    private Intent data;
    MyRentApp app;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Long resId = (Long)getArguments().getSerializable(EXTRA_RESIDENCE_ID);

        app = MyRentApp.getApp();
        portfolio = app.portfolio;
        residence = portfolio.getResidence(resId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,  parent, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_residence, parent, false);

        addListeners(v);
        updateControls(residence);

        return v;
    }

    private void addListeners(View v)
    {
        geolocation  = (EditText) v.findViewById(R.id.geolocation);
        dateButton   = (Button)   v.findViewById(R.id.registration_date);
        rented       = (CheckBox) v.findViewById(R.id.isrented);
        tenantButton = (Button)   v.findViewById(R.id.tenant);
        reportButton = (Button)   v.findViewById(R.id.residence_reportButton);


        geolocation .addTextChangedListener(this);
        dateButton  .setOnClickListener(this);
        rented      .setOnCheckedChangeListener(this);
        tenantButton.setOnClickListener(this);
        reportButton.setOnClickListener(this);
    }

    public void updateControls(Residence residence)
    {
        geolocation.setText(residence.geolocation);
        rented.setChecked(residence.rented);
        dateButton.setText(residence.getDateString());
        tenantButton.setText("Tenant: "+residence.tenant);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home: navigateUp(getActivity());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        portfolio.saveResidences();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != Activity.RESULT_OK)
        {
            return;
        }

        switch (requestCode)
        {
            case REQUEST_CONTACT:
                this.data = data;
                checkContactsReadPermission();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {}

    @Override
    public void afterTextChanged(Editable c)
    {
        Log.i(this.getClass().getSimpleName(), "geolocation " + c.toString());
        residence.geolocation = c.toString();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        residence.rented = isChecked;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.registration_date      : Calendar c = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog (getActivity(), this,
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH));
                dpd.show();
                break;
            case R.id.tenant :
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, REQUEST_CONTACT);
                tenantButton.setText("Tenant: "+residence.tenant);
                break;
            case R.id.residence_reportButton :
                sendEmail(getActivity(), emailAddress, getString(R.string.residence_report_subject), residence.getResidenceReport(getActivity()));
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    {
        Date date = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
        residence.date = date.getTime();
        dateButton.setText(residence.getDateString());
    }

    //https://developer.android.com/training/permissions/requesting.html
    private void checkContactsReadPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            //We can request the permission.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT);
        }
        else {
            //We already have permission, so go head and read the contact
            readContact();
        }
    }

    private void readContact() {
        String name = getContact(getActivity(), data);
        emailAddress = getEmail(getActivity(), data);
        residence.tenant = name;
        tenantButton.setText("Tenant: "+residence.tenant);
    }

    //https://developer.android.com/training/permissions/requesting.html
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    readContact();
                }
            }
        }
    }
}
