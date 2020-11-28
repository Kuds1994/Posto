package com.kudu.posto.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.kudu.posto.beans.Login;
import com.kudu.posto.beans.Token;
import com.kudu.posto.beans.TokenService;
import com.kudu.posto.conexao.ConectarBanco;
import com.kudu.posto.constantes.Constantes;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Autenticador {

    private Context context;
    private ConectarBanco conectarBanco;
    public Autenticador(Context context){
        this.context = context;
    }

    public boolean refreshToken(String token){
        conectarBanco = new ConectarBanco(context);
        TokenService loginService = conectarBanco.RetroConfig().create(TokenService.class);
        Call<Token> tokenCall = loginService.refresh_token(token, Constantes.CLIENT_ID, Constantes.REFRESH_TOKEN);
        try {
            Response<Token> response = tokenCall.execute();
            if(response.code() == 200){
                saveToken(response.body());
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void saveToken(Token token){
        SharedPreferences preferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        preferences.edit().putString("token", token.getAccess_token()).putString("refresh_token", token.getRefresh_token()).apply();
    }

    public void logout(){
        SharedPreferences preferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
    }
}
