package com.example.fisiofinalfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class interfazPrincipal extends AppCompatActivity {

    Button irMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfaz_principal);
        irMapa = (Button) findViewById(R.id.button3);
        irMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PantallaMapa();
            }
        });

    }
    public void PantallaVideo(View view){
        Intent irVideo= new Intent(this,interfazVideo.class);
        startActivity(irVideo);
        finish();
    }

    public void PantallaEjercicio(View view){
        Intent irEjercicio= new Intent(this,interfazEjercicio.class);
        startActivity(irEjercicio);
        finish();
    }

    public void PantallaMapa(){
        Intent irMapa= new Intent(this,MapsActivity.class);
        startActivity(irMapa);
        finish();
    }

    public void SalirSesión(View view){
        Intent salir= new Intent(this,MainActivity.class);
        startActivity(salir);
        finish();
    }
}
