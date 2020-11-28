package com.kudu.posto.ui.mapa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;
import com.kudu.posto.R;
import com.kudu.posto.beans.Posto;
import com.kudu.posto.beans.PostoService;
import com.kudu.posto.conexao.ConectarBanco;
import com.kudu.posto.ui.posto.PostoFragment;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Mapa extends Fragment {

    public static final String ARG_OBJECT = "object";

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LatLng localUsuario;
    private LatLng localPosto;
    private View viewBottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(final GoogleMap googleMap) {
            mMap = googleMap;
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            buscarPostos(googleMap, 0);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    localPosto = marker.getPosition();

                    viewBottomSheet = getActivity().findViewById(R.id.bottomSheet);
                    bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                    PostoFragment postoFragment = PostoFragment.newInstance((Integer) marker.getTag(), localUsuario.latitude, localUsuario.longitude, localPosto.latitude, localPosto.longitude);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.bottomSheet, postoFragment);

                    transaction.commit();

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(SphericalUtil.computeOffset(marker.getPosition(), 500, 180))
                            .zoom(15f)
                            .build();
                    CameraUpdate c = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    mMap.animateCamera(c);

                    return true;
                }
            });

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                mMap.setMyLocationEnabled(true);

                if(loc != null){
                    localUsuario = new LatLng( loc.getLatitude(), loc.getLongitude());
                    pegarLocalizacao(14f);
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
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {

                            }

                            @Override
                            public void onProviderEnabled(@NonNull String provider) {
                                if(localUsuario != null){
                                    configuracaoCamera(14f);
                                }
                            }

                            @Override
                            public void onProviderDisabled(@NonNull String provider) {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("GPS desligado")
                                        .setMessage("Para usar o app, ligue o GPS para acessar informações de localização")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                            }
                                }).show();
                            }
                        }
                );
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mapa, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = getActivity().findViewById(R.id.buttonLocalizacaoCadastro);
        FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.floatingActionButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificaGPS()){
                    pegarLocalizacao(14f);
                }else{
                    new AlertDialog.Builder(getActivity())
                            .setTitle("GPS desligado")
                            .setMessage("Para usar o app, ligue o GPS para acessar informações de localização")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            }).show();
                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view1 = getLayoutInflater().inflate(R.layout.buscar_postos, null);
                builder.setView(view1);

                builder.setPositiveButton(R.string.buscar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RadioGroup rg = view1.findViewById(R.id.radioBuscarPostos);
                        RadioButton rb = rg.findViewById(rg.getCheckedRadioButtonId());
                        int index = rg.indexOfChild(rb);
                        buscarPostos(mMap, index);
                    }
                });
                builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public void buscarPostos(GoogleMap googleMap, int i) {
        googleMap.clear();
        ConectarBanco conectarBanco = new ConectarBanco(getContext());
        PostoService postoService = conectarBanco.RetroConfig().create(PostoService.class);
        Call<List<Posto>> posto = postoService.getAll();
        posto.enqueue(new Callback<List<Posto>>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(Call<List<Posto>> call, Response<List<Posto>> response) {
                if(response.code() == 200){
                    View campo = getLayoutInflater().inflate(R.layout.marker_content, null);
                    TextView textView = campo.findViewById(R.id.textViewPreco);

                    IconGenerator iconGenerator = new IconGenerator(getContext());
                    iconGenerator.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.marker_posto_1));

                    List<Posto> postos = response.body();

                    switch (i){
                        case 0:
                            for (Posto p : postos) {
                                if(p.getPreco() != null){
                                    textView.setText(String.format(Locale.getDefault(),"R$%.2f", p.getPreco().getGasolina()));
                                }else{
                                    textView.setText("Não cadastrado");
                                }
                                iconGenerator.setContentView(campo);
                                Bitmap icon = iconGenerator.makeIcon();
                                LatLng location = new LatLng(Double.parseDouble(p.getLtd()), Double.parseDouble(p.getLgt()));
                                Marker marker = googleMap.addMarker(new MarkerOptions().position(location));
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                                marker.setTitle(p.getNome());
                                marker.setTag(p.getId());
                            }
                            break;
                        case 1:
                            for (Posto p : postos) {
                                if(p.getPreco() != null){
                                    textView.setText(String.format(Locale.getDefault(),"R$%.2f", p.getPreco().getAditivada()));
                                }else{
                                    textView.setText("Não cadastrado");
                                }
                                iconGenerator.setContentView(campo);
                                Bitmap icon = iconGenerator.makeIcon();
                                LatLng location = new LatLng(Double.parseDouble(p.getLtd()), Double.parseDouble(p.getLgt()));
                                Marker marker = googleMap.addMarker(new MarkerOptions().position(location));
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                                marker.setTitle(p.getNome());
                                marker.setTag(p.getId());
                            }
                            break;
                        case 2:
                            for (Posto p : postos) {
                                if(p.getPreco() != null){
                                    textView.setText(String.format(Locale.getDefault(),"R$%.2f", p.getPreco().getAlcool()));
                                }else{
                                    textView.setText("Não cadastrado");
                                }
                                iconGenerator.setContentView(campo);
                                Bitmap icon = iconGenerator.makeIcon();
                                LatLng location = new LatLng(Double.parseDouble(p.getLtd()), Double.parseDouble(p.getLgt()));
                                Marker marker = googleMap.addMarker(new MarkerOptions().position(location));
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                                marker.setTitle(p.getNome());
                                marker.setTag(p.getId());
                            }
                            break;
                        case 3:
                            for (Posto p : postos) {
                                if(p.getPreco() != null){
                                    textView.setText(String.format(Locale.getDefault(),"R$%.2f", p.getPreco().getDiesel()));
                                }else{
                                    textView.setText("Não cadastrado");
                                }
                                iconGenerator.setContentView(campo);
                                Bitmap icon = iconGenerator.makeIcon();
                                LatLng location = new LatLng(Double.parseDouble(p.getLtd()), Double.parseDouble(p.getLgt()));
                                Marker marker = googleMap.addMarker(new MarkerOptions().position(location));
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                                marker.setTitle(p.getNome());
                                marker.setTag(p.getId());
                            }
                            break;
                        case 4:
                            for (Posto p : postos) {
                                if(p.getPreco() != null){
                                    textView.setText(String.format(Locale.getDefault(),"R$%.2f", p.getPreco().getGnv()));
                                }else{
                                    textView.setText("Não cadastrado");
                                }
                                iconGenerator.setContentView(campo);
                                Bitmap icon = iconGenerator.makeIcon();
                                LatLng location = new LatLng(Double.parseDouble(p.getLtd()), Double.parseDouble(p.getLgt()));
                                Marker marker = googleMap.addMarker(new MarkerOptions().position(location));
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                                marker.setTitle(p.getNome());
                                marker.setTag(p.getId());
                            }
                            break;
                        case 5:
                            for (Posto p : postos) {
                                if(p.getPreco() != null){
                                    textView.setText(String.format(Locale.getDefault(),"R$%.2f", p.getPreco().getEtanol()));
                                }else{
                                    textView.setText("Não cadastrado");
                                }
                                iconGenerator.setContentView(campo);
                                Bitmap icon = iconGenerator.makeIcon();
                                LatLng location = new LatLng(Double.parseDouble(p.getLtd()), Double.parseDouble(p.getLgt()));
                                Marker marker = googleMap.addMarker(new MarkerOptions().position(location));
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                                marker.setTitle(p.getNome());
                                marker.setTag(p.getId());
                            }
                            break;
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Posto>> call, Throwable t) {
                t.printStackTrace();
            }
        });
        configuracaoCamera(13f);
    }

    public void configuracaoCamera(float zoom) {
        if(localUsuario != null){
            CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(localUsuario)
                .zoom(zoom)
                .build();
            CameraUpdate c = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mMap.animateCamera(c);
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

    public void pegarLocalizacao(float zoom){
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null){
            localUsuario = new LatLng(location.getLatitude(), location.getLongitude());
            configuracaoCamera(zoom);
        }
    }
}