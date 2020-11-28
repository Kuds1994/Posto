package com.kudu.posto.beans;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TokenService {

    @POST("o/token/")
    @FormUrlEncoded
    Call<Token> login(@Field("username") String email,
                      @Field("password") String password,
                      @Field("client_id") String client_id,
                      @Field("grant_type") String grant_type
    );

    @POST("o/token/")
    @FormUrlEncoded
    Call<Token> refresh_token(@Field("refresh_token") String refresh_token,
                              @Field("client_id") String client_id,
                              @Field("grant_type") String grant_type
    );
}
