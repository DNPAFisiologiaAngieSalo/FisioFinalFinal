package com.example.fisiofinalfinal.Modelo;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import com.example.fisiofinalfinal.R;

import java.util.HashMap;

public class AccelMagne implements SensorEventListener {
    private static final String TAG = "DetermineOrientationActivity";
    private static final int RATE = SensorManager.SENSOR_DELAY_NORMAL;
    private static final int TTS_STREAM = AudioManager.STREAM_NOTIFICATION;
    private static final String TTS_NOTIFICATION_PREFERENCES_KEY =
            "TTS_NOTIFICATION_PREFERENCES_KEY";
    private static final double GRAVITY_THRESHOLD =
            SensorManager.STANDARD_GRAVITY / 2;
    public SensorManager sensorManager;
    private float[] accelerationValues;
    private float[] magneticValues;
    public TextToSpeech tts;
    private boolean isFaceUp;
    public TextView orientationValue;
    private HashMap<String, String> ttsParams;
    public SharedPreferences preferences;
    private boolean ttsNotifications;
    private int selectedSensorId;

    //Geter y Seter del Sensor Manager
    public SensorManager getSensorManager() {

        return sensorManager;
    }

    public void setSensorManager(SensorManager sensorRecibido) {
        sensorManager = sensorRecibido;
    }

    //Seter para Preferences
    public void setPreferences(SharedPreferences preferenciaRecibida) {
        preferences = preferenciaRecibida;
    }
    //Geter y Seter de Tts
    public TextToSpeech getTts() {
        return tts;
    }

    public void setTts(TextToSpeech ttsRecibido) {
        tts = ttsRecibido;
    }

    //Metodo para llamar al acelerometro-magnetometro en otras clases
    public void LlamarAcelMag() {
        ttsParams = new HashMap<String, String>();
        ttsParams.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(TTS_STREAM));

    }

    //Metodo que comparte Preferences
    public SharedPreferences getPreferences() {

        return preferences;
    }
    //Se detecta si el sensorcambió
    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] rotationMatrix;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerationValues = event.values.clone();
                rotationMatrix = generateRotationMatrix();
                if (rotationMatrix != null) {
                    determineOrientation(rotationMatrix);
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticValues = event.values.clone();
                rotationMatrix = generateRotationMatrix();
                if (rotationMatrix != null) {
                    determineOrientation(rotationMatrix);
                }
                break;
        }
    }

    //Metodo que detecta si la presion cambió
    @SuppressLint("LongLogTag")
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, String.format("Accuracy for sensor %s = %d",
                sensor.getName(), accuracy));
    }

    //Metodo que genera la Matriz de rotación
    @SuppressLint("LongLogTag")
    public float[] generateRotationMatrix() {
        float[] rotationMatrix = null;
        if (accelerationValues != null && magneticValues != null) {
            rotationMatrix = new float[16];
            boolean rotationMatrixGenerated;
            rotationMatrixGenerated =
                    SensorManager.getRotationMatrix(rotationMatrix,
                            null,
                            accelerationValues,
                            magneticValues);
            if (!rotationMatrixGenerated) {
                rotationMatrix = null;
            }
        }
        return rotationMatrix;
    }
    //Metodo que detecta la orientación del celular
    public void determineOrientation(float[] rotationMatrix) {
        float[] orientationValues = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientationValues);
        double azimuth = Math.toDegrees(orientationValues[0]);
        double pitch = Math.toDegrees(orientationValues[1]);
        double roll = Math.toDegrees(orientationValues[2]);

        if (pitch <= 10) {
            if (Math.abs(roll) >= 170) {
                onFaceDown();
            } else if (Math.abs(roll) <= 10) {
                onFaceUp();
            }
        }
    }

    //Metodo que indica si el celular esta Boca arriba
    public void onFaceUp() {
        if (!isFaceUp) {
            orientationValue.setText(R.string.faceUpText);
            isFaceUp = true;
        }
    }
    //Metodo que indica si el celular esta Boca abajo
    public void onFaceDown() {
        if (isFaceUp) {
            orientationValue.setText(R.string.faceDownText);
            isFaceUp = false;
        }
    }

    //Usa los sensores de Acelerometro y Magenetometo
    public void updateSelectedSensor() {

        sensorManager.unregisterListener(this);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                RATE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                RATE);
    }

}