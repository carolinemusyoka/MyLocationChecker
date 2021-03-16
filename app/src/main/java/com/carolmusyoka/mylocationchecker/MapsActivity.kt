package com.carolmusyoka.mylocationchecker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var dbReference: DatabaseReference = database.getReference("test")
    private lateinit var find_location_btn: Button


        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

            find_location_btn = findViewById(R.id.btn_find_location)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
            dbReference = Firebase.database.reference
            dbReference.addValueEventListener(locationListener)
    }

    val locationListener = object : ValueEventListener {
        //     @SuppressLint("LongLogTag")
        override fun onDataChange(snapshot: DataSnapshot) {
            if(snapshot.exists()){
                val location = snapshot.child("test").getValue(LocationInfo::class.java)
                var locationLat = location?.latitude
                var locationLong = location?.longitude
                find_location_btn.setOnClickListener {


                    if (locationLat != null && locationLong!= null) {
                        val latLng = LatLng(locationLat, locationLong)
                        map.addMarker(MarkerOptions().position(latLng)
                                .title("The user is currently here"))
                        val update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)

                        map.moveCamera(update)
                    }
                    else {
                        // if location is null , log an error message
                        Log.e(TAG, "No location found")
                    }
                }

            }
        }

        override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Could not read from database", Toast.LENGTH_LONG).show()
        }

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

    }
    companion object {
        private const val TAG = "MapsActivity" // for debugging
    }


}