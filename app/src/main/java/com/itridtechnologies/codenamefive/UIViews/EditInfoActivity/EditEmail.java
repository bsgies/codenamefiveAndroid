package com.itridtechnologies.codenamefive.UIViews.EditInfoActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
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

public class EditEmail extends AppCompatActivity implements View.OnClickListener, UniversalDialog.DialogListener {

    //const
    private static final String TAG = "EditEmail";

    //UI views
    private TextInputLayout mInputLayoutEmail;
    private ImageView mImageViewDone;
    private ProgressBar mBar;
    private PartnerRegistrationApi mRegistrationApi;

    //vars
    private Intent getEmail;
    private String email;
    private String emailOld;
    private boolean isEmailOky = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);
        Log.d(TAG, "onCreate: called..");

        //find views
        mInputLayoutEmail = findViewById(R.id.partner_email);
        mImageViewDone = findViewById(R.id.img_done);
        mBar = findViewById(R.id.pBar_loading);
        ImageView imageViewClose = findViewById(R.id.img_close);

        //listener
        mImageViewDone.setOnClickListener(this);
        imageViewClose.setOnClickListener(this);

        //init
        getEmail = getIntent();

        //init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mRegistrationApi = retrofit.create(PartnerRegistrationApi.class);

    }//onCreate

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: activity showing !");

        if (getEmail != null) {
            emailOld = getEmail.getStringExtra("EMAIL");
            mInputLayoutEmail.getEditText().setText(emailOld);
            Log.d(TAG, "onResume: email: " + emailOld);
        }

    }//resume

    @Override
    public void onDestroy() {
        super.onDestroy();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }//back

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_done) {
            //validate
            if (validateEmail() && isEmailOky) {
                //hide done img
                mImageViewDone.setVisibility(View.GONE);
                //show loading
                mBar.setVisibility(View.VISIBLE);
                //call api
                updateEmail();

            } else if (!isEmailOky) {
                //leave activity
                closeActivity();
            }// email valid
        }//if
        else if (v.getId() == R.id.img_close) {
            closeActivity();
        }
    }//on views click

    private boolean validateEmail() {
        Log.d(TAG, "validateEmail: validating...");
        email = mInputLayoutEmail.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            mInputLayoutEmail.setError("Enter new email");
            mInputLayoutEmail.setErrorIconDrawable(R.drawable.icon_error_til);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mInputLayoutEmail.setError("Enter a valid email");
            mInputLayoutEmail.setErrorIconDrawable(R.drawable.icon_error_til);
            return false;
        } else if (emailOld.equals(email)) {
            isEmailOky = false;
            return false;
        } else {
            Log.d(TAG, "validateEmail: validation success...");
            mInputLayoutEmail.setError(null);
            mInputLayoutEmail.setErrorIconDrawable(null);
            isEmailOky = true;
            return true;
        }
    }//end validate

    private void updateEmail() {
        Log.d(TAG, "updateEmail: calling api...");

        //pack email in json
        JsonObject object = new JsonObject();
        object.addProperty("email", email);

        Call<UpdateInfoResponse> call = mRegistrationApi.updatePartnerEmail(
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

                        Toast.makeText(EditEmail.this, "Email updated...", Toast.LENGTH_SHORT).show();
                        //save to pref
                        new PreferenceManager(EditEmail.this).savePartnerEmail(email);
                        closeActivity();

                    } else if (response.code() == 400 || response.code() == 500) {

                        Toast.makeText(EditEmail.this, "Email Already Exist !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showInvalidAccessDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<UpdateInfoResponse> call, @NotNull Throwable t) {
                //hide loading
                mBar.setVisibility(View.INVISIBLE);
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(EditEmail.this, "Internet Connection Error...", Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(this, PartnerEditProfile.class));
        finish();
        finishAffinity();
    }
}//end class