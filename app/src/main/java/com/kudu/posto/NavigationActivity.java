package com.kudu.posto;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.kudu.posto.beans.User;
import com.kudu.posto.beans.UserService;
import com.kudu.posto.conexao.ConectarBanco;
import com.kudu.posto.permissoes.Permissoes;
import com.kudu.posto.services.Autenticador;
import com.kudu.posto.ui.CadastroActivity;
import com.kudu.posto.ui.LoginActivity;
import com.kudu.posto.ui.MeusDadosActivity;
import com.kudu.posto.ui.PrecosActivity;
import com.kudu.posto.ui.TornarEvidenteActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationActivity extends AppCompatActivity {

    private TextView nomePosto, cnpjPosto;
    private AppBarConfiguration mAppBarConfiguration;
    private SharedPreferences settings;
    DrawerLayout drawer;
    NavigationView navigationView;
    private String token;
    private int REQUEST_CODE = 1;

    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences("pref", Context.MODE_PRIVATE);
        token = settings.getString("token", null);

        if(token != null){
            setContentView(R.layout.activity_navigation_login);
            navigationView = findViewById(R.id.nav_view);
            drawer = findViewById(R.id.drawer_layout);

            View header = navigationView.getHeaderView(0);
            nomePosto = header.findViewById(R.id.textViewNomePosto);
            cnpjPosto = header.findViewById(R.id.textViewCNPJPosto);

            pegarDadosPosto();
        }else{
            setContentView(R.layout.activity_navigation);
            navigationView = findViewById(R.id.nav_view);
            drawer = findViewById(R.id.drawer_layout);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;

                switch (item.getItemId()){
                    case R.id.nav_dados:
                        startActivity(new Intent(getApplication(), MeusDadosActivity.class),
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.transition.animation, R.transition.animation2).toBundle());
                        return true;
                    case R.id.nav_precos:
                        startActivity(new Intent(getApplication(), PrecosActivity.class),
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.transition.animation, R.transition.animation2).toBundle());
                        return true;
                    case R.id.nav_cadastrar:
                        intent = new Intent(getApplication(), CadastroActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_login:
                        intent = new Intent(getApplication(), LoginActivity.class);
                        startActivityForResult(intent, REQUEST_CODE );
                        return true;

                    case R.id.nav_sair:
                        sair();
                        return true;
                }
                return false;
            }
        });
        Permissoes.validarPermissoes(permissoes, this, 1);
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            //permission denied (negada)
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                //Alerta
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void sair(){
        Autenticador autenticador = new Autenticador(this);
        autenticador.logout();
        startActivity(new Intent(getApplication(), NavigationActivity.class));
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK){
                startActivity(new Intent(this, NavigationActivity.class));
                finish();
            }
        }
    }

    public void pegarDadosPosto(){
        ConectarBanco conectarBanco = new ConectarBanco(this);
        UserService userService = conectarBanco.RetroConfig().create(UserService.class);
        Call<User> user = userService.get("Bearer " + token);
        user.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.code() == 200){
                    nomePosto.setText(response.body().getNomeDoPosto());
                    cnpjPosto.setText("CNPJ: " +response.body().getPosto().getCnpj());
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }

        });
    }
}