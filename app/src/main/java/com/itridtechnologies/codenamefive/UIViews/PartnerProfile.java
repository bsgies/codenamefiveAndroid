package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.itridtechnologies.codenamefive.R;
import com.itridtechnologies.codenamefive.UIViews.BankInfoActivity.PartnerBankInfo;
import com.itridtechnologies.codenamefive.utils.DataHelper;

public class PartnerProfile extends AppCompatActivity implements View.OnClickListener {

    //const
    private static final String TAG = "PartnerProfile";

    //ui views
    private TableRow rowEditProfile;
    private TableRow rowVehicleInfo;
    private TableRow rowBankInfo;
    private ImageView closeProfile;
    //..
    private TextView mTextViewPartnerName;
    private TextView mTextViewPartnerEmail;
    private TextView mTextViewPartnerPhone;
    private ImageView mImageViewPartnerPic;

    //vars
    private String partnerName;
    private String profileImgUrl;
    private String partnerEmail;
    private String partnerPhoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_profile);

        //findViews
        rowEditProfile = findViewById(R.id.tr_pt_edit_profile);
        rowVehicleInfo = findViewById(R.id.tr_pt_vehicle_info);
        rowBankInfo = findViewById(R.id.tr_pt_bank_info);
        closeProfile = findViewById(R.id.close_profile);
        mTextViewPartnerName = findViewById(R.id.profile_pt_name);
        mTextViewPartnerEmail = findViewById(R.id.profile_pt_email);
        mTextViewPartnerPhone = findViewById(R.id.profile_pt_phone);
        mImageViewPartnerPic = findViewById(R.id.img_profile_photo);

        //setListeners
        rowEditProfile.setOnClickListener(this);
        rowVehicleInfo.setOnClickListener(this);
        rowBankInfo.setOnClickListener(this);
        closeProfile.setOnClickListener(this);

    }//onCreate

    @Override
    protected void onStart() {
        super.onStart();
        //assign data
        if (DataHelper.USER_DATA_TRANSPORTER != null) {
            partnerName =
                    DataHelper.USER_DATA_TRANSPORTER.getData().getResults().getFirstName().toUpperCase()
                            + " " +
                            DataHelper.USER_DATA_TRANSPORTER.getData().getResults().getLastName().toUpperCase();

            profileImgUrl = DataHelper.PARTNER_PROFILE_IMG;

            partnerPhoneNo = DataHelper.USER_DATA_TRANSPORTER.getData().getResults().getPhoneNumber();

            partnerEmail = DataHelper.USER_DATA_TRANSPORTER.getData().getResults().getEmail();

        }
    }//onStart

    @Override
    protected void onResume() {
        super.onResume();
        UpdateUI();
    }//onResume

    private void UpdateUI() {

        mTextViewPartnerName.setText(partnerName);
        mTextViewPartnerEmail.setText(partnerEmail);
        mTextViewPartnerPhone.setText(partnerPhoneNo);

        //load img
        Glide
                .with(this)
                .load(profileImgUrl)
                .centerCrop()
                .placeholder(R.drawable.img_place_holder)
                .into(mImageViewPartnerPic);

    }//end fun

    @Override
    public void onClick(View v) {
        //handling click events
        switch (v.getId()) {

            case R.id.tr_pt_edit_profile:
                Intent intent1 = new Intent(this, PartnerEditProfile.class);
                startActivity(intent1);
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.tr_pt_vehicle_info:
                Intent intent2 = new Intent(this, PartnerVehicleInfo.class);
                startActivity(intent2);
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.tr_pt_bank_info:
                Intent intent3 = new Intent(this, PartnerBankInfo.class);
                startActivity(intent3);
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }//switch
    }//end OnClick
}//endClass