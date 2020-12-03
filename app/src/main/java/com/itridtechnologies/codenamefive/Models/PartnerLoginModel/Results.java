package com.itridtechnologies.codenamefive.Models.PartnerLoginModel;

import com.google.gson.annotations.SerializedName;

public class Results{

	@SerializedName("profile_photo")
	private String profilePhoto;

	@SerializedName("auto_accept_status")
	private int autoAcceptStatus;

	@SerializedName("online_status")
	private int onlineStatus;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("phone_number")
	private String phoneNumber;

	@SerializedName("id")
	private int id;

	@SerializedName("mark_as_last_trip")
	private int markAsLastTrip;

	@SerializedName("first_name")
	private String firstName;

	@SerializedName("email")
	private String email;

	@SerializedName("status")
	private String status;

	public String getProfilePhoto(){
		return profilePhoto;
	}

	public int getAutoAcceptStatus(){
		return autoAcceptStatus;
	}

	public int getOnlineStatus(){
		return onlineStatus;
	}

	public String getLastName(){
		return lastName;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public int getId(){
		return id;
	}

	public int getMarkAsLastTrip(){
		return markAsLastTrip;
	}

	public String getFirstName(){
		return firstName;
	}

	public String getEmail(){
		return email;
	}

	public String getStatus(){
		return status;
	}
}