package com.kudu.posto.beans;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DenunciaService {

    @POST("api/denuncia/create/")
    Call<Denuncia> post(@Body Denuncia denuncia);

}
