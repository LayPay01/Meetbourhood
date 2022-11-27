package com.example.meethbourhood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class PantallaCarga extends AppCompatActivity {

    TextView app_name, desarrolladortxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_carga);

        app_name = findViewById(R.id.app_name);
        desarrolladortxt = findViewById(R.id.desarrolladortxt);

        cambioletra();

        final int Duracion = 2500;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(PantallaCarga.this, Inicio.class);
                startActivity(i);
                finish();
            }
        },Duracion);
    }

    private void cambioletra(){
        /*FUENTE DE LETRA*/
        String ubicacion = "fuentes/sans_medio.ttf";
        Typeface Tf = Typeface.createFromAsset(PantallaCarga.this.getAssets(),ubicacion);
        /*FUENTE DE LETRA*/
        app_name.setTypeface(Tf);
        desarrolladortxt.setTypeface(Tf);
    }
}