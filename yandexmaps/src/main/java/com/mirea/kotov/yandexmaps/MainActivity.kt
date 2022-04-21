package com.mirea.kotov.yandexmaps

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider

class MainActivity : AppCompatActivity(), UserLocationObjectListener {
    private var work = false
    private val REQUEST_PERMISSIONS_CODE = 158

    private var userLocationLayer: UserLocationLayer? = null
    private var mapView: MapView? = null
    private val MAPKIT_API_KEY = "e6b9a93e-0b05-4258-b518-4b866a099933"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this)

        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        mapView!!.map.move(
            CameraPosition(Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f),
            null
        )

        checkOrRequestPermissions()
        loadUserLocationLayer()

    }

    private fun checkOrRequestPermissions(){
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

    override fun onStart(){
        super.onStart()

        mapView?.onStart()
        MapKitFactory.getInstance().onStart()

        mapView!!.map!!.mapObjects.add
    }

    override fun onStop() {
        super.onStop()

        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
    }

    private fun loadUserLocationLayer(){
        val mapKit: MapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(mapView!!.mapWindow)
        userLocationLayer!!.isVisible = true
        userLocationLayer!!.isHeadingEnabled = true
        userLocationLayer!!.setObjectListener(this)

    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        checkOrRequestPermissions()

        userLocationLayer!!.setAnchor(
            PointF(mapView!!.width * 0.5f, mapView!!.height * 0.5f),
            PointF(mapView!!.width * 0.5f, mapView!!.height * 0.5f)
        )

        userLocationView.arrow.setIcon(ImageProvider.fromResource(
            this, R.drawable.star_big_on))
        userLocationView.pin.setIcon(ImageProvider.fromResource(
            this, R.drawable.ic_launcher_foreground))

        userLocationView.accuracyCircle.fillColor = Color.BLUE
    }

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }
}