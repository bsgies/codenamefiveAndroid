package com.itridtechnologies.codenamefive.UIViews.EditInfoActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.itridtechnologies.codenamefive.Const.Constants.BASE_URL;

public class EditNextToKin extends AppCompatActivity implements UniversalDialog.DialogListener {

    //const
    private static final String TAG = "EditNextToKin";

    //ui views
    private TextInputLayout mInputLayoutName;
    private TextInputLayout mInputLayoutPhone;
    private ImageView mImageViewDone;
    private ImageView mImageViewClose;
    private ProgressBar mProgressBar;

    //vars
    private String emergencyName;
    private String emergencyPhone;
    private CountryCodePicker mCodePicker;
    private boolean isModified;

    private PartnerRegistrationApi mRegistrationApi;
    private PreferenceManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_next_to_kin);

        //find views
        mInputLayoutName = findViewById(R.id.emergency_name);
        mInputLayoutPhone = findViewById(R.id.emergency_contact);
        mImageViewDone = findViewById(R.id.img_done);
        mImageViewClose = findViewById(R.id.img_close);
        mProgressBar = findViewById(R.id.pBar_loading);

        //register picker with editText
        mCodePicker = findViewById(R.id.ccp);
        mCodePicker.registerCarrierNumberEditText(mInputLayoutPhone.getEditText());

        //init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mRegistrationApi = retrofit.create(PartnerRegistrationApi.class);

        //listeners
        mImageViewDone.setOnClickListener(v -> {
            if (validate() && isModified) {
                updateNextToKin();
            } else if (!isModified) {
                finish();
            }
        });

        mImageViewClose.setOnClickListener(v -> {
            if (isModified) {
                showChangeNotSavedDialog();
            } else {
                closeActivity();
            }
        });

    }//onCreate


    public boolean validate() {
        Log.d(TAG, "validate: validating...");

        if (mInputLayoutName.getEditText().getText().toString().trim().isEmpty() &&
                mInputLayoutPhone.getEditText().getText().toString().trim().isEmpty()) {
            mInputLayoutName.setError("");
            mInputLayoutPhone.setError("");
            mInputLayoutName.setErrorIconDrawable(null);
            mInputLayoutPhone.setErrorIconDrawable(null);
            //do data entered
            isModified = false;
            return false;
        } else if (mInputLayoutName.getEditText().getText().toString().trim().isEmpty()) {
            mInputLayoutName.setError("Emergency contact name required");
            mInputLayoutName.setErrorIconDrawable(R.drawable.icon_error);
            isModified = true;
            return false;
        } else if (mInputLayoutPhone.getEditText().getText().toString().trim().isEmpty()) {
            mInputLayoutPhone.setError("Emergency contact phone number required");
            mInputLayoutPhone.setErrorIconDrawable(R.drawable.icon_error);
            isModified = true;
            return false;
        } else if (!mCodePicker.isValidFullNumber()) {
            mInputLayoutPhone.setError("Invalid phone number");
            mInputLayoutPhone.setErrorIconDrawable(R.drawable.icon_error);
            isModified = true;
            return false;
        } else {
            //get values
            emergencyName = mInputLayoutName.getEditText().getText().toString().trim();
            emergencyPhone = mCodePicker.getFullNumberWithPlus();
            Log.d(TAG, "validate: success: " + emergencyName + " : " + emergencyPhone);

            mInputLayoutName.setError("");
            mInputLayoutPhone.setError("");
            mInputLayoutName.setErrorIconDrawable(null);
            mInputLayoutPhone.setErrorIconDrawable(null);

            isModified = true;
            return true;
        }
    }//end validate

    private void updateNextToKin() {
        Log.d(TAG, "updateNextToKin: calling api next to kin...");

        //show loading
        mProgressBar.setVisibility(View.VISIBLE);
        mImageViewDone.setVisibility(View.GONE);
        //prepare json object
        JsonObject object = new JsonObject();
        //values
        object.addProperty("contactName", emergencyName);
        object.addProperty("phoneNumber", emergencyPhone);

        Call<UpdateInfoResponse> call = mRegistrationApi.updateNextToKin(
                getAuthToken(),
                object
        );
        //enqueue

        call.enqueue(new Callback<UpdateInfoResponse>() {
            @Override
            public void onResponse(@NotNull Call<UpdateInfoResponse> call, @NotNull Response<UpdateInfoResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200 && response.body() != null) {
                        //hide loading
                        mProgressBar.setVisibility(View.GONE);
                        mManager = new PreferenceManager(EditNextToKin.this);

                        Log.d(TAG, "onResponse: success: " + response.body().isSuccess());
                        Log.d(TAG, "onResponse: message: " + response.body().getMsg());
                        //save to cache..
                        mManager.saveNextToKinName(emergencyName);
                        mManager.saveNextToKinPhone(emergencyPhone);

                        Toast.makeText(EditNextToKin.this, "Net of kin updated successfully!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    showInvalidAccessDialog();
                }

            }//response

            @Override
            public void onFailure(@NotNull Call<UpdateInfoResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
                mProgressBar.setVisibility(View.GONE);
                showNetworkDialog();
            }
        });

    }//end api

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

    private void showNetworkDialog() {
        DialogFragment fragment = new UniversalDialog(
                "Internet connection error",
                "Make sure you have working internet connection",
                "Retry",
                "Cancel",
                R.drawable.icon_no_internet,
                2,
                0
        );
        fragment.setCancelable(false);
        fragment.show(getSupportFragmentManager(), "NetDialog");
    }


    private void showChangeNotSavedDialog() {
        DialogFragment fragment = new UniversalDialog(
                "Unsaved changes",
                "You have unsaved changes, do you really want to leave this page?",
                "Yes, leave",
                "Cancel",
                R.drawable.icon_no_internet,
                3,
                4
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
        } else if (code == 0 || code == 3) {
            closeActivity();
        } else if (code == 2) {
            updateNextToKin();
        }
    }

    private void closeActivity() {
        startActivity(new Intent(this, PartnerEditProfile.class));
        finish();
        finishAffinity();
    }

}//end Class