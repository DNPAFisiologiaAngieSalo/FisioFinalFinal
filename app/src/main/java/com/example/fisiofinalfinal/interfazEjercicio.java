package com.example.fisiofinalfinal;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashMap;

public class interfazEjercicio extends AppCompatActivity{

    Accelerometro miAcel;
    AccelMagne miAcelMag;

    private static final int TTS_STREAM = AudioManager.STREAM_NOTIFICATION;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfaz_ejercicio);

        acelerometro();
        magne_acel();

    }

    void acelerometro(){
        miAcel= new Accelerometro();
        miAcel.sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        miAcel.textView=(TextView) findViewById(R.id.textViewEjer);
        miAcel.LlamarAccelerometro();
    }

    void magne_acel(){
        miAcelMag = new AccelMagne();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setVolumeControlStream(TTS_STREAM);
        miAcelMag.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        miAcelMag.orientationValue = (TextView) findViewById(R.id.orientationValue);
        miAcelMag.preferences = getPreferences(MODE_PRIVATE);

        miAcelMag.crear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        miAcelMag.updateSelectedSensor();
    }

    @Override
    protected void onPause() {
        super.onPause();

        miAcelMag.sensorManager.unregisterListener(miAcelMag);

        if (miAcelMag.tts != null) {
            miAcelMag.tts.shutdown();
        }
    }
}
