package com.kudu.posto.ui.posto;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kudu.posto.R;
import com.kudu.posto.beans.Denuncia;
import com.kudu.posto.beans.DenunciaService;
import com.kudu.posto.beans.Posto;
import com.kudu.posto.beans.PostoService;
import com.kudu.posto.conexao.ConectarBanco;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";

    // TODO: Rename and change types of parameters
    private int mParam1;
    private double mParam2, mParam3, mParam4, mParam5;
    private TextView textPostoFragGas, textPostoFragGasA, textPostoFragAlc, textPostoFragDie, textPostoFragGnv, textPostoFragEta, textPostoFragNomePosto;
    private Button textViewcomoChegar;
    private ImageView imageViewDenuncia;
    private ViewGroup viewGroup;

    public PostoFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PostoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostoFragment newInstance(int param1, double usuLat, double usuLon, double posLat, double posLon) {
        PostoFragment fragment = new PostoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putDouble(ARG_PARAM2, usuLat);
        args.putDouble(ARG_PARAM3, usuLon);
        args.putDouble(ARG_PARAM4, posLat);
        args.putDouble(ARG_PARAM5, posLon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getDouble(ARG_PARAM2);
            mParam3 = getArguments().getDouble(ARG_PARAM3);
            mParam4 = getArguments().getDouble(ARG_PARAM4);
            mParam5 = getArguments().getDouble(ARG_PARAM5);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ConectarBanco conectarBanco = new ConectarBanco(getContext());
        PostoService postoService = conectarBanco.RetroConfig().create(PostoService.class);
        View view = inflater.inflate(R.layout.fragment_posto, container, false);

        ViewGroup include = view.findViewById(R.id.include_frame);
        View load = getLayoutInflater().inflate(R.layout.loading_fragment, include, false);

        include.addView(load);

        Call<Posto> callPosto = postoService.get(mParam1);
        callPosto.enqueue(new Callback<Posto>() {
            @Override
            public void onResponse(Call<Posto> call, Response<Posto> response) {
                if(response.code() == 200 && response.body().getPreco() != null){
                    include.removeAllViews();
                    View view = getLayoutInflater().inflate(R.layout.fragment_posto_container, include, false);
                    textPostoFragGas =  view.findViewById(R.id.textPostoFragGas);
                    textPostoFragGasA =  view.findViewById(R.id.textPostoFragGasA);
                    textPostoFragAlc =  view.findViewById(R.id.textPostoFragAlc);
                    textPostoFragEta =  view.findViewById(R.id.textPostoFragEta);
                    textPostoFragGnv =  view.findViewById(R.id.textPostoFragGnv);
                    textPostoFragDie =  view.findViewById(R.id.textPostoFragDie);

                    textPostoFragNomePosto = view.findViewById(R.id.textPostoFragNomePosto);
                    textViewcomoChegar = view.findViewById(R.id.buttonComoChegar);
                    imageViewDenuncia = view.findViewById(R.id.imageViewDenuncia);

                    textPostoFragGas.setText(String.format(Locale.getDefault(),"R$ %.2f", response.body().getPreco().getGasolina()));
                    textPostoFragGasA.setText(String.format(Locale.getDefault(),"R$ %.2f",response.body().getPreco().getAditivada()));
                    textPostoFragAlc.setText(String.format(Locale.getDefault(),"R$ %.2f",response.body().getPreco().getAlcool()));
                    textPostoFragEta.setText(String.format(Locale.getDefault(),"R$ %.2f",response.body().getPreco().getEtanol()));
                    textPostoFragGnv.setText(String.format(Locale.getDefault(),"R$ %.2f",response.body().getPreco().getGnv()));
                    textPostoFragDie.setText(String.format(Locale.getDefault(),"R$ %.2f",response.body().getPreco().getDiesel()));

                    textPostoFragNomePosto.setText(String.valueOf(response.body().getNome()));

                    textViewcomoChegar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = "https://www.google.com/maps/dir/?api=1&origin="+ mParam2 +","+mParam3+"&destination=" + mParam4 + "," + mParam5;
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                        }
                    });

                    imageViewDenuncia.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            View view = getLayoutInflater().inflate(R.layout.denuncia_postos, null);
                            builder.setView(view);

                            RadioButton rb1 = view.findViewById(R.id.radioButton5);
                            RadioButton rb2 = view.findViewById(R.id.radioButton4);
                            RadioButton rb3 = view.findViewById(R.id.radioButton6);
                            RadioButton rb4 = view.findViewById(R.id.radioButton7);

                            rb1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TextInputLayout inputEditText = view.findViewById(R.id.denuncia_texto_layout);
                                    inputEditText.setVisibility(View.GONE);
                                }
                            });

                            rb2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TextInputLayout inputEditText = view.findViewById(R.id.denuncia_texto_layout);
                                    inputEditText.setVisibility(View.GONE);
                                }
                            });

                            rb3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TextInputLayout inputEditText = view.findViewById(R.id.denuncia_texto_layout);
                                    inputEditText.setVisibility(View.GONE);
                                }
                            });

                            rb4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TextInputLayout inputEditText = view.findViewById(R.id.denuncia_texto_layout);
                                    inputEditText.setVisibility(View.VISIBLE);
                                }
                            });


                            builder.setPositiveButton(R.string.fazer_denuncia, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Denuncia denuncia = new Denuncia();
                                    RadioGroup rg = view.findViewById(R.id.radioDenunciaPostos);
                                    RadioButton rb = rg.findViewById(rg.getCheckedRadioButtonId());
                                    int index = rg.indexOfChild(rb);

                                    if(index == 3){
                                        TextInputEditText inputEditText = view.findViewById(R.id.denuncia_texto);
                                        denuncia.setMotivos(inputEditText.getText().toString());
                                        denuncia.setDenuncia(index + 1);
                                        denuncia.setPosto(mParam1);
                                    }else{
                                        denuncia.setMotivos("");
                                        denuncia.setDenuncia(index + 1);
                                        denuncia.setPosto(mParam1);
                                    }

                                    criarDenuncia(denuncia);
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

                    include.addView(view);
                }

            }

            @Override
            public void onFailure(Call<Posto> call, Throwable t) {

            }
        });

        return view;
    }
    public void criarDenuncia(Denuncia denuncia){
        ConectarBanco conectarBanco = new ConectarBanco(getContext());
        DenunciaService denunciaService = conectarBanco.RetroConfig().create(DenunciaService.class);

        Call<Denuncia> callDenuncia = denunciaService.post(denuncia);
        callDenuncia.enqueue(new Callback<Denuncia>() {
            @Override
            public void onResponse(Call<Denuncia> call, Response<Denuncia> response) {
                if(response.code() == 201){
                    Toast.makeText(getContext(), "Denuncia enviada", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Denuncia> call, Throwable t) {

            }
        });

    }

}