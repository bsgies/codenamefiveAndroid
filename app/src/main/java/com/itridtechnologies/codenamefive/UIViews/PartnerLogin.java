package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.itridtechnologies.codenamefive.Models.PartnerLoginModel.LoginResponse;
import com.itridtechnologies.codenamefive.R;
import com.itridtechnologies.codenamefive.utils.ApplicationManager;
import com.itridtechnologies.codenamefive.utils.CNF;
import com.itridtechnologies.codenamefive.utils.DataHelper;
import com.itridtechnologies.codenamefive.utils.TinyDB;

import java.util.regex.Pattern;

public class PartnerLogin extends AppCompatActivity {

    //const
    private static final String TAG = "PartnerLogin";

    //pattern phone
    private static final Pattern phoneNumPattern = Pattern.compile("^\\s*(?:\\+" +
            "?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$");

    private TextInputLayout partnerPhoneEmail;
    private Button continuePartnerLogin;
    private Button notPartnerYet;
    private CardView loginCard;
    private Animation animation;
    private Boolean inputOk;
    private String keyboardType;
    private String PARTNER_EMAIL_PHONE = null;
    //..
    private final TinyDB mTinyDB = new TinyDB(CNF.getAppContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_login);

        //find views
        partnerPhoneEmail = findViewById(R.id.partner_email_or_phone);
        continuePartnerLogin = findViewById(R.id.btn_continue_partner_login);
        notPartnerYet = findViewById(R.id.btn_goto_registration);
        loginCard = findViewById(R.id.partner_login_cardView);

        //loadAnimation
        animation = AnimationUtils.loadAnimation(this, R.anim.right_to_left_slide);
        loginCard.setAnimation(animation);
        //...........................................................
        //LISTENERS
        //...........................................................
        continuePartnerLogin.setOnClickListener(v -> {
            //inputValidationsMethod
            validateInput();
            if (inputOk) {
                partnerPhoneEmail.setError(null);
                Intent intent = new Intent(PartnerLogin.this, PartnerLoginSecurityCode.class);
                intent.putExtra("riderPhone", PARTNER_EMAIL_PHONE);
                intent.putExtra("keyboardType", keyboardType);
                startActivity(intent);
                finish();
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }//end if
        });
        //...........................................................

    }//onCreate


    @Override
    protected void onStart() {
        super.onStart();

        //look for previous user data in preferences
        LoginResponse userData = mTinyDB.getObject("user_data", LoginResponse.class);

        if (userData != null) {
            Log.d(TAG, "onStart: user data found !");
            DataHelper.USER_DATA_TRANSPORTER = userData;
            DataHelper.AUTH_TOKEN = DataHelper.USER_DATA_TRANSPORTER.getData().getToken();
            DataHelper.PARTNER_PROFILE_IMG += userData.getData().getResults().getProfilePhoto();

            //nav directly to dashboard
            finish();
            ApplicationManager.intent(PartnerDashboardMain.class);

        } else {
            Log.d(TAG, "onStart: user data not found !");
        }

    }//onStart

    //methods
    public void validateInput() {
        //getInput
        try {
            PARTNER_EMAIL_PHONE = partnerPhoneEmail.getEditText().getText().toString().trim();
        } catch (NullPointerException ex) {
            Log.d(TAG, "validateInput: " + ex.getMessage());
        }

        this.inputOk = false;
        if (PARTNER_EMAIL_PHONE.isEmpty()) {
            this.partnerPhoneEmail.setError("Field can't be empty");
            this.partnerPhoneEmail.requestFocus();
            this.inputOk = false;

        } else if (Patterns.EMAIL_ADDRESS.matcher(PARTNER_EMAIL_PHONE).matches()) {
            this.inputOk = true;
            this.keyboardType = "password";

        } else if (phoneNumPattern.matcher(PARTNER_EMAIL_PHONE).matches()) {
            this.inputOk = true;
            this.keyboardType = "phone";

        } else {
            this.partnerPhoneEmail.setError("Invalid Phone or Email");
            this.partnerPhoneEmail.requestFocus();
            this.inputOk = false;
        }
    }// end validate

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}//end class