package com.itridtechnologies.codenamefive.UIViews.EditInfoActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.itridtechnologies.codenamefive.Models.Preferences.PreferenceManager;
import com.itridtechnologies.codenamefive.Models.UpdateInfoResponse;
import com.itridtechnologies.codenamefive.R;
import com.itridtechnologies.codenamefive.RetrofitInterfaces.PartnerRegistrationApi;
import com.itridtechnologies.codenamefive.UIViews.PartnerEditProfile;
import com.itridtechnologies.codenamefive.UIViews.PartnerLogin;
import com.itridtechnologies.codenamefive.utils.UniversalDialog;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.itridtechnologies.codenamefive.Const.Constants.BASE_URL;

public class EditPhone extends AppCompatActivity implements View.OnClickListener, UniversalDialog.DialogListener {

    //const
    private static final String TAG = "EditPhone";

    //UI views
    private TextInputLayout mInputLayoutPhone;
    private ImageView mImageViewDone;
    private ProgressBar mBar;
    private PartnerRegistrationApi mRegistrationApi;

    //vars
    private Intent getPhone;
    private String phone;
    private String phoneOld;
    private boolean isPhoneOky = true;
    private CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone);

        //find views
        mInputLayoutPhone = findViewById(R.id.partner_phone);
        mImageViewDone = findViewById(R.id.img_done);
        mBar = findViewById(R.id.pBar_loading);
        ImageView imageViewClose = findViewById(R.id.img_close);

        //listener
        mImageViewDone.setOnClickListener(this);
        imageViewClose.setOnClickListener(this);

        //init
        getPhone = getIntent();

        //ccp
        ccp = findViewById(R.id.ccPicker);

        //init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mRegistrationApi = retrofit.create(PartnerRegistrationApi.class);


    }//OnCreate

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: activity showing !");

        /*register edt_phone to ccp
        in order to format number */
        ccp.registerCarrierNumberEditText(mInputLayoutPhone.getEditText());

        if (getPhone != null) {
            phoneOld = getPhone.getStringExtra("PHONE");
            phoneOld = phoneOld.replace("+92", "");
            mInputLayoutPhone.getEditText().setText(phoneOld);
            Log.d(TAG, "onResume: phone: " + phoneOld);
        }

    }//resume

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivity();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_done) {
            //validate
            if (validatePhone() && isPhoneOky) {
                Log.d(TAG, "onClick: phone full: " + phone);
                //hide done img
                mImageViewDone.setVisibility(View.GONE);
                //show loading
                mBar.setVisibility(View.VISIBLE);
                //call api
                updatePhone();

            } else if (!isPhoneOky) {
                //leave activity
                closeActivity();

            }// email valid
        }//if
        else if (v.getId() == R.id.img_close) {
            closeActivity();
        }
    }//on Click

    private boolean validatePhone() {
        Log.d(TAG, "validateEmail: validating...");

        if (mInputLayoutPhone.getEditText().getText().toString().trim().isEmpty()) {
            mInputLayoutPhone.setError("Enter phone number");
            mInputLayoutPhone.setErrorIconDrawable(R.drawable.icon_error_til);
            return false;
        } else if (!ccp.isValidFullNumber()) {
            mInputLayoutPhone.setError("Phone number not valid");
            mInputLayoutPhone.setErrorIconDrawable(R.drawable.icon_error_til);
            return false;
        } else if (phoneOld.equals(ccp.getFullNumberWithPlus())) {
            isPhoneOky = false;
            return false;
        } else {
            //get full number
            phone = ccp.getFullNumberWithPlus();
            mInputLayoutPhone.setError(null);
            mInputLayoutPhone.setErrorIconDrawable(null);
            isPhoneOky = true;
            //
            Log.d(TAG, "validatePhone: validation success..." + phone);
            return true;
        }
    }//end validate

    private void updatePhone() {
        Log.d(TAG, "updatePhone: calling api...");

        //pack email in json
        JsonObject object = new JsonObject();
        object.addProperty("phone", phone);

        Call<UpdateInfoResponse> call = mRegistrationApi.updatePartnerPhone(
                getAuthToken(),
                object
        );

        call.enqueue(new Callback<UpdateInfoResponse>() {
            @Override
            public void onResponse(@NotNull Call<UpdateInfoResponse> call, @NotNull Response<UpdateInfoResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200 && response.body() != null) {

                        //hide loading
                        mBar.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onResponse: success: " + response.body().isSuccess());
                        Log.d(TAG, "onResponse: message: " + response.body().getMsg());

                        Toast.makeText(EditPhone.this, "Phone number updated !", Toast.LENGTH_SHORT).show();
                        //save to pref
                        new PreferenceManager(EditPhone.this).savePartnerPhone(phone);
                        closeActivity();
                    }
                } else if (response.code() == 400) {
                    Toast.makeText(EditPhone.this, "Phone already exist !", Toast.LENGTH_SHORT).show();
                    mImageViewDone.setVisibility(View.VISIBLE);
                    mBar.setVisibility(View.GONE);
                } else {
                    mBar.setVisibility(View.GONE);
                    showInvalidAccessDialog();
                }
            }//response

            @Override
            public void onFailure(@NotNull Call<UpdateInfoResponse> call, @NotNull Throwable t) {
                //hide loading
                mBar.setVisibility(View.INVISIBLE);
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(EditPhone.this, "Internet Connection Error...", Toast.LENGTH_SHORT).show();
            }
        });
    }//email api

    private String getAuthToken() {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaXJzdF9uYW1lIjoiaW1ybmEiLCJsYXN0X25hbWUiOiJyYXNoZWVkIiwiZW1haWwiOiJpbXJhbnJhc2hlZWQuZGV2ZWxvcGVyQG91dGxvb2suY28iLCJpZCI6MTA0LCJwcm9maWxlX3Bob3RvIjoicHVibGljL3VwbG9hZHMvcGFydG5lci85MTllMDlmYmMwYzRhODczMGM4MjVlYjUzODEzZjk3ZS5qcGciLCJwaG9uZV9udW1iZXIiOiIwMzA2NDQ2OTc5OSIsInN0YXR1cyI6InBlbmRpbmciLCJvbmxpbmVfc3RhdHVzIjowLCJpYXQiOjE2MDIyNDE3OTN9.P7ZztlTapq1t7CapraMECMUeqWji3TV1LDRqMT5AJ3Y";
    }

    private void showInvalidAccessDialog() {
        DialogFragment fragment = new UniversalDialog(
                "Invalid session",
                "Your session has expired",
                "Okay",
                "",
                R.drawable.icon_error,
                1,
                0
        );
        fragment.setCancelable(false);
        fragment.show(getSupportFragmentManager(), "AuthDialog");
    }

    @Override
    public void onDialogButtonClicked(int code) {
        if (code == 1) {
            finish();
            finishAffinity();
            startActivity(new Intent(this, PartnerLogin.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private void closeActivity() {
        finish();
        finishAffinity();
        startActivity(new Intent(this, PartnerEditProfile.class));

    }
}//end class