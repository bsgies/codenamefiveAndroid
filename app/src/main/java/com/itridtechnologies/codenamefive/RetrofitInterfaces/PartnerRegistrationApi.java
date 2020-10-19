package com.itridtechnologies.codenamefive.RetrofitInterfaces;

import com.google.gson.JsonObject;
import com.itridtechnologies.codenamefive.Models.PartnerPayment.GatewayResponse;
import com.itridtechnologies.codenamefive.Models.PartnerVehicleModel.PartnerVehicleResponse;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.CityResponse;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.CountryResponse;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.DocumentModels.PojoImageResponse;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.EmailPassExistResponse;
import com.itridtechnologies.codenamefive.Models.RegistrationModels.StatesResponse;
import com.itridtechnologies.codenamefive.Models.UpdateInfoResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface PartnerRegistrationApi {

    @GET("country")
    Call<CountryResponse> getAllCountries();

    @GET("state/{id}")
    Call<StatesResponse> getAllStates(@Path("id") int stateId);

    @GET("city/{id}")
    Call<CityResponse> getAllCities(@Path("id") int cityId);

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    @POST("partner/exist")
    Call<EmailPassExistResponse> isExistingEmail(@Body JsonObject object);

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    @POST("partner/exist")
    Call<EmailPassExistResponse> isExistingPhone(@Body JsonObject object);

    //document uploading functions

    @Multipart
    @POST("partner/image")
    Call<PojoImageResponse> uploadDocument1(@Part MultipartBody.Part filePart);

    @Multipart
    @POST("partner/image")
    Call<PojoImageResponse> uploadDocument2(@Part MultipartBody.Part filePart);

    @Multipart
    @POST("partner/image")
    Call<PojoImageResponse> uploadDocument3(@Part MultipartBody.Part filePart);

    @Multipart
    @POST("partner/image")
    Call<PojoImageResponse> uploadProfilePhoto(@Part MultipartBody.Part filePart);

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    @POST("partner")
    Call<EmailPassExistResponse> registerPartner(@Body JsonObject object);
    //____________________________

    //update email
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    @PATCH("partner/email")
    Call<UpdateInfoResponse> updatePartnerEmail(@Header("x-access-token") String accessToken, @Body JsonObject object);

    //update phone
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    @PATCH("partner/phone")
    Call<UpdateInfoResponse> updatePartnerPhone(@Header("x-access-token") String accessToken, @Body JsonObject object);

    //partner next to kin
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    @PATCH("partner/next-of-kin")
    Call<UpdateInfoResponse> updateNextToKin(@Header("x-access-token") String accessToken, @Body JsonObject object);

    @GET("partner/vehicle")
    Call<PartnerVehicleResponse> getVehicleInformation(@Header("x-access-token") String accessToken);

    //payment information.............................................
    @GET("gateway")
    Call<GatewayResponse> getPaymentGateways();
    //payment information.............................................
}//end interface
