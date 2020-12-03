package com.itridtechnologies.codenamefive.Models.PartnerLoginModel;

import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("results")
	private Results results;

	@SerializedName("token")
	private String token;

	public Results getResults(){
		return results;
	}

	public String getToken(){
		return token;
	}
}