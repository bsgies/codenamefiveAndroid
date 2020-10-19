package com.itridtechnologies.codenamefive.Models.PartnerPayment;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GatewayResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private ArrayList<Gateways> gatewayNames;

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<Gateways> getGatewayNames() {
        return gatewayNames;
    }
}
