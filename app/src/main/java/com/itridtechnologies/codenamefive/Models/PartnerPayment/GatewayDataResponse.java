package com.itridtechnologies.codenamefive.Models.PartnerPayment;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GatewayDataResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private ArrayList<GatewayData> gatewayDataList;

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<GatewayData> getGatewayDataList() {
        return gatewayDataList;
    }
}
