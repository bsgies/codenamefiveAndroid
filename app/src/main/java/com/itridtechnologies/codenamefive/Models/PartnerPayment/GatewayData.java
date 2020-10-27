package com.itridtechnologies.codenamefive.Models.PartnerPayment;

import com.google.gson.annotations.SerializedName;

public class GatewayData {
    @SerializedName("key")
    private String label;

    @SerializedName("placeholder")
    private String hint;

    public String getLabel() {
        return label;
    }

    public String getHint() {
        return hint;
    }
}
