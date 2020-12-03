package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.itridtechnologies.codenamefive.Models.PartnerLoginModel.LoginResponse;
import com.itridtechnologies.codenamefive.NetworkManager.RestApiManager;
import com.itridtechnologies.codenamefive.R;
import com.itridtechnologies.codenamefive.utils.ApplicationManager;
import com.itridtechnologies.codenamefive.utils.CNF;
import com.itridtechnologies.codenamefive.utils.DataHelper;
import com.itridtechnologies.codenamefive.utils.TinyDB;
import com.itridtechnologies.codenamefive.utils.UniversalDialog;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PartnerLoginSecurityCode extends AppCompatActivity implements UniversalDialog.DialogListener {

    private static final String TAG = "PartnerLoginSecurityCod";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private final String msg = "We sent a code to";
    private final String msg2 = "Enter your password to login";
    private CardView partnerSecurityCardView;
    private Animation animation;
    private String LOGIN_PASSWORD = null;
    //preference turbo
    private final TinyDB mTinyDB = new TinyDB(CNF.getAppContext());
    private TextView infoMessage;
    private TextView displayPhone;
    MediaPlayer player;
    //ui views
    private TextInputLayout partnerSecurityCode;
    private String LOGIN_EMAIL_OR_PHONE = null;
    //..
    private Button mButtonConfirmLogin;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_login_security_code);

        //findViews
        partnerSecurityCode = findViewById(R.id.partner_login_password);
        partnerSecurityCardView = findViewById(R.id.partner_security_code_cardView);
        infoMessage = findViewById(R.id.msg2);
        displayPhone = findViewById(R.id.tv_display_phone);

        mButtonConfirmLogin = findViewById(R.id.btn_confirm_login);
        mProgressBar = findViewById(R.id.progress_bar_btn_continue);

        //loadAnimation
        animation = AnimationUtils.loadAnimation(this, R.anim.right_to_left_slide);
        partnerSecurityCardView.setAnimation(animation);

        //...........................................................
        //LISTENERS
        mButtonConfirmLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateSecurityCode()) {
                    if (checkGooglePlayServices()) {

                        if (ApplicationManager.isNetworkOk()) {
                            //show button loading
                            mButtonConfirmLogin.setBackgroundColor(getResources().getColor(R.color.backgroundLightGrey));
                            mButtonConfirmLogin.setText("");
                            mProgressBar.setVisibility(View.VISIBLE);

                            loginPartner();

                        } else {
                            showInternetErrorDialog();
                            mButtonConfirmLogin.setBackgroundColor(getResources().getColor(R.color.appThemeColor));
                            mButtonConfirmLogin.setText("Login");
                            mProgressBar.setVisibility(View.GONE);
                        }

                    }//end if
                }//end if
            }
        });//end listener
        //...........................................................
    }//onCreate

    @Override
    protected void onStart() {
        super.onStart();

        //setIntentValues
        getIntentValues();
    }

    //public methods

    public boolean checkGooglePlayServices() {
        Log.d(TAG, "checkGooglePlayServices: Checking Google Services Version...");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(PartnerLoginSecurityCode.this);
        //check for correct version
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "checkGooglePlayServices: Google Play Services Is Working!");
            //user can now make make request
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //error occurred but can be fixed
            Log.d(TAG, "checkGooglePlayServices: Error Occurred But Google Can Fix It!");
            //open a dialog from google to resolve issue
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(PartnerLoginSecurityCode.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Map Request Cannot Be Made...", Toast.LENGTH_SHORT).show();
        }
        return false;
    }//end check play services

    public boolean validateSecurityCode() {
        //getInput

        if (partnerSecurityCode.getEditText().getText().toString().trim().isEmpty()) {
            partnerSecurityCode.setError("Field can't be empty");
            return false;

        } else {
            LOGIN_PASSWORD = partnerSecurityCode.getEditText().getText().toString().trim();
            partnerSecurityCode.setError(null);
            return true;
        }
    }// end validate

    public void getIntentValues() {
        Intent intent = getIntent();
        String EXTRA_PHONE = intent.getStringExtra("riderPhone");
        String EXTRA_KEYBOARD = intent.getStringExtra("keyboardType");
        LOGIN_EMAIL_OR_PHONE = intent.getStringExtra("riderPhone");

        if (!EXTRA_PHONE.isEmpty() && EXTRA_KEYBOARD.equals("phone")) {

            this.infoMessage.setText(this.msg);
            this.partnerSecurityCode.getEditText().setHint("Security code");
            this.displayPhone.setText(EXTRA_PHONE);
            this.partnerSecurityCode.getEditText().setInputType(3);

        } else if (EXTRA_KEYBOARD.equals("password")) {
            this.infoMessage.setText(this.msg2);
            this.partnerSecurityCode.getEditText().setHint("Enter your password");
            this.partnerSecurityCode.getEditText().setInputType(1);
        }
    }//end getValues

    @Override
    public void finish() {
        super.finish();
        //close anim
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void loginPartner() {
        Log.d(TAG, "loginPartner: calling api...");

        JsonObject loginParams = new JsonObject();

        //put values
        loginParams.addProperty("email", LOGIN_EMAIL_OR_PHONE);
        loginParams.addProperty("password", LOGIN_PASSWORD);

        Call<LoginResponse> call = RestApiManager.getRestApiService().loginPartner(loginParams);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {

                //hide loading
                mButtonConfirmLogin.setBackgroundColor(getResources().getColor(R.color.appThemeColor));
                mButtonConfirmLogin.setText("Login");
                mProgressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        //ApplicationManager.toast("Successfully logged in !\nPreference data updated...");

                        Object userData = null;
                        userData = response.body();

                        //save user data in preferences
                        mTinyDB.putObject("user_data", userData);

                        //also save in helper for accessing data later in app
                        DataHelper.USER_DATA_TRANSPORTER = response.body();

                        DataHelper.AUTH_TOKEN = DataHelper.USER_DATA_TRANSPORTER.getData().getToken();

                        //saving user profile img url
                        DataHelper.PARTNER_PROFILE_IMG += response.body().getData().getResults().getProfilePhoto();

                        createVibeAlertWithSound();
                        showLoginSuccessDialog();

                    }
                }//success

                else if (response.code() == 400) {
                    ApplicationManager.toast("Email or password doesn't matched !");

                    mButtonConfirmLogin.setBackgroundColor(getResources().getColor(R.color.appThemeColor));
                    mButtonConfirmLogin.setText("Login");
                    mProgressBar.setVisibility(View.GONE);
                }

            }//response

            @Override
            public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());

                mButtonConfirmLogin.setBackgroundColor(getResources().getColor(R.color.appThemeColor));
                mButtonConfirmLogin.setText("Login");
                mProgressBar.setVisibility(View.GONE);

                showServerErrorDialog();
            }
        });

    }//end fun

    private void showServerErrorDialog() {
        UniversalDialog dialog = new UniversalDialog(
                "Could'nt connect to server",
                "Server unreachable, try again later.",
                "Retry",
                getResources().getString(R.string.dialog_cancel),
                R.drawable.icon_error,
                2,
                0
        );
        dialog.show(getSupportFragmentManager(), "Server Error");
    }

    private void showLoginSuccessDialog() {
        DialogFragment dialog = new UniversalDialog(
                "Login Successful",
                "You have been logged in successfully into the system.",
                "Continue",
                "",
                R.drawable.icon_cloud_done,
                3,
                0
        );
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "Application Done");
    }

    private void showInternetErrorDialog() {
        UniversalDialog dialog = new UniversalDialog(
                getResources().getString(R.string.dialog_con_error),
                getResources().getString(R.string.connection_warning),
                getResources().getString(R.string.turn_on),
                getResources().getString(R.string.dialog_cancel),
                R.drawable.icon_no_internet,
                1,
                0
        );
        dialog.show(getSupportFragmentManager(), "No Internet");
    }

    private void createVibeAlertWithSound() {
        Log.d(TAG, "createVibeAlertWithSound: trying to give haptic feedback...");
        //init vibe
        Vibrator vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //check for hardware
        if (vibe.hasVibrator() && player == null) {

            if (Build.VERSION.SDK_INT >= 26) {
                //vibrate
                vibe.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                player = MediaPlayer.create(this, R.raw.swiftly);
                //sound
                player.start();
                //release player
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        player.release();
                        player = null;
                    }
                });
            } else {
                player = MediaPlayer.create(this, R.raw.swiftly);
                //sound
                player.start();
                //release player
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        player.release();
                        player = null;
                    }
                });
                vibe.vibrate(500);
            }
        }//end if

    }//end vibration

    @Override
    public void onDialogButtonClicked(int code) {
        if (code != 0) {
            if (code == 1) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            } else if (code == 2) {
                //server unreachable (clear text communication not possible)
                if (!ApplicationManager.isNetworkOk()) {
                    showInternetErrorDialog();
                } else {
                    loginPartner();
                }

            } else if (code == 3) {
                navToDashboard();
                finish();
                finishAffinity();
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    }

    private void navToDashboard() {

        Intent intent = new Intent(PartnerLoginSecurityCode.this, PartnerDashboardMain.class);
        startActivity(intent);
    }
}//endClass
