package com.kudu.posto.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.kudu.posto.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapaCadastroActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localMapa;
    private TextView textViewLocal;
    private ProgressBar carregandoLocal;
    private String rua;
    private LatLng localUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_cadastro);

        textViewLocal = findViewById(R.id.textViewLocal);
        carregandoLocal = findViewById(R.id.carregandoLocal);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            mMap.setMyLocationEnabled(true);

            if(loc != null){
                localUsuario = new LatLng( loc.getLatitude(), loc.getLongitude());
                pegarLocalizacao(mMap, 14f);
            }
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            localUsuario = new LatLng(location.getLatitude(), location.getLongitude());
                            try {
                                geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                configuracaoCamera(googleMap, 14f);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(@NonNull String provider) {

                        }

                        @Override
                        public void onProviderDisabled(@NonNull String provider) {
                            if(!isFinishing()){
                                new AlertDialog.Builder(MapaCadastroActivity.this)
                                        .setTitle("GPS desligado")
                                        .setMessage("Para usar o app, ligue o GPS para acessar informações de localização")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                            }
                                        }).show().getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                            }
                        }
                    }
            );
        }

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng center = mMap.getCameraPosition().target;
                localMapa = new LatLng(center.latitude, center.longitude);
                try {
                    List<Address> enderecos = geocoder.getFromLocation(center.latitude, center.longitude, 1);
                    if(enderecos != null && enderecos.size()> 0 ){
                        Address endereco = enderecos.get(0);
                        carregandoLocal.setVisibility(View.INVISIBLE);
                        textViewLocal.setText(endereco.getThoroughfare());
                        rua = endereco.getThoroughfare();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                textViewLocal.setText("");
                carregandoLocal.setVisibility(View.VISIBLE);
            }
        });
    }

    public void selecionarMapa(View view){
        Log.d("center", "Latitude: " + localMapa.latitude + " Longitude: " + localMapa.longitude);
        Intent intent = new Intent();
        intent.putExtra("lat", localMapa.latitude);
        intent.putExtra("lng", localMapa.longitude);
        intent.putExtra("rua", rua);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void pegarLocalizacaoAtual(View view){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if(verificaGPS()){
                pegarLocalizacao(mMap, 14f);
            }else{
                new AlertDialog.Builder(this)
                        .setTitle("GPS desligado")
                        .setMessage("Para usar o app, ligue o GPS para acessar informações de localização")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        }).show().getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
            }
        }
    }

    public boolean verificaGPS() {
        boolean gps_enabled = false;
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception ex){}

        if(gps_enabled){
            return true;
        }
        return false;
    }

    public void configuracaoCamera(GoogleMap googleMap, float zoom) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(localUsuario)
                .zoom(zoom)
                .build();
        CameraUpdate c = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.animateCamera(c);
    }

    public void pegarLocalizacao(GoogleMap googleMap, float zoom){
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null){
            localUsuario = new LatLng(location.getLatitude(), location.getLongitude());
            configuracaoCamera(googleMap, zoom);
        }
    }

    public void sair (View view){
        finish();
    }
}
