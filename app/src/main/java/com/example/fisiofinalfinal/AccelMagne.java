package com.example.fisiofinalfinal;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import static android.content.Context.SENSOR_SERVICE;

public class AccelMagne implements SensorEventListener {
    private static final String TAG = "DetermineOrientationActivity";
    private static final int RATE = SensorManager.SENSOR_DELAY_NORMAL;
    Sensor accelMag;
    private float[] accelerationValues;
    private float[] magneticValues;
    private TextToSpeech tts;
    private boolean isFaceUp;
    TextView orientationValue;
    public SensorManager sensorManager;

    public void LlamarAccelMagnetometro() {


        // setContentView(R.layout.activity_interfaz_ejercicio);
        //----------Aceleración Lineal con Accelerometro------------

        accelMag=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener((SensorEventListener) AccelMagne.this,accelMag,SensorManager.SENSOR_DELAY_NORMAL);

        //--------------------------------------------------------------
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        float[] rotationMatrix;
        switch (event.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                accelerationValues = event.values.clone();
                rotationMatrix = generateRotationMatrix();
                if (rotationMatrix != null)
                {
                    determineOrientation(rotationMatrix);
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticValues = event.values.clone();
                rotationMatrix = generateRotationMatrix();
                if (rotationMatrix != null)
                {
                    determineOrientation(rotationMatrix);
                }
                break;
        }
    }
    @SuppressLint("LongLogTag")
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        Log.d(TAG,String.format("Accuracy for sensor %s = %d",
                sensor.getName(), accuracy));
    }
    @SuppressLint("LongLogTag")
    private float[] generateRotationMatrix()
    {
        float[] rotationMatrix = null;
        if (accelerationValues != null && magneticValues != null)
        {
            rotationMatrix = new float[16];
            boolean rotationMatrixGenerated;
            rotationMatrixGenerated =
                    SensorManager.getRotationMatrix(rotationMatrix,
                            null,
                            accelerationValues,
                            magneticValues);
            if (!rotationMatrixGenerated)
            {
                Log.w(TAG,"Error en la rotación");
                rotationMatrix = null;
            }
        }
        return rotationMatrix;
    }
    private void determineOrientation(float[] rotationMatrix)
    {
        float[] orientationValues = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientationValues);
        double azimuth = Math.toDegrees(orientationValues[0]);
        double pitch = Math.toDegrees(orientationValues[1]);
        double roll = Math.toDegrees(orientationValues[2]);

        if (pitch <= 10)
        {
            if (Math.abs(roll) >= 170)
            {
                onFaceDown();
            }
            else if (Math.abs(roll) <= 10)
            {
                onFaceUp();
            }
        }
    }
    private void onFaceUp()
    {
        if (!isFaceUp)
        {
            orientationValue.setText(R.string.faceUpText);
            isFaceUp = true;
        }
    }
    private void onFaceDown()
    {
        if (isFaceUp)
        {
            orientationValue.setText(R.string.faceDownText);
            isFaceUp = false;
        }
    }
    private void updateSelectedSensor()
    {
// Clear any current registrations
        sensorManager.unregisterListener(this);
// Determine which radio button is currently selected and enable the
// appropriate sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                RATE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                RATE);
    }


}
