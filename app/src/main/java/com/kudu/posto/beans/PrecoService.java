package com.kudu.posto.beans;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PrecoService {

    @POST("api/posto/preco/create/")
    Call<Preco> post(@Body Preco preco, @Header("Authorization") String auth);

    @GET("api/posto/preco/me/")
    Call<Preco> get(@Header("Authorization") String auth);

}
