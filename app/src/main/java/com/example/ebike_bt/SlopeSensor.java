package com.example.ebike_bt;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class SlopeSensor extends Activity implements SensorEventListener {

    //sensor
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    // public TextView tvPitch;
    float[] mGravity;
    float[] mGeomagnetic;
    public TextView tvPitch;
    private ImageView biker;
    float pitch, tara = 0;

    ArrayList<Integer> valuesMedia = new ArrayList<>();

    SlopeSensor(Activity activity) {

        tvPitch = activity.findViewById(R.id.id_pitch);

        biker = activity.findViewById(R.id.id_slope);

        biker.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                tara = pitch;
                return true;
            }
        });

    }

    public void prueba(Context context) {

        // magnetic sensor

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        assert mSensorManager != null;
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                float rawPitch = orientation[1]; // orientation contains: azimut, pitch and roll
                pitch = (rawPitch * 100) - tara;

                if (pitch < -45) {
                    pitch = -45;
                } else if (pitch > 45) {
                    pitch = 45;
                }



                    String strPitch = String.format(Locale.ENGLISH, "%.0f", pitch);//un decimal maximo en voltaje
                    tvPitch.setText(String.format("%sº", strPitch));// convierte a string y pone V al final
                    biker.setRotation(pitch);

            }
        }
    }

        @Override
        public void onAccuracyChanged (Sensor sensor,int accuracy){

        }

    }
