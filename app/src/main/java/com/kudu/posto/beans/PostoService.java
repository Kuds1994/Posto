package com.kudu.posto.beans;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PostoService {

    @GET("api/posto/")
    Call<List<Posto>> getAll();

    @GET("api/posto/details/{id}/")
    Call<Posto> get(@Path("id") int id);

    @PATCH("api/posto/update/")
    Call<Posto> update(@Body String posto, @Header("Authorization") String auth);

}
