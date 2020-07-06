package com.example.fisiofinalfinal.Vista;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.fisiofinalfinal.Modelo.AccelMagne;
import com.example.fisiofinalfinal.Modelo.Accelerometro;
import com.example.fisiofinalfinal.R;

public class interfazEjercicio extends AppCompatActivity {

    Accelerometro miAcel;
    AccelMagne miAcelMag;
    private static final int TTS_STREAM = AudioManager.STREAM_NOTIFICATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfaz_ejercicio);
        //Se invoca al sensor de accelerometro para detecctar el movimiento
        acelerometro();
        //Se invoca al sensor de accelerometro-Magnetometro para detecctar el movimiento
        magne_acel();

    }
    //boton para regresar a la pestaña principal
    public void regresarMenu(View view) {
        Intent resgresar = new Intent(this, interfazPrincipal.class);
        startActivity(resgresar);
        finish();
    }
    //metodo que invoca al acelerometro para detectar movimiento del celular
    public void acelerometro() {
        miAcel = new Accelerometro();
        SensorManager sensorRecibido = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        miAcel.setSensorManager(sensorRecibido);
        miAcel.textView = (TextView) findViewById(R.id.textViewEjer);
        miAcel.LlamarAccelerometro();
    }
//metodo que invoca al acelerometro y al magnetometro para detectar la orientacion del celular (FaceUp,FaceDown)
    public void magne_acel() {
        miAcelMag = new AccelMagne();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setVolumeControlStream(TTS_STREAM);
        SensorManager sensorRecibido = (SensorManager) getSystemService(SENSOR_SERVICE);
        miAcelMag.setSensorManager(sensorRecibido);
        miAcelMag.orientationValue = (TextView) findViewById(R.id.orientationValue);
        miAcelMag.setPreferences(getPreferences(MODE_PRIVATE));
        miAcelMag.LlamarAcelMag();
    }
    //En onResume se actualiza contansemente los sensores
    @Override
    protected void onResume() {
        super.onResume();
        miAcelMag.updateSelectedSensor();
    }
    //en onPause se pausa los sensores
    @Override
    protected void onPause() {
        super.onPause();

        miAcelMag.getSensorManager().unregisterListener(miAcelMag);

        if (miAcelMag.getTts() != null) {
            miAcelMag.getTts().shutdown();
        }
    }
}
