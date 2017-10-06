package com.gary.myrent.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import static com.gary.android.helpers.IntentHelper.selectContact;


public class ResidenceActivity extends AppCompatActivity implements TextWatcher, OnCheckedChangeListener, View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private EditText geolocation;
    private Residence residence;
    private CheckBox rented;
    private Button dateButton;
    private Portfolio portfolio;
    private Button tenantButton;
    private Button reportButton;
    private String emailAddress = "";
    private static final int REQUEST_CONTACT = 1; // prospective tenant ID we will use for the implicit Intent
    private Intent data; // This field is initialized in `onActivityResult`


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_residence);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        geolocation = (EditText) findViewById(R.id.geolocation);
        residence = new Residence();
        geolocation.addTextChangedListener(this);

        dateButton = (Button) findViewById(R.id.registration_date);
        dateButton.setOnClickListener(this);

        rented = (CheckBox) findViewById(R.id.isrented);
        rented.setOnCheckedChangeListener(this);

        MyRentApp app = (MyRentApp) getApplication();
        portfolio = app.portfolio;

        tenantButton = (Button) findViewById(R.id.tenant);
        reportButton = (Button) findViewById(R.id.residence_reportButton);

        Long resId = (Long) getIntent().getExtras().getSerializable("RESIDENCE_ID");
        residence = portfolio.getResidence(resId);
        if (residence != null) {
            updateControls(residence);
        }

    }

    public void updateControls(Residence residence) {
        geolocation.setText(residence.geolocation);
        tenantButton.setOnClickListener(this);
        reportButton.setOnClickListener(this);
        // fix issue that prevents the rented residence field from being serialized
        // rented.setChecked(residence.rented)
        rented.setOnCheckedChangeListener(this);
        dateButton.setText(residence.getDateString());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        residence.setGeolocation(editable.toString());
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Log.i(this.getClass().getSimpleName(), "rented Checked");
        residence.rented = isChecked;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Date date = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
        residence.date = date.getTime();
        dateButton.setText(residence.getDateString());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registration_date:
                Calendar c = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(this, this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                dpd.show();
                break;
            case R.id.tenant:
                selectContact(this, REQUEST_CONTACT);
                break;
            case R.id.residence_reportButton:
                sendEmail(this, emailAddress,
                        getString(R.string.residence_report_subject), residence.getResidenceReport(this));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navigateUp(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        portfolio.saveResidences();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONTACT:
                this.data = data;
                checkContactsReadPermission();
                break;
        }
    }

    // adding permission checks prior reading the contact details
    // we will farm the reading of the contact details into this new private method

    private void readContact() {
        String name = getContact(this, data);
        emailAddress = getEmail(this, data);
        tenantButton.setText(name + " : " + emailAddress);
        residence.tenant = name;
    }

    //https://developer.android.com/training/permissions/requesting.html
    private void checkContactsReadPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            //We can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT);
        } else {
            //We already have permission, so go head and read the contact
            readContact();
        }
    }

    // When user responds to dialog box (allow/deny)
    // the system invokes the app's onRequestPermissionsResult() method passing in users response
    // Note if permission is granted, the readContact method is called, otherwise it is ignored
    // https://developer.android.com/training/permissions/requesting.html
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