package com.mirea.kotov.googlemaps

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mirea.kotov.googlemaps.databinding.ActivityMapsBinding
import java.security.Permission

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private val REQUEST_PERMISSIONS_CODE = 178
    private var work = false

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

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_CODE
        ) } else work = true

        if (work) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isZoomControlsEnabled = true
            mMap.isTrafficEnabled = true
            setUpMap()
        }
        mMap.setOnMapClickListener(this)
    }

    private fun setUpMap(){
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        val mirea = LatLng(55.670005, 37.479894)
        val cameraPosition = CameraPosition.Builder().target(mirea).zoom(12.0F).build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        mMap.addMarker(MarkerOptions().title("МИРЭА")
            .snippet("Крупнейший политехнический ВУЗ").position(mirea))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        work = (requestCode == REQUEST_PERMISSIONS_CODE &&
                grantResults == IntArray(grantResults.size) { PackageManager.PERMISSION_GRANTED })
    }

    override fun onMapClick(latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(12F).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        mMap.addMarker(MarkerOptions().title("Где я?")
            .snippet("Новое место").position(latLng))
    }


}