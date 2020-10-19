package com.itridtechnologies.codenamefive.Models.PartnerPayment;

import com.google.gson.annotations.SerializedName;

public class Gateways {
    @SerializedName("id")
    private int gatewayId;

    @SerializedName("name")
    private String gatewayName;

    public int getGatewayId() {
        return gatewayId;
    }

    public String getGatewayName() {
        return gatewayName;
    }
}
