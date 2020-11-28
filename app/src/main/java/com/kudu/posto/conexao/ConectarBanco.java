package com.kudu.posto.conexao;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.kudu.posto.services.TokenAutenticador;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ConectarBanco {

      private Context context;

      private SharedPreferences settings;
      public ConectarBanco(Context context){this.context = context;}

      public Retrofit RetroConfig(){
        HttpLoggingInterceptor i = new HttpLoggingInterceptor();
        Retrofit retrofit;

        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();

        okBuilder.addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                Request.Builder request = chain.request().newBuilder();

                return chain.proceed(request.build());
            }
        });

        i.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient cliente = okBuilder
                .authenticator(new TokenAutenticador(context))
                .addInterceptor(i)
                .build();

        // Computador 192.168.15.33 Celular 192.168.43.222

        retrofit = new Retrofit.Builder()
                .client(cliente)
                .baseUrl("http:/192.168.43.222:8000/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit;
    }
}


