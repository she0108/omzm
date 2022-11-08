package com.she.omealjomeal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.she.omealjomeal.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.CameraPosition

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
        mMap = googleMap

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        // Add a marker in Sydney and move the camera
        val yUn = LatLng(latitude, longitude)
        /* val markerOptions=MarkerOptions()
            .position(sydney)
            .title("Sydney")
         mMap.addMarker(markerOptions)
         */
        val cameraPosition =CameraPosition.Builder()
            .target(yUn)
            .zoom(15.0f)
            .build()

        val cameraUpdate=CameraUpdateFactory.newCameraPosition(cameraPosition)
        mMap.addMarker(MarkerOptions().position(yUn).title("Y University"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(yUn))
        mMap.moveCamera(cameraUpdate)

        val markerOptions =MarkerOptions()
            .position(yUn)
            .title("Y University")
            .snippet("37.383376,126.671190")
        mMap.addMarker(markerOptions)
    }
}