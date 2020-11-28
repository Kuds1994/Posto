package com.kudu.posto.beans;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DeviceService {

    @POST("api/device/gcm/")
    Call<Device> post(@Body Device user);
}
