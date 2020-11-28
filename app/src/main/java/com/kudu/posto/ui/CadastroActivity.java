package com.kudu.posto.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.kudu.posto.R;
import com.kudu.posto.beans.Posto;
import com.kudu.posto.beans.User;
import com.kudu.posto.beans.UserService;
import com.kudu.posto.conexao.ConectarBanco;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CadastroActivity extends AppCompatActivity {

    private TextView localMapa;
    private TextView setLtdLgn;
    private TextInputEditText textInputNomePosto, textInputCNPJ, textInputEmail, textInputSenha;
    private Double lat, lng;

    private Retrofit retrofit;

    final int REQUEST_CODE = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        getSupportActionBar().setTitle("Cadastro");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        localMapa = findViewById(R.id.selecionarLocalMapa);
        setLtdLgn = findViewById(R.id.setLtdLgn);

        textInputNomePosto = findViewById(R.id.textInputNomePosto);
        textInputCNPJ = findViewById(R.id.textInputCNPJ);
        textInputEmail = findViewById(R.id.textInputEmail);
        textInputSenha = findViewById(R.id.textInputSenha);
    }

    public void abrirCadastroMapa(View view){
        Intent intent = new Intent(this, MapaCadastroActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            this.lat = data.getDoubleExtra("lat", 0);
            this.lng = data.getDoubleExtra("lng", 0);
            setLtdLgn.setText("Endereco: " + data.getStringExtra("rua"));
        }
    }

    public void salvar(View view){
        ConectarBanco conectarBanco = new ConectarBanco(getApplicationContext());
        UserService userService = conectarBanco.RetroConfig().create(UserService.class);

        User user = new User();
        Posto posto = new Posto();

        posto.setNome(textInputNomePosto.getText().toString());
        user.setNomeDoPosto(textInputNomePosto.getText().toString());
        user.setEmail(textInputEmail.getText().toString());
        user.setSenha(textInputSenha.getText().toString());

        posto.setCnpj(textInputCNPJ.getText().toString());
        posto.setLtd(String.valueOf(lat));
        posto.setLgt(String.valueOf(lng));

        user.setPosto(posto);

        Call<User> userPost = userService.post(user);
        userPost.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("mensagem", response.raw().toString());
                finish();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}