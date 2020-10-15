package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.itridtechnologies.codenamefive.Models.Preferences.PreferenceManager;
import com.itridtechnologies.codenamefive.R;
import com.itridtechnologies.codenamefive.UIViews.EditInfoActivity.EditEmail;
import com.itridtechnologies.codenamefive.UIViews.EditInfoActivity.EditNextToKin;
import com.itridtechnologies.codenamefive.UIViews.EditInfoActivity.EditPhone;

public class PartnerEditProfile extends AppCompatActivity implements View.OnClickListener {

    //const
    private static final String TAG = "PartnerEditProfile";

    //ui views
    private TextView partnerEmail;
    private TextView partnerPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_edit_profile);

        //find views
        TableRow tableRowEmail = findViewById(R.id.row_edit_email);
        TableRow tableRowPhone = findViewById(R.id.row_partner_phone);
        TableRow tableRowKinName = findViewById(R.id.row_next_to_kin_name);
        TableRow tableRowKinPhone = findViewById(R.id.row_next_to_kin_contact);
        partnerEmail = findViewById(R.id.tv_partner_email);
        partnerPhone = findViewById(R.id.tv_partner_phone);
        RelativeLayout relativeLayoutPhone = findViewById(R.id.rel_lay_phone);

        //listeners
        tableRowEmail.setOnClickListener(this);
        tableRowPhone.setOnClickListener(this);
        tableRowKinName.setOnClickListener(this);
        tableRowKinPhone.setOnClickListener(this);
        relativeLayoutPhone.setOnClickListener(this);

    }//onCreate

    @Override
    protected void onResume() {
        super.onResume();

        //set email & get from cache
        partnerEmail.setText(
                new PreferenceManager(this).retrievePartnerEmail()
        );

        //set email & get from cache
        partnerPhone.setText(
                new PreferenceManager(this).retrievePartnerPhone()
        );
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.row_edit_email:
                navToEditEmailScreen();
                break;

            case R.id.row_partner_phone:
            case R.id.rel_lay_phone:
                navToEditPhoneScreen();
                break;

            case R.id.row_next_to_kin_name:
            case R.id.row_next_to_kin_contact:
                navToEditKinScreen();
        }

    }//onClick

    private void navToEditKinScreen() {
        Intent intent = new Intent(this, EditNextToKin.class);
        startActivity(intent);

        //anim
        overridePendingTransition(R.anim.slide_up_anim, R.anim.fade_out);
        Log.d(TAG, "navToEditPhoneScreen: " + partnerPhone.getText().toString());

    }

    private void navToEditPhoneScreen() {
        Intent intent = new Intent(this, EditPhone.class);
        intent.putExtra("PHONE", partnerPhone.getText().toString());
        startActivity(intent);

        //anim
        overridePendingTransition(R.anim.slide_up_anim, R.anim.fade_out);
        Log.d(TAG, "navToEditPhoneScreen: " + partnerPhone.getText().toString());
    }

    private void navToEditEmailScreen() {
        Intent intent = new Intent(this, EditEmail.class);
        intent.putExtra("EMAIL", partnerEmail.getText().toString());
        startActivity(intent);
        //anim
        overridePendingTransition(R.anim.slide_up_anim, R.anim.fade_out);
        Log.d(TAG, "navToEditEmailScreen: " + partnerEmail.getText().toString());
    }//end nav

}//endClass