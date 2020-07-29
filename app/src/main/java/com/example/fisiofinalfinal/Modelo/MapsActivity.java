package com.example.fisiofinalfinal.Modelo;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.fisiofinalfinal.R;
import com.example.fisiofinalfinal.Vista.interfazPrincipal;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private Button salir;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtien el SupportMapFragment y recibe una notificación cuando el mapa esté listo para ser utilizado.
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        salirPestaña();
    }

    public void salirPestaña(){
        salir = findViewById(R.id.btnLogOutCourier);
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resgresar = new Intent(MapsActivity.this, interfazPrincipal.class);
                startActivity(resgresar);
                finish();
            }
        });


    }
    
    //Metodo del mapa a mostrar en el celular
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
                ,
                300,
                5, locationListener);
    }

    //Se detecta la ubicación del celular en el mapa
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marcador aqui"));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            saveLocation(location);



        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    protected void saveLocation(Location location){
        //Usamos GeoFire para almacenar esta ubicación
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("clientsLocation");
        GeoFire geoFire = new GeoFire(databaseReference);
        geoFire.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));

    }

    @Override
    protected void onStop() {
        super.onStop();
        //Cuando se sale de la activity, la ubicación registrada se retira de la database
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("clientsLocation");
        GeoFire geoFire = new GeoFire(databaseReference);
        geoFire.removeLocation(userID);
    }
}