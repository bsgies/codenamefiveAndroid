package com.itridtechnologies.codenamefive.Models;

import com.google.gson.annotations.SerializedName;

public class UpdateInfoResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String msg;

    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }
}
