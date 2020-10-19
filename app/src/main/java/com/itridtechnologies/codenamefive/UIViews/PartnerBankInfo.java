package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.itridtechnologies.codenamefive.Const.Constants;
import com.itridtechnologies.codenamefive.Models.PartnerPayment.GatewayResponse;
import com.itridtechnologies.codenamefive.R;
import com.itridtechnologies.codenamefive.RetrofitInterfaces.PartnerRegistrationApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PartnerBankInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //const
    private static final String TAG = "PartnerBankInfo";
    //ui views
    private Spinner mSpinnerPayoutMethods;
    //vars
    private List<String> payoutMethodsList;
    private PartnerRegistrationApi mRegistrationApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_bank_info);

        //find views
        mSpinnerPayoutMethods = findViewById(R.id.spinner_payouts);
        mSpinnerPayoutMethods.setOnItemSelectedListener(this);

        //init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRegistrationApi = retrofit.create(PartnerRegistrationApi.class);

    }//onCreate

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: showing...");

        getGatewaysList();
    }

    private void setUpSpinner() {

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, payoutMethodsList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // attaching data adapter to spinner
        mSpinnerPayoutMethods.setAdapter(dataAdapter);

    }//end spinner

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected: selected option: " + parent.getItemAtPosition(position).toString());
    }//spinner item select listener

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void getGatewaysList() {

        Call<GatewayResponse> call = mRegistrationApi.getPaymentGateways();

        call.enqueue(new Callback<GatewayResponse>() {
            @Override
            public void onResponse(Call<GatewayResponse> call, Response<GatewayResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200 && response.body() != null) {
                        Log.d(TAG, "onResponse: success: " + response.body().isSuccess());
                        payoutMethodsList = new ArrayList<>();

                        //get list of gateways
                        payoutMethodsList.add("Select a payout method");

                        for (int i = 0; i < response.body().getGatewayNames().size(); i++) {
                            payoutMethodsList.add(response.body().getGatewayNames().get(i).getGatewayName());
                        }
                        //set up
                        setUpSpinner();
                    }
                }//success

            }//response

            @Override
            public void onFailure(Call<GatewayResponse> call, Throwable t) {

            }
        });

    }//end api

}//end class