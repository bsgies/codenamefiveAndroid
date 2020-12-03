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
import com.itridtechnologies.codenamefive.NetworkManager.RestApiManager;
import com.itridtechnologies.codenamefive.R;
import com.itridtechnologies.codenamefive.NetworkManager.PartnerRegistrationApi;
import com.itridtechnologies.codenamefive.utils.DataHelper;

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

        Call<PartnerVehicleResponse> call = RestApiManager.getRestApiService().getVehicleInformation
                (
                        DataHelper.AUTH_TOKEN
                );

        call.enqueue(new Callback<PartnerVehicleResponse>() {
            @Override
            public void onResponse(@NotNull Call<PartnerVehicleResponse> call, @NotNull Response<PartnerVehicleResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200 && response.body() != null) {

                        Log.d(TAG, "onResponse: success: " + response.body().isSuccess());
                        mBar.setVisibility(View.GONE);

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
                        mBar.setVisibility(View.GONE);
                    }
                }
            }//response

            @Override
            public void onFailure(@NotNull Call<PartnerVehicleResponse> call, @NotNull Throwable t) {
                mBar.setVisibility(View.GONE);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }//end api

}//endClass