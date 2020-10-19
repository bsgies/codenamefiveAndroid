package com.itridtechnologies.codenamefive.Models.PartnerVehicleModel;

import com.google.gson.annotations.SerializedName;

public class PartnerVehicle {
    @SerializedName("vehicle_type")
    private String vehicleType;

    @SerializedName("vehicle_reg")
    private String registrationNum;

    //getter

    public String getVehicleType() {
        return vehicleType;
    }

    public String getRegistrationNum() {
        return registrationNum;
    }
}//end class
