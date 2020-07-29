package com.example.fisiofinalfinal.Vista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.fisiofinalfinal.Controlador.MainActivity;
import com.example.fisiofinalfinal.Modelo.MapsActivity;
import com.example.fisiofinalfinal.OpenCV.ReconocimientoActivity;
import com.example.fisiofinalfinal.R;
import com.google.firebase.auth.FirebaseAuth;

public class interfazPrincipal extends AppCompatActivity {

    Button irMapa;
    Button btnLogOut;
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
        //Button para cerrar sesión en Firebase
        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                //Volvemos a la interfaz de inicio
                Intent intent = new Intent(interfazPrincipal.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    //Boton para redirigir a la pesta de reprduccion de video
    public void PantallaVideo(View view) {
        Intent irVideo = new Intent(this, interfazVideo.class);
        startActivity(irVideo);
        finish();
    }
    // boton que redirige a la pantalla de reconocimiento de ejercicio
    public void PantallaEjercicio(View view) {
        Intent irEjercicio = new Intent(this, interfazEjercicio.class);
        startActivity(irEjercicio);
        finish();
    }
    //Boton para redirigir a la pantalla de mapa y ver ubicación
    public void PantallaMapa() {
        Intent irMapa = new Intent(this, MapsActivity.class);
        startActivity(irMapa);
        finish();
    }

    public void goReconocimientoActivity(View view){
        Intent intent = new Intent(this, ReconocimientoActivity.class);
        startActivity(intent);
    }


}
