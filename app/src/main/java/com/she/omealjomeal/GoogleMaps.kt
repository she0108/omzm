package com.she.omealjomeal

import android.location.LocationRequest
import android.os.Bundle
import android.widget.Toast
import android.os.Looper
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.she.omealjomeal.databinding.ActivityGoogleMapsBinding
import java.util.jar.Manifest
import android.annotation.SuppressLint as SuppressLint1

class GoogleMaps : AppCompatActivity(), OnMapReadyCallback {

    //AIzaSyBLMinRgnSUoTkIwLSoJ0Q-ZWVDCdSsmEY

    lateinit var locationPermission: ActivityResultLauncher<Array<String>>
    private lateinit var mMap: GoogleMap


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    //이상

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGoogleMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationPermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()){results->
            if(results.all{it.value}){
                startProcess()
            }else{
                Toast.makeText(this
                , "권한 승인이 필요합니다"
                ,Toast.LENGTH_LONG).show()
            }
        }
        locationPermission.launch(
            arrayOf(
                Manifest.permission.ACCES_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
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

    locationPermmission.launch(
    arrayOf(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION
     )
    )

    fun startProcess(){
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        updateLocation()
    }

    @SuppressLint1("MissingPermission")
    fun updateLocation(){
        val locationRequest = LocationRequest.create()
        locationRequest.run{
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval=1000
        }

        locationCallback = object: LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?){
                locationResult?.let{
                    for((i,location) in it.locations.withInde()){
                        Log.d("Location", "$i ${location.latitude}, ${location.
                                "longtitude}")
                                setLastLocation(location)
                    }
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
        Looper.myLooper())
    }

    fun setLastLocation(lastLocation: Location) {
        val LATING = LatLng(lastLocation.latitude, lastLocation.longtitude)
        val marketOptions = MarkerOptions()
            .position(LATING)
            .title("Here!")
        val cameraPosition = CameraPosition.Builder()
            .target(LATING)
            .zoom(15.0f)
            .build()
        mMap.clear()
        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
}