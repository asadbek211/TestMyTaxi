package com.bizmiz.testtopshiriq.ui.home

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bizmiz.testtopshiriq.R
import com.bizmiz.testtopshiriq.app.App
import com.bizmiz.testtopshiriq.databinding.FragmentHomeBinding
import com.bizmiz.testtopshiriq.receiver.GetLocationReceiver
import com.bizmiz.testtopshiriq.ui.MainActivity
import com.bizmiz.testtopshiriq.ui.home.tariff.TariffDialog
import com.bizmiz.testtopshiriq.util.UserLocationResource
import com.bizmiz.testtopshiriq.util.isUsingNightModeResources
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.scalebar.scalebar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class HomeFragment : Fragment(R.layout.fragment_home), View.OnClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var userLocationViewModel: UserLocationViewModel

    @Inject
    lateinit var getLocationReceiver: GetLocationReceiver

    private var isCameraPosition = true
    private var zoomCount = 16.5
    private var currentPosition: Point? = null
    private var geoJsonSource: GeoJsonSource? = null
    private var animator: ValueAnimator? = null
    private var symbolLayer: SymbolLayer? = null
    private lateinit var sensor: Sensor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
        userLocationViewModel.getUsersLocation()
        getUserLocationDb()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.decorView.windowInsetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            requireActivity().window.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.black)
        }
        binding.mapView.compass.enabled = false
        binding.mapView.scalebar.enabled = false
        binding.mapView.gestures.rotateEnabled = false
        styleAddSource()
        getLocationReceiver.getLocationListener { location ->
            currentPosition = Point.fromLngLat(location.longitude, location.latitude)
            if (animator != null && animator!!.isStarted) {
                currentPosition = animator!!.animatedValue as Point
                animator!!.cancel()
            }
            animator = ValueAnimator().apply {
                setObjectValues(
                    currentPosition,
                    Point.fromLngLat(location.longitude, location.latitude)
                )
                setEvaluator(pointEvaluator)
                addUpdateListener {
                    geoJsonSource?.feature(Feature.fromGeometry(it.animatedValue as Point))
                }
                duration = 2000
                start()
            }
            currentPosition = Point.fromLngLat(location.longitude, location.latitude)
            if (isCameraPosition) {
                isCameraPosition = false
                val cameraPosition = CameraOptions.Builder()
                    .center(
                        location.let { Point.fromLngLat(it.longitude, location.latitude) }
                    )
                    .zoom(zoomCount)
                    .build()
                binding.mapView.getMapboxMap().setCamera(cameraPosition)
            }

        }
        onClickInstallation()
        rotationChanged()
        if (isUsingNightModeResources(requireContext())) {
            binding.mapView.getMapboxMap().loadStyleUri(Style.DARK)
            binding.ivOrders.setBackgroundResource(R.drawable.shape_category_night)
            binding.ivFrieze.setBackgroundResource(R.drawable.shape_category_night)
            binding.ivTariff.setBackgroundResource(R.drawable.shape_category_night)
        } else {
            binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
            binding.ivOrders.setBackgroundResource(R.drawable.shape_category)
            binding.ivFrieze.setBackgroundResource(R.drawable.shape_category)
            binding.ivTariff.setBackgroundResource(R.drawable.shape_category)
        }

    }

    private fun styleAddSource() {
        val bitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                this.resources, R.drawable.car_icon
            ), 55, 95, true
        )
        geoJsonSource = GeoJsonSource.Builder(
            "source-id",
        ).build().feature(
            Feature.fromGeometry(
                currentPosition?.let {
                    Point.fromLngLat(
                        it.longitude(),
                        currentPosition!!.latitude()
                    )
                }
            )
        )
        symbolLayer = SymbolLayer("layer-id", "source-id").iconImage("marker_icon")
            .iconIgnorePlacement(true).iconAllowOverlap(true)
        binding.mapView.getMapboxMap().getStyle { style ->
            style.addImage(
                "marker_icon", bitmap
            )
            style.addSource(geoJsonSource!!)
            style.addLayer(
                symbolLayer!!
            )

        }
    }

    private fun rotationChanged(
    ) {
        val sensorManager =
            requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = try {
            sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        } catch (e: Exception) {
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }
        sensorManager.registerListener(object : SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            }

            override fun onSensorChanged(sensorEvent: SensorEvent?) {
                if (sensorEvent?.sensor?.type == Sensor.TYPE_ORIENTATION) {
                    symbolLayer?.iconRotate(sensorEvent.values[0].toDouble())
                }
            }
        }, sensor, SensorManager.SENSOR_DELAY_GAME)
    }

    private val pointEvaluator: TypeEvaluator<Point> =
        TypeEvaluator<Point> { fraction, startValue, endValue ->
            Point.fromLngLat(
                startValue.longitude()
                        + ((endValue.longitude() - startValue.longitude()) * fraction),
                startValue.latitude()
                        + (endValue.latitude() - startValue.latitude()) * fraction
            )
        }

    private fun getUserLocationDb() {
        CoroutineScope(Dispatchers.IO).launch {
            userLocationViewModel.getStateFlow().collect {
                when (it) {
                    is UserLocationResource.Loading -> {}
                    is UserLocationResource.Error -> {}
                    is UserLocationResource.Success -> {
                        Log.d("USER_LOCATION", "List: ${it.response}")
                    }
                }
            }
        }
    }

    private fun onClickInstallation() {
        binding.cardZoomOut.setOnClickListener(this)
        binding.cardLocation.setOnClickListener(this)
        binding.cardZoomIn.setOnClickListener(this)
        binding.cardTariff.setOnClickListener(this)
        binding.ivOrdersClick.setOnClickListener(this)
        binding.cardNavDraw.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cardLocation -> {
                val cameraPosition = CameraOptions.Builder()
                    .center(
                        currentPosition
                    )
                    .zoom(zoomCount)
                    .build()
                binding.mapView.getMapboxMap().setCamera(cameraPosition)
            }
            R.id.cardZoomIn -> {
                zoomCount += 1
                val cameraPosition = CameraOptions.Builder()
                    .center(
                        currentPosition
                    )
                    .zoom(zoomCount)
                    .build()
                binding.mapView.getMapboxMap().setCamera(cameraPosition)
            }
            R.id.cardZoomOut -> {
                if (zoomCount >= 1) {
                    zoomCount -= 1
                }
                val cameraPosition = CameraOptions.Builder()
                    .center(
                        currentPosition
                    )
                    .zoom(zoomCount)
                    .build()
                binding.mapView.getMapboxMap().setCamera(cameraPosition)
            }
            R.id.cardTariff -> {
                TariffDialog(this)
            }
            R.id.cardNavDraw -> {
                (activity as MainActivity).openDrawer()
            }
            R.id.ivOrdersClick -> {
                findNavController().navigate(R.id.nav_home_to_orders)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity().registerReceiver(
            getLocationReceiver,
            IntentFilter("ACTION_CURRENT_LOCATION")
        )
        isCameraPosition = true
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        requireActivity().unregisterReceiver(getLocationReceiver)
        super.onDestroy()
    }
}