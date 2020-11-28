package com.kudu.posto.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kudu.posto.NavigationActivity;
import com.kudu.posto.R;
import com.kudu.posto.beans.AlterarSenha;
import com.kudu.posto.beans.Posto;
import com.kudu.posto.beans.PostoService;
import com.kudu.posto.beans.User;
import com.kudu.posto.beans.UserService;
import com.kudu.posto.conexao.ConectarBanco;
import com.kudu.posto.services.Autenticador;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeusDadosActivity extends AppCompatActivity {


    private TextView textCNPJ, textEmail, textPosto;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_dados);

        SharedPreferences settings = getSharedPreferences("pref", Context.MODE_PRIVATE);
        token = settings.getString("token", null);

        getSupportActionBar().setTitle("Meus Dados");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textPosto = findViewById(R.id.textViewNomePosto);
        textEmail = findViewById(R.id.textViewEmail);
        textCNPJ = findViewById(R.id.textViewCNPJ);

        pegarDadosPosto();
    }

    public void pegarDadosPosto(){
        ConectarBanco conectarBanco = new ConectarBanco(this);
        UserService userService = conectarBanco.RetroConfig().create(UserService.class);
        Call<User> user = userService.get("Bearer " + token);
        user.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.code() == 200){
                    textPosto.setText(response.body().getPosto().getNome());
                    textEmail.setText(response.body().getEmail());
                    textCNPJ.setText(response.body().getPosto().getCnpj());
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }

        });
    }

    public void editar(View view){
        AlertDialog.Builder buider = new AlertDialog.Builder(this);
        View view1 = getLayoutInflater().inflate(R.layout.alterar_nome, null);

        TextInputEditText nomePosto = view1.findViewById(R.id.textInputEditTextNomePosto);

        buider.setView(view1)
                .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Posto posto = new Posto();

                        posto.setNome(nomePosto.getText().toString());

                        ConectarBanco conectarBanco = new ConectarBanco(MeusDadosActivity.this);
                        PostoService userService = conectarBanco.RetroConfig().create(PostoService.class);
                        Call<Posto> postoCall = userService.update(nomePosto.getText().toString(), "Bearer " + token);

                        postoCall.enqueue(new Callback<Posto>() {
                            @Override
                            public void onResponse(Call<Posto> call, Response<Posto> response) {
                                if(response.code() == 200){
                                    Toast.makeText(MeusDadosActivity.this, "Campo alterado", Toast.LENGTH_SHORT).show();
                                    textPosto.setText(response.body().getNome());
                                }
                            }

                            @Override
                            public void onFailure(Call<Posto> call, Throwable t) {

                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = buider.create();
        dialog.show();
    }

    public void alterarSenha(View view){
        AlertDialog.Builder buider = new AlertDialog.Builder(this);
        View view1 = getLayoutInflater().inflate(R.layout.alterar_senha, null);

        TextInputEditText senhaAtual = view1.findViewById(R.id.textInputEditTextSenhaAtual);
        TextInputEditText senhaNova = view1.findViewById(R.id.textInputEditTextSenhaNova);
        TextInputEditText repitaSenhaNova = view1.findViewById(R.id.textInputEditTextSenhaNovaRepetir);


        buider.setView(view1)
                .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                            if(senhaNova.getText().toString().equals(repitaSenhaNova.getText().toString()) ){
                            AlterarSenha alterarSenha = new AlterarSenha();

                            alterarSenha.setSenha_atual(senhaAtual.getText().toString());
                            alterarSenha.setSenha_nova(senhaNova.getText().toString());

                            ConectarBanco conectarBanco = new ConectarBanco(MeusDadosActivity.this);
                            UserService userService = conectarBanco.RetroConfig().create(UserService.class);
                            Call<Void> userCall = userService.changepassword("Bearer " + token, alterarSenha);

                            userCall.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if(response.code() == 200){
                                        Toast.makeText(MeusDadosActivity.this, "Senha alterada", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(MeusDadosActivity.this, "Senha atual está incorreta", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {

                                }
                            });
                            }else{
                                Toast.makeText(MeusDadosActivity.this, "Senhas não conferem", Toast.LENGTH_SHORT).show();
                            }
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = buider.create();
        dialog.show();
    }

    public void excluirSenha(View view){
        AlertDialog.Builder buider = new AlertDialog.Builder(this);
        View view1 = getLayoutInflater().inflate(R.layout.excluir_conta, null);

        TextInputEditText senha = view1.findViewById(R.id.textInputEditTextSenha);

        buider.setView(view1)
                .setPositiveButton(R.string.excluir_conta, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(senha.getText().toString().length() > 2){

                            ConectarBanco conectarBanco = new ConectarBanco(MeusDadosActivity.this);
                            UserService userService = conectarBanco.RetroConfig().create(UserService.class);
                            Call<Void> userCall = userService.delete("Bearer " + token, senha.getText().toString());

                            userCall.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if(response.code() == 204){
                                        Autenticador autenticador = new Autenticador(MeusDadosActivity.this);
                                        autenticador.logout();
                                        startActivity(new Intent(getApplication(), NavigationActivity.class));
                                        finish();
                                    }else{
                                        Toast.makeText(MeusDadosActivity.this, "Senha está incorreta", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {

                                }
                            });
                        }else{
                            Toast.makeText(MeusDadosActivity.this, "Digite sua senha", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = buider.create();
        dialog.show();
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}