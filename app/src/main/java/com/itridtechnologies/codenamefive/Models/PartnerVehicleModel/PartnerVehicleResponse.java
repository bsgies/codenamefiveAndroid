package com.itridtechnologies.codenamefive.Models.PartnerVehicleModel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PartnerVehicleResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private ArrayList<PartnerVehicle> vehicleList;

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<PartnerVehicle> getVehicleList() {
        return vehicleList;
    }
}//end class
