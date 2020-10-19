package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itridtechnologies.codenamefive.Models.PartnerVehicleModel.PartnerVehicle;
import com.itridtechnologies.codenamefive.Models.PartnerVehicleModel.PartnerVehicleResponse;
import com.itridtechnologies.codenamefive.R;
import com.itridtechnologies.codenamefive.RetrofitInterfaces.PartnerRegistrationApi;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.itridtechnologies.codenamefive.Const.Constants.BASE_URL;

public class PartnerVehicleInfo extends AppCompatActivity {

    //const
    private static final String TAG = "PartnerVehicleInfo";

    //ui views
    private TextView mTextViewVehicleType;
    private TextView mTextViewVehicleNumber;
    private ImageView mImageViewBack;
    private ProgressBar mBar;

    //vars
    private PartnerRegistrationApi mRegistrationApi;
    private String vehicleType;
    private String vehicleNumber;
    private ArrayList<PartnerVehicle> mPartnerVehicles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_vehicle_info);

        //find views
        mTextViewVehicleType = findViewById(R.id.tv_vehicle_type);
        mTextViewVehicleNumber = findViewById(R.id.tv_vehicle_num);
        mImageViewBack = findViewById(R.id.img_back);
        mBar = findViewById(R.id.progressBar);

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
        Log.d(TAG, "onResume: activity showing...");

        fetchVehicleData();
    }

    private void fetchVehicleData() {
        Log.d(TAG, "fetchVehicleData: fetching data...");
        mBar.setVisibility(View.VISIBLE);

        Call<PartnerVehicleResponse> call = mRegistrationApi.getVehicleInformation(getAuthToken());

        call.enqueue(new Callback<PartnerVehicleResponse>() {
            @Override
            public void onResponse(@NotNull Call<PartnerVehicleResponse> call, @NotNull Response<PartnerVehicleResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200 && response.body() != null) {

                        Log.d(TAG, "onResponse: success: " + response.body().isSuccess());

                        //get values from data[] array we received in body
                        mPartnerVehicles = response.body().getVehicleList();

                        //save values
                        vehicleType = mPartnerVehicles.get(0).getVehicleType();
                        vehicleNumber = mPartnerVehicles.get(0).getRegistrationNum();

                        //set data to UI text views
                        if (vehicleType != null && vehicleNumber != null) {
                            mTextViewVehicleType.setText(vehicleType);
                            mTextViewVehicleNumber.setText(vehicleNumber);
                        } else {
                            Log.d(TAG, "onResponse: some thing is wrong...NULL");
                        }
                    }//success
                    else if (response.code() == 400) {
                        Toast.makeText(PartnerVehicleInfo.this, "Access Denied !", Toast.LENGTH_SHORT).show();
                    }
                }
            }//response

            @Override
            public void onFailure(@NotNull Call<PartnerVehicleResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }//end api

    private String getAuthToken() {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaXJzdF9uYW1lIjoiaW1ybmEiLCJsYXN0X25hbWUiOiJyYXNoZWVkIiwiZW1haWwiOiJpbXJhbnJhc2hlZWQuZGV2ZWxvcGVyQG91dGxvb2suY28iLCJpZCI6MTA0LCJwcm9maWxlX3Bob3RvIjoicHVibGljL3VwbG9hZHMvcGFydG5lci85MTllMDlmYmMwYzRhODczMGM4MjVlYjUzODEzZjk3ZS5qcGciLCJwaG9uZV9udW1iZXIiOiIwMzA2NDQ2OTc5OSIsInN0YXR1cyI6InBlbmRpbmciLCJvbmxpbmVfc3RhdHVzIjowLCJpYXQiOjE2MDIyNDE3OTN9.P7ZztlTapq1t7CapraMECMUeqWji3TV1LDRqMT5AJ3Y";
    }
}//endClass