package com.xxx.service;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ChunqiuService {

	@FormUrlEncoded
	@POST("Flights/SearchByTime")
	Call<String> query(@Header("Referer") String referer, @Header("User-Agent") String userAgent,
			@Field("Departure") String departure, @Field("Arrival") String arrival, 
			@Field("DepartureDate") String departureDate, @Field("Currency") int currency, @Field("SType") int stype, 
			@Field("ReturnDate") String returnDate, @Field("IsIJFlight") String isIJFlight, @Field("IsBg") String isBg,
			@Field("IsEmployee") String isEmployee, @Field("IsLittleGroupFlight") String isLittleGroupFlight, 
			@Field("SeatsNum") int seatsNum, @Field("ActId") int actId, @Field("IfRet") String ifRet);
	
}
