package com.kudu.posto.services;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAutenticador implements Authenticator {

    private Context context;
    public TokenAutenticador(Context context){
        this.context = context;
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
        Autenticador auth = new Autenticador(context);

        SharedPreferences settings = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        String refreshToken = settings.getString("refresh_token", null);

        if(refreshToken == null){
            return null;
        }

        if(auth.refreshToken(refreshToken)){
            String novo_token = settings.getString("token", null);
            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + novo_token)
                    .build();
        }

        return null;
    }
}
