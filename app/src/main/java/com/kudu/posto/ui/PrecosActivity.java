package com.kudu.posto.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.kudu.posto.R;
import com.kudu.posto.beans.Preco;
import com.kudu.posto.beans.PrecoService;
import com.kudu.posto.conexao.ConectarBanco;

import java.text.DecimalFormat;

import me.abhinay.input.CurrencyEditText;
import me.abhinay.input.CurrencySymbols;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrecosActivity extends AppCompatActivity {

    private CurrencyEditText campoGasolina, campoAditivada, campoAlcool, campoDiesel, campoGnv, campoEtanol;
    private TextInputLayout inputLayoutGasolina, inputLayoutAditivada, inputLayoutAlcool, inputLayoutDiesel, inputLayoutGnv, inputLayoutEtanol;
    private String token;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precos);

        settings = getSharedPreferences("pref", Context.MODE_PRIVATE);
        token = settings.getString("token", null);

        getSupportActionBar().setTitle("Meus Preços");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        campoGasolina = findViewById(R.id.campoGasolina);

        campoAditivada = findViewById(R.id.campoAditivada);
        campoAlcool = findViewById(R.id.campoAlcool);
        campoDiesel = findViewById(R.id.campoDiesel);
        campoGnv = findViewById(R.id.campoGnv);
        campoEtanol = findViewById(R.id.campoEtanol);

        inputLayoutGasolina = findViewById(R.id.textInputLayoutGasolina);
        inputLayoutAditivada = findViewById(R.id.textInputLayoutAditivada);
        inputLayoutDiesel = findViewById(R.id.textInputLayoutDiesel);
        inputLayoutAlcool = findViewById(R.id.textInputLayoutAlcool);
        inputLayoutGnv = findViewById(R.id.textInputLayoutGnv);
        inputLayoutEtanol = findViewById(R.id.textInputLayoutEtanol);

        buscarPrecos(token);
    }

    public void atualizarDados(View view){

        if((campoGasolina.getText().toString().length() > 0  && campoAditivada.getText().toString().length() > 0 &&
           campoAlcool.getText().toString().length() > 0 && campoDiesel.getText().toString().length() > 0 &&
           campoGnv.getText().toString().length() > 0) && campoEtanol.getText().toString().length() > 0 ) {
                ConectarBanco conectarBanco = new ConectarBanco(this);
                PrecoService precoService = conectarBanco.RetroConfig().create(PrecoService.class);

                Preco preco = new Preco();

                preco.setGasolina(campoGasolina.getCleanDoubleValue() / 100);
                preco.setAditivada(campoAditivada.getCleanDoubleValue() / 100);
                preco.setAlcool(campoAlcool.getCleanDoubleValue() / 100);
                preco.setDiesel(campoDiesel.getCleanDoubleValue() / 100);
                preco.setGnv(campoGnv.getCleanDoubleValue() / 100);
                preco.setEtanol(campoEtanol.getCleanDoubleValue() / 100);

                Call<Preco> user = precoService.post(preco, "Bearer " + token);
                user.enqueue(new Callback<Preco>() {
                    @Override
                    public void onResponse(Call<Preco> call, Response<Preco> response) {
                        if(response.code() == 200){
                            Toast.makeText(PrecosActivity.this, "Preços atualizados", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Preco> call, Throwable t) {
                        t.printStackTrace();
                    }

                });
        }else{
           Toast.makeText(PrecosActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        }
    }

    public void buscarPrecos(String token){
        ConectarBanco conectarBanco = new ConectarBanco(this);
        PrecoService precoService = conectarBanco.RetroConfig().create(PrecoService.class);

        Call<Preco> user = precoService.get("Bearer " + token);
        user.enqueue(new Callback<Preco>() {
            @Override
            public void onResponse(Call<Preco> call, Response<Preco> response) {
                if(response.code() == 200){

                    DecimalFormat df = new DecimalFormat("#,##.00");

                    campoGasolina.setText(df.format(response.body().getGasolina()));
                    campoAditivada.setText(df.format(response.body().getAditivada()));
                    campoAlcool.setText(df.format(response.body().getAlcool()));
                    campoDiesel.setText(df.format(response.body().getDiesel()));
                    campoGnv.setText(df.format(response.body().getGnv()));
                    campoEtanol.setText(df.format(response.body().getEtanol()));

                    desabilitar();
                }
            }
            @Override
            public void onFailure(Call<Preco> call, Throwable t) {

            }
        });
    }

    public void editar(View view){
        inputLayoutGasolina.setBoxStrokeWidth(1);
        inputLayoutAditivada.setBoxStrokeWidth(1);
        inputLayoutAlcool.setBoxStrokeWidth(1);
        inputLayoutDiesel.setBoxStrokeWidth(1);
        inputLayoutGnv.setBoxStrokeWidth(1);
        inputLayoutEtanol.setBoxStrokeWidth(1);

        campoGasolina.setEnabled(true);
        campoAditivada.setEnabled(true);
        campoAlcool.setEnabled(true);
        campoDiesel.setEnabled(true);
        campoGnv.setEnabled(true);
        campoEtanol.setEnabled(true);

        campoGasolina.setFocusable(true);
        campoGasolina.setFocusableInTouchMode(true);
        campoAditivada.setFocusable(true);
        campoAditivada.setFocusableInTouchMode(true);
        campoAlcool.setFocusable(true);
        campoAlcool.setFocusableInTouchMode(true);
        campoDiesel.setFocusable(true);
        campoDiesel.setFocusableInTouchMode(true);
        campoGnv.setFocusable(true);
        campoGnv.setFocusableInTouchMode(true);
        campoEtanol.setFocusable(true);
        campoEtanol.setFocusableInTouchMode(true);

    }

    public void desabilitar(){
        inputLayoutGasolina.setBoxStrokeWidth(0);
        inputLayoutAditivada.setBoxStrokeWidth(0);
        inputLayoutAlcool.setBoxStrokeWidth(0);
        inputLayoutDiesel.setBoxStrokeWidth(0);
        inputLayoutGnv.setBoxStrokeWidth(0);
        inputLayoutEtanol.setBoxStrokeWidth(0);

        campoGasolina.setEnabled(false);
        campoAditivada.setEnabled(false);
        campoAlcool.setEnabled(false);
        campoDiesel.setEnabled(false);
        campoGnv.setEnabled(false);
        campoEtanol.setEnabled(false);

        campoGasolina.setFocusable(false);
        campoAditivada.setFocusable(false);
        campoAlcool.setFocusable(false);
        campoDiesel.setFocusable(false);
        campoGnv.setFocusable(false);
        campoEtanol.setFocusable(false);
    }
}