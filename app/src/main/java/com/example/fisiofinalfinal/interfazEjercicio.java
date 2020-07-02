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

    private HashMap<String, String> ttsParams;
    private SharedPreferences preferences;
    private boolean ttsNotifications;
    private int selectedSensorId;

    private static final int TTS_STREAM = AudioManager.STREAM_NOTIFICATION;
    private static final String TTS_NOTIFICATION_PREFERENCES_KEY =
            "TTS_NOTIFICATION_PREFERENCES_KEY";
    private static final double GRAVITY_THRESHOLD =
            SensorManager.STANDARD_GRAVITY / 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfaz_ejercicio);
        miAcel= new Accelerometro();
        miAcel.sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        miAcel.textView=(TextView) findViewById(R.id.textViewEjer);
        miAcel.LlamarAccelerometro();

        //----------------------------------------------------------
        // Keep the screen on so that changes in orientation can be easily
// observed
        miAcelMag=new AccelMagne();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
// Set up stream to use for Text-To-Speech
        ttsParams = new HashMap<String, String>();
        ttsParams.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(TTS_STREAM));
// Set the volume control to use the same stream as TTS which allows
// the user to easily adjust the TTS volume
        this.setVolumeControlStream(TTS_STREAM);
// Get a reference to the sensor service

        miAcelMag.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
// Initialize references to the UI views that will be updated in the
// code
        miAcelMag.orientationValue = (TextView) findViewById(R.id.orientationValue);

// Retrieve stored preferences
        preferences = getPreferences(MODE_PRIVATE);
        ttsNotifications =
                preferences.getBoolean(TTS_NOTIFICATION_PREFERENCES_KEY, true);

        miAcelMag.LlamarAccelMagnetometro();
        //----------------------------------------------------------
    }

    public void regresarMenu(View view){
        Intent resgresar= new Intent(this,interfazPrincipal.class);
        startActivity(resgresar);
        finish();
    }

}
