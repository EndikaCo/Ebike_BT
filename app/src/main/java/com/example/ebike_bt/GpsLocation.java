package com.example.ebike_bt;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Locale;

class GpsLocation extends Activity implements IBaseGpsListener {

    TextView tvKmh, tvKmhMax, tvAverage;
    int kmTotal, kmTrip, maxSpeed;
    ArrayList<Integer> valuesMedia = new ArrayList<>();

    CLocation lastLocation;
    float lastDistance = 0;


    GpsLocation(Activity activity) {
        tvKmh = activity.findViewById(R.id.id_text_speed);
        tvKmhMax = activity.findViewById(R.id.id_km_max);


    }

    private void updateSpeed(CLocation location) {

        float nCurrentSpeed = 0;

        if(location != null)
        {
            nCurrentSpeed = location.getSpeed();
        }

        //KMH VIEW
        int kmh = (int)nCurrentSpeed;
        tvKmh.setText(String.valueOf(kmh));

        //MAX SPEED
        if(kmh> maxSpeed){
            maxSpeed = kmh;
            tvKmhMax.setText(String.valueOf(maxSpeed));
        }

        //AVERAGE
        if(kmh>1){
            valuesMedia.add(kmh);
            int sizeV = valuesMedia.size();

            double last=0;
            for (int i = 0; i < sizeV; i++) {
                last = last + valuesMedia.get(i);
            }
            Double average = last / sizeV;

            tvAverage.setText(String.valueOf(average));

        }

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null)
        {
            CLocation myLocation = new CLocation(location);
            this.updateSpeed(myLocation);

            meters(myLocation);
        }
    }

    public void meters(CLocation location ) {

        if(lastLocation!=null &&  location != null) {

            float mDistance =location.distanceTo(lastLocation);
            mDistance = (mDistance + lastDistance)/1000; //change to km

            String distance = String.format(Locale.ENGLISH,"%.01f", mDistance);

            //tvTrip.setText(distance + "Km");

            lastLocation=location;
            lastDistance = mDistance;
        }
        if(lastLocation==null &&  location != null){
            lastLocation=location;
        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getApplicationContext(), "GPS: disabled" , Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(getApplicationContext(), "GPS: " + status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGpsStatusChanged(int event) {
        Toast.makeText(getApplicationContext(), "GPS: " + event, Toast.LENGTH_SHORT).show();
    }


}

class CLocation extends Location {

    public CLocation(Location location) {
        this(location, true);
    }

    public CLocation(Location location, boolean bUseMetricUnits) {
        super(location);
    }

    @Override
    public float distanceTo(Location dest) {
        return super.distanceTo(dest);
    }

    @Override
    public float getAccuracy() {
        return super.getAccuracy();
    }

    @Override
    public double getAltitude() {
        return super.getAltitude();
    }

    @Override
    public float getSpeed() {
        return super.getSpeed() * 3.6f;
    }

}



