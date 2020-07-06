package com.example.fisiofinalfinal.Vista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.fisiofinalfinal.R;

public class interfazVideo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfaz_video);
    }
    //boton para regresar a la pestaña principal
    public void regresarMenu(View view) {
        Intent resgresar = new Intent(this, interfazPrincipal.class);
        startActivity(resgresar);
        finish();
    }
}
