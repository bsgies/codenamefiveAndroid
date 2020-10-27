package com.itridtechnologies.codenamefive.Models.PartnerPayment;

import com.google.gson.annotations.SerializedName;

public class Gateways {
    @SerializedName("id")
    private int gatewayId;

    @SerializedName("name")
    private String gatewayName;

    @SerializedName("partner_type")
    private String type;

    public int getGatewayId() {
        return gatewayId;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public String getType() {
        return type;
    }
}
