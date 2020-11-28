package com.kudu.posto.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.kudu.posto.NavigationActivity;
import com.kudu.posto.R;
import com.kudu.posto.beans.Login;
import com.kudu.posto.beans.Token;
import com.kudu.posto.beans.TokenService;
import com.kudu.posto.conexao.ConectarBanco;
import com.kudu.posto.constantes.Constantes;
import com.kudu.posto.services.Autenticador;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText campoLogin, campoSenha;
    private TextView tv;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        campoLogin = findViewById(R.id.campoLogin);
        campoSenha = findViewById(R.id.campoSenha);
    }

    public void login(View view){
        Login loginBean = new Login();
        Autenticador autenticador = new Autenticador(this);
        loginBean.setEmail(campoLogin.getText().toString());
        loginBean.setSenha(campoSenha.getText().toString());

        ConectarBanco conectarBanco = new ConectarBanco(this);
        TokenService loginService = conectarBanco.RetroConfig().create(TokenService.class);
        Call<Token> tokenCall = loginService.login(loginBean.getEmail(), loginBean.getSenha(), Constantes.CLIENT_ID, Constantes.PASSWORD);
        tokenCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                    if(response.code() == 200){
                        autenticador.saveToken(response.body());
                        Intent intent = new Intent();
                        setResult(LoginActivity.RESULT_OK, intent);
                        finish();
                    }else{
                        Toast.makeText( LoginActivity.this, "Erro ao logar usuário", Toast.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {

            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent();
        setResult(LoginActivity.RESULT_CANCELED, intent);
        finish();

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            Intent intent = new Intent();
            setResult(LoginActivity.RESULT_CANCELED, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}