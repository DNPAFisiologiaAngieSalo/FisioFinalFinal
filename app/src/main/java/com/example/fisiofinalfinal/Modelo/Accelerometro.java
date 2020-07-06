package com.example.fisiofinalfinal.Modelo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

public class Accelerometro implements SensorEventListener {
    private static final String TAG = "Accelerometer";
    public SensorManager sensorManager;
    Sensor accelerometer;
    //Variables de normalización del acelerómetro
    private float[] gravity;
    private float[] linear_acceleration;
    private String seMueve = "Se mueve";
    private String noSeMueve = "No se mueve";
    public String resultado = "";
    public TextView textView;

    float anterior_X = 0.0f, anterior_Y = 0.0f, anterior_Z = 0.0f;
    float rango = 0.1f;

    //Geter para enviar un Sensor Manager a otra clase
    public SensorManager getSensorManager() {

        return sensorManager;
    }

    //Seter para guardar un Sensor Manager recibido de otra clase
    public void setSensorManager(SensorManager sensorRecibido) {

        sensorManager = sensorRecibido;
    }

    //Metodo para llamar al acelerometro en otras clases
    public void LlamarAccelerometro() {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener((SensorEventListener) Accelerometro.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //Se detecta el movmiento del celular
    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.8f;
        gravity = new float[3];
        linear_acceleration = new float[3];
        // Se aisla la fuerza de gravedad con el filtro de paso bajo.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Se elimina la contribución de la gravedad con el filtro de paso alto.
        linear_acceleration[0] = Math.abs(event.values[0] - gravity[0]);
        linear_acceleration[1] = Math.abs(event.values[1] - gravity[1]);
        linear_acceleration[2] = Math.abs(event.values[2] - gravity[2]);

        Log.d(TAG, "" + linear_acceleration[0] + " " + linear_acceleration[1] + " " + linear_acceleration[2]);

        if (anterior_X < linear_acceleration[0] + rango && anterior_X > linear_acceleration[0] - rango &&
                anterior_Y < linear_acceleration[1] + rango && anterior_Y > linear_acceleration[1] - rango &&
                anterior_Z < linear_acceleration[2] + rango && anterior_Z > linear_acceleration[2] - rango)
            resultado = noSeMueve;
        else
            resultado = seMueve;

        textView.setText(resultado);
        //Se guarda los valores de las coordenadas X,Y,Z de la posicion antigua.
        anterior_X = linear_acceleration[0];
        anterior_Y = linear_acceleration[1];
        anterior_Z = linear_acceleration[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
