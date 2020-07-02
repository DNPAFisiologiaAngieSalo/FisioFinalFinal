package com.example.fisiofinalfinal;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Accelerometro implements SensorEventListener {
    private static final String TAG= "Accelerometer";
    public SensorManager sensorManager;
    Sensor accelerometer;
    //Accelerometer normalization variables
    private float[] gravity;
    private float[] linear_acceleration;
    private String seMueve = "Se mueve";
    private String noSeMueve = "No se mueve";
    public String resultado = "";
    TextView textView;

    float anterior_X = 0.0f, anterior_Y = 0.0f, anterior_Z = 0.0f;
    float rango = 0.1f;
    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfaz_ejercicio);
        textView = (TextView) findViewById(R.id.textViewEjer);

    }*/


    public void LlamarAccelerometro() {
       // setContentView(R.layout.activity_interfaz_ejercicio);
        //----------Aceleración Lineal con Accelerometro------------

        Log.d(TAG,"onCreate: Initializing Sensor Services");

        accelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener((SensorEventListener) Accelerometro.this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG,"onCreate: Registered accelerometer listener");
        //--------------------------------------------------------------
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.8f;
        gravity=new float[3];
        linear_acceleration=new float[3];
        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = Math.abs(event.values[0] - gravity[0]);
        linear_acceleration[1] = Math.abs(event.values[1] - gravity[1]);
        linear_acceleration[2] = Math.abs(event.values[2] - gravity[2]);

        Log.d(TAG,""+ linear_acceleration[0]+ " " + linear_acceleration[1] + " "+linear_acceleration[2] );

        if(anterior_X < linear_acceleration[0]+rango && anterior_X > linear_acceleration[0] - rango &&
                anterior_Y < linear_acceleration[1]+rango && anterior_Y > linear_acceleration[1] - rango &&
                anterior_Z < linear_acceleration[2]+rango && anterior_Z > linear_acceleration[2] - rango )
            resultado = noSeMueve;
        else
            resultado = seMueve;
        //Toast.makeText(getApplicationContext(),resultado,Toast.LENGTH_SHORT).show();
        textView.setText(resultado);
        anterior_X = linear_acceleration[0];
        anterior_Y = linear_acceleration[1];
        anterior_Z = linear_acceleration[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
