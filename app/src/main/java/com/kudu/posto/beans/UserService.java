package com.kudu.posto.beans;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface UserService {

    @POST("api/user/create/")
    Call<User> post(@Body User user);

    @GET("api/user/details/me/")
    Call<User> get(@Header("Authorization") String auth);

    @PATCH("api/user/changepassword")
    Call<Void> changepassword(@Header("Authorization") String auth, @Body AlterarSenha senha);

    @HTTP(method = "DELETE", path = "api/user/delete/", hasBody = true)
    Call<Void> delete(@Header("Authorization") String auth, @Body String senha);
}
