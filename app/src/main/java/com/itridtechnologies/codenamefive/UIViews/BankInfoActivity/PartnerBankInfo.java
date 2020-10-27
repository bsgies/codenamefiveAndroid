package com.itridtechnologies.codenamefive.UIViews.BankInfoActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itridtechnologies.codenamefive.Const.Constants;
import com.itridtechnologies.codenamefive.Models.PartnerPayment.GatewayData;
import com.itridtechnologies.codenamefive.Models.PartnerPayment.GatewayDataResponse;
import com.itridtechnologies.codenamefive.Models.PartnerPayment.GatewayResponse;
import com.itridtechnologies.codenamefive.R;
import com.itridtechnologies.codenamefive.RetrofitInterfaces.PartnerRegistrationApi;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PartnerBankInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    //const
    private static final String TAG = "PartnerBankInfo";
    //ui views
    private Spinner mSpinnerPayoutMethods;
    private ProgressBar mProgressBar;
    private TextView label1;
    private TextView label2;
    private TextView label3;
    private TextView label4;
    private TextView label5;
    private TextView placeholder1;
    private TextView placeholder2;
    private TextView placeholder3;
    private TextView placeholder4;
    private TextView placeholder5;
    private TextView errorPayouts;

    //vars
    private List<String> payoutMethodsList;
    private List<String> payoutMethodPartnerTypes;
    private PartnerRegistrationApi mRegistrationApi;
    private ArrayList<GatewayData> mGatewayDataList;
    private ArrayList<String> gatewayLabels;
    private String mPartnerType;
    private int gatewayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_bank_info);

        //find views
        mSpinnerPayoutMethods = findViewById(R.id.spinner_payouts);
        mProgressBar = findViewById(R.id.progressBar);
        errorPayouts = findViewById(R.id.tv_no_payout_method);
        //spinner listener
        mSpinnerPayoutMethods.setOnItemSelectedListener(this);
        payoutMethodPartnerTypes = new ArrayList<>();

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
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, payoutMethodsList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mSpinnerPayoutMethods.setAdapter(dataAdapter);

    }//end spinner

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected: selected option: " + parent.getItemAtPosition(position).toString());

        //get id of gateway
        gatewayId = (int) parent.getItemIdAtPosition(position);
        Log.d(TAG, "onItemSelected: gateway id: " + gatewayId);

        //api gateway data
        if (gatewayId != 0) {
            fetchGatewayData(gatewayId);
        }

    }//spinner item select listener

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void getGatewaysList() {
        mProgressBar.setVisibility(View.VISIBLE);

        Call<GatewayResponse> call = mRegistrationApi.getPaymentGateways();

        call.enqueue(new Callback<GatewayResponse>() {
            @Override
            public void onResponse(@NotNull Call<GatewayResponse> call, @NotNull Response<GatewayResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200 && response.body() != null) {
                        Log.d(TAG, "onResponse: success: " + response.body().isSuccess());
                        payoutMethodsList = new ArrayList<>();
                        payoutMethodPartnerTypes.clear();

                        //get list of gateways
                        payoutMethodsList.add("Select a payout method");

                        for (int i = 0; i < response.body().getGatewayNames().size(); i++) {
                            payoutMethodsList.add(response.body().getGatewayNames().get(i).getGatewayName());
                            payoutMethodPartnerTypes.add(response.body().getGatewayNames().get(i).getType());
                            Log.d(TAG, "onResponse: partner types: "+payoutMethodPartnerTypes.get(i));
                        }
                        //set up
                        setUpSpinner();
                        //hide loading
                        mProgressBar.setVisibility(View.GONE);
                    }
                }//success

            }//response

            @Override
            public void onFailure(@NotNull Call<GatewayResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                //hide loading
                mProgressBar.setVisibility(View.GONE);
            }
        });

    }//end api

    private void fetchGatewayData(int gatewayId) {
        Log.d(TAG, "fetchGatewayData: fetching gateway data...");
        mProgressBar.setVisibility(View.VISIBLE);

        Call<GatewayDataResponse> call = mRegistrationApi.getGatewayData(gatewayId);

        call.enqueue(new Callback<GatewayDataResponse>() {
            @Override
            public void onResponse(Call<GatewayDataResponse> call, Response<GatewayDataResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d(TAG, "onResponse: success: " + response.body().isSuccess());

                        //hide error tv
                        errorPayouts.setVisibility(View.GONE);

                        //init list
                        mGatewayDataList = new ArrayList<>();
                        //empty list, if previous entries exist
                        mGatewayDataList.clear();
                        //get data from array list
                        mGatewayDataList = response.body().getGatewayDataList();
                        Log.d(TAG, "onResponse: gateway data loaded, size: " + mGatewayDataList.size());

                        //update ui with data size
                        updateUI(mGatewayDataList.size());

                    }

                }//success

            }//response

            @Override
            public void onFailure(Call<GatewayDataResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                //hide loading
                mProgressBar.setVisibility(View.GONE);

                //show error tv
                errorPayouts.setVisibility(View.VISIBLE);

                //hide views
                hideViews();
            }
        });
    }//end api

    private void updateUI(int size) {
        Log.d(TAG, "updateUI: updating ui...");

        if (size == 2) {
            //find and declare 2 fields
            label1 = findViewById(R.id.tv_label1);
            label2 = findViewById(R.id.tv_label2);
            //placeholders
            placeholder1 = findViewById(R.id.edt_placeholder1);
            placeholder2 = findViewById(R.id.edt_placeholder2);

            //set data coming from gateway data
            label1.setText(mGatewayDataList.get(0).getLabel());
            placeholder1.setText(mGatewayDataList.get(0).getHint());

            label2.setText(mGatewayDataList.get(1).getLabel());
            placeholder2.setText(mGatewayDataList.get(1).getHint());

            //store labels to pass to other screen
            gatewayLabels = new ArrayList<>();
            for (int i = 0; i < mGatewayDataList.size(); i++) {
                gatewayLabels.add(mGatewayDataList.get(i).getLabel());
                Log.d(TAG, "updateUI: " + gatewayLabels.get(i));
            }

            //revive their visibility
            showViews(size);

            //hide loading
            mProgressBar.setVisibility(View.GONE);

        }// update data with 2 fields

        if (size == 5) {
            //find and declare 2 fields
            label1 = findViewById(R.id.tv_label1);
            label2 = findViewById(R.id.tv_label2);
            label3 = findViewById(R.id.tv_label3);
            label4 = findViewById(R.id.tv_label4);
            label5 = findViewById(R.id.tv_label5);
            //placeholders
            placeholder1 = findViewById(R.id.edt_placeholder1);
            placeholder2 = findViewById(R.id.edt_placeholder2);
            placeholder3 = findViewById(R.id.edt_placeholder3);
            placeholder4 = findViewById(R.id.edt_placeholder4);
            placeholder5 = findViewById(R.id.edt_placeholder5);

            //set data coming from gateway data
            label1.setText(mGatewayDataList.get(0).getLabel());
            placeholder1.setText(mGatewayDataList.get(0).getHint());

            label2.setText(mGatewayDataList.get(1).getLabel());
            placeholder2.setText(mGatewayDataList.get(1).getHint());

            label3.setText(mGatewayDataList.get(2).getLabel());
            placeholder3.setText(mGatewayDataList.get(2).getHint());

            label4.setText(mGatewayDataList.get(3).getLabel());
            placeholder4.setText(mGatewayDataList.get(3).getHint());

            label5.setText(mGatewayDataList.get(4).getLabel());
            placeholder5.setText(mGatewayDataList.get(4).getHint());

            //store labels to pass to other screen
            gatewayLabels = new ArrayList<>();
            for (int i = 0; i < mGatewayDataList.size(); i++) {
                gatewayLabels.add(mGatewayDataList.get(i).getLabel());
                Log.d(TAG, "updateUI: " + gatewayLabels.get(i));
            }

            //revive their visibility
            showViews(size);

            //hide loading
            mProgressBar.setVisibility(View.GONE);

        }// update data with 2 fields

    }//end UI

    private void showViews(int numOfViews) {
        Log.d(TAG, "showHideViews: called..");

        if (numOfViews == 2) {
            //show all 2 views
            label1.setVisibility(View.VISIBLE);
            label2.setVisibility(View.VISIBLE);
            placeholder1.setVisibility(View.VISIBLE);
            placeholder2.setVisibility(View.VISIBLE);

            //listeners
            placeholder1.setOnClickListener(this);
            placeholder2.setOnClickListener(this);

        }//for 2 views

        if (numOfViews == 5) {
            //show all 2 views
            label1.setVisibility(View.VISIBLE);
            label2.setVisibility(View.VISIBLE);
            label3.setVisibility(View.VISIBLE);
            label4.setVisibility(View.VISIBLE);
            label5.setVisibility(View.VISIBLE);
            placeholder1.setVisibility(View.VISIBLE);
            placeholder2.setVisibility(View.VISIBLE);
            placeholder3.setVisibility(View.VISIBLE);
            placeholder4.setVisibility(View.VISIBLE);
            placeholder5.setVisibility(View.VISIBLE);

            //listeners
            placeholder1.setOnClickListener(this);
            placeholder2.setOnClickListener(this);
            placeholder3.setOnClickListener(this);
            placeholder4.setOnClickListener(this);
            placeholder5.setOnClickListener(this);

        }//for 2 views

    }//end fun

    private void hideViews() {
        Log.d(TAG, "hideViews: called..");

        //find and declare all fields
        label1 = findViewById(R.id.tv_label1);
        label2 = findViewById(R.id.tv_label2);
        label3 = findViewById(R.id.tv_label3);
        label4 = findViewById(R.id.tv_label4);
        label5 = findViewById(R.id.tv_label5);
        //placeholders
        placeholder1 = findViewById(R.id.edt_placeholder1);
        placeholder2 = findViewById(R.id.edt_placeholder2);
        placeholder3 = findViewById(R.id.edt_placeholder3);
        placeholder4 = findViewById(R.id.edt_placeholder4);
        placeholder5 = findViewById(R.id.edt_placeholder5);

        //hide all views
        label1.setVisibility(View.GONE);
        label2.setVisibility(View.GONE);
        label3.setVisibility(View.GONE);
        label4.setVisibility(View.GONE);
        label5.setVisibility(View.GONE);
        placeholder1.setVisibility(View.GONE);
        placeholder2.setVisibility(View.GONE);
        placeholder3.setVisibility(View.GONE);
        placeholder4.setVisibility(View.GONE);
        placeholder5.setVisibility(View.GONE);

    }//end fun

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edt_placeholder1:
            case R.id.edt_placeholder2:
            case R.id.edt_placeholder3:
            case R.id.edt_placeholder4:
            case R.id.edt_placeholder5:
                Log.d(TAG, "onClick: clicked...");
                navToPaymentDetails();
                break;

            default:
                Log.d(TAG, "onClick: no such view !!");
                break;

        }
    }//on click

    private void navToPaymentDetails() {
        Intent paymentIntent = new Intent(PartnerBankInfo.this, PostBankInfo.class);
        //send labels array
        paymentIntent.putStringArrayListExtra("Labels", gatewayLabels);
        //get partner type against selected method
        mPartnerType = payoutMethodPartnerTypes.get(gatewayId - 1);
        paymentIntent.putExtra("type" , mPartnerType);
        //gateway id
        paymentIntent.putExtra("gateway_id" , gatewayId);

        startActivity(paymentIntent);
    }
}//end class