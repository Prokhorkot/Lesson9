package com.mirea.kotov.yandexdriver


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError


class MainActivity : AppCompatActivity(), DrivingSession.DrivingRouteListener {
    private val MAPKIT_API_KEY = "e6b9a93e-0b05-4258-b518-4b866a099933"
    private val ROUTE_START_LOCATION: Point = Point(55.670005, 37.479894)
    private val ROUTE_END_LOCATION: Point = Point(55.794229, 37.700772)
    private val SCREEN_CENTER: Point = Point(
        (ROUTE_START_LOCATION.latitude + ROUTE_END_LOCATION.latitude) / 2,
        (ROUTE_START_LOCATION.longitude + ROUTE_END_LOCATION.longitude) / 2
    )

    private var mapView: MapView? = null
    private var mapObjects: MapObjectCollection? = null
    private var drivingRouter: DrivingRouter? = null
    private var drivingSession: DrivingSession? = null
    private val colors = intArrayOf(-0x10000, -0xff0100, 0x00FFBBBB, -0xffff01)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        DirectionsFactory.initialize(this);

        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)

        mapView!!.map.move(
            CameraPosition(
                SCREEN_CENTER, 10f, 0f, 0f
            )
        )

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        mapObjects = mapView!!.map.mapObjects.addCollection()
        submitRequest()
    }

    override fun onDrivingRoutes(list: MutableList<DrivingRoute>) {
        var color: Int
        for (i in 0 until list.size) {
            // настроиваем цвета для каждого маршрута
            color = colors[i]
            // добавляем маршрут на карту
            mapObjects!!.addPolyline(list[i].geometry).strokeColor = color
        }
    }

    override fun onDrivingRoutesError(error: Error) {
        var errorMessage = "Unknown Error"
        if (error is RemoteError) {
            errorMessage = "Remote Error"
        } else if (error is NetworkError) {
            errorMessage = "Network Error"
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView!!.onStart()
    }

    override fun onStop() {
        mapView!!.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    private fun submitRequest(){
        val options = DrivingOptions()

        options.alternativeCount = 3
        val requestPoints: ArrayList<RequestPoint> = ArrayList()

        requestPoints.add(
            RequestPoint(
                ROUTE_START_LOCATION,
                RequestPointType.WAYPOINT,
                null
            )
        )
        requestPoints.add(
            RequestPoint(
                ROUTE_END_LOCATION,
                RequestPointType.WAYPOINT,
                null
            )
        )

        drivingSession = drivingRouter!!.requestRoutes(requestPoints, options, this)
    }
}