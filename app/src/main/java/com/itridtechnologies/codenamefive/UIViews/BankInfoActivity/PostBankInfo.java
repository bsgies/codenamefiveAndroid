package com.itridtechnologies.codenamefive.UIViews.BankInfoActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itridtechnologies.codenamefive.Const.Constants;
import com.itridtechnologies.codenamefive.Models.UpdateInfoResponse;
import com.itridtechnologies.codenamefive.R;
import com.itridtechnologies.codenamefive.RetrofitInterfaces.PartnerRegistrationApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostBankInfo extends AppCompatActivity {

    //const
    private static final String TAG = "PostBankInfo";
    //ui views
    private TextInputLayout til1;
    private TextInputLayout til2;
    private TextInputLayout til3;
    private TextInputLayout til4;
    private TextInputLayout til5;
    //vars
    private ArrayList<String> gatewayLabels;
    private String partnerType;
    private int gatewayId;
    private int size;
    private String value1;
    private String value2;
    private PartnerRegistrationApi mRegistrationApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_bank_info);

        //find views
        til1 = findViewById(R.id.til_1);
        til2 = findViewById(R.id.til_2);
        til3 = findViewById(R.id.til_3);
        til4 = findViewById(R.id.til_4);
        til5 = findViewById(R.id.til_5);

        ImageView done = findViewById(R.id.img_done);

        //init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRegistrationApi = retrofit.create(PartnerRegistrationApi.class);

        Intent dataIntent = getIntent();
        //get array of labels
        gatewayLabels = dataIntent.getStringArrayListExtra("Labels");
        //type
        partnerType = dataIntent.getStringExtra("type");
        //id
        gatewayId = dataIntent.getIntExtra("gateway_id", 0);
        //size
        size = gatewayLabels.size();

        //listener
        done.setOnClickListener(v -> {
            if (validate(size)) {
                Toast.makeText(PostBankInfo.this, "Ready to post", Toast.LENGTH_SHORT).show();
                postPayout();
            }//valid
        });


    }//on create

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: visible..");

        updateUI(size);
    }

    private void updateUI(int size) {
        Log.d(TAG, "updateUI: updating...");
        if (size == 2) {
            //show only 2 views
            til1.setVisibility(View.VISIBLE);
            til2.setVisibility(View.VISIBLE);
            //set placeholders
            til1.setHint(gatewayLabels.get(0));
            til2.setHint(gatewayLabels.get(1));
        }//for 2 fields
    }

    private boolean validate(int size) {
        Log.d(TAG, "validate: validating...");
        if (size == 2) {
            if (til1.getEditText().getText().toString().trim().isEmpty()) {
                til1.setError("Field can't be empty");
                til1.setErrorIconDrawable(R.drawable.icon_error_til);
                return false;
            } else if (til2.getEditText().getText().toString().trim().isEmpty()) {
                til2.setError("Field can't be empty");
                til2.setErrorIconDrawable(R.drawable.icon_error_til);
                return false;
            } else {
                Log.d(TAG, "validate: validation success !");
                til1.setError(null);
                til1.setErrorIconDrawable(null);
                til2.setError(null);
                til2.setErrorIconDrawable(null);
                //save values
                value1 = til1.getEditText().getText().toString().trim();
                value2 = til2.getEditText().getText().toString().trim();
                Log.d(TAG, "validate: value1: "+value1 + "value2: " + value2);
            }
        }//fro 2 inputs
        return false;
    }

    private void postPayout () {
        Log.d(TAG, "postPayout: posting..");

        JsonArray payoutDetail = new JsonArray();
        JsonObject obj = new JsonObject();
        JsonObject wrapperObj = new JsonObject();

        //add type & id
        wrapperObj.addProperty("type" , partnerType); //in wrapper
        wrapperObj.addProperty("gatewayId" , gatewayId); //in wrapper

        //set values in form of json obj
        obj.addProperty("key" , gatewayLabels.get(0));
        obj.addProperty("key", value1);

        obj.addProperty("key" , gatewayLabels.get(1));
        obj.addProperty("key", value2);

        //put in json ary
        payoutDetail.add(obj);

        wrapperObj.add("payoutDetail" , payoutDetail);//in wrapper

        Log.d(TAG, "postPayout: wrapper: " + wrapperObj.toString());

        Call<UpdateInfoResponse> call = mRegistrationApi.payouts(wrapperObj);

        call.enqueue(new Callback<UpdateInfoResponse>() {
            @Override
            public void onResponse(Call<UpdateInfoResponse> call, Response<UpdateInfoResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: success: " + response.body().isSuccess());
                }
            }

            @Override
            public void onFailure(Call<UpdateInfoResponse> call, Throwable t) {

            }
        });


    }
}//end class