package com.bizmiz.testtopshiriq.ui.home

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bizmiz.testtopshiriq.R
import com.bizmiz.testtopshiriq.app.App
import com.bizmiz.testtopshiriq.databinding.FragmentHomeBinding
import com.bizmiz.testtopshiriq.receiver.GetLocationReceiver
import com.bizmiz.testtopshiriq.receiver.RestartForegroundServiceReceiver
import com.bizmiz.testtopshiriq.service.MyForegroundService
import com.bizmiz.testtopshiriq.ui.MainActivity
import com.bizmiz.testtopshiriq.ui.home.tariff.TariffDialog
import com.bizmiz.testtopshiriq.util.UserLocationResource
import com.bizmiz.testtopshiriq.viewModel.UserLocationViewModel
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.plugin.compass.compass
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

    @Inject
    lateinit var restartForegroundServiceReceiver: RestartForegroundServiceReceiver
    private var isCameraPosition = true
    private var zoomCount = 16.5
    private var currentPosition: Point? = null
    private var geoJsonSource: GeoJsonSource? = null
    private var animator: ValueAnimator? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        App.appComponent.inject(this)
        userLocationViewModel.getUsersLocation()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.decorView.windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            requireActivity().window.statusBarColor =
                ContextCompat.getColor(requireActivity(), R.color.white)
        }
        binding.cardNavDraw.setOnClickListener {
            (activity as MainActivity).openDrawer()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.decorView.windowInsetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            requireActivity().window.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.black)
        }

        startService()
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
        binding.mapView.compass.enabled = false
        binding.mapView.scalebar.enabled = false
        binding.mapView.getMapboxMap().getStyle { style ->
            style.addImage(
                "marker_icon", bitmap
            )
            style.addSource(geoJsonSource!!)
            style.addLayer(
                SymbolLayer("layer-id", "source-id").iconImage("marker_icon")
                    .iconIgnorePlacement(true).iconAllowOverlap(true)
            )

        }
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
        getUserLocation()
        if (isUsingNightModeResources()) {
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

    private val pointEvaluator: TypeEvaluator<Point> =
        TypeEvaluator<Point> { fraction, startValue, endValue ->
            Point.fromLngLat(
                startValue.longitude()
                        + ((endValue.longitude() - startValue.longitude()) * fraction),
                startValue.latitude()
                        + (endValue.latitude() - startValue.latitude()) * fraction
            )
        }

    private fun getUserLocation() {
        CoroutineScope(Dispatchers.Main).launch {
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

    private fun isUsingNightModeResources(): Boolean {
        return when (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }

    private fun onClickInstallation() {
        binding.cardZoomOut.setOnClickListener(this)
        binding.cardLocation.setOnClickListener(this)
        binding.cardZoomIn.setOnClickListener(this)
        binding.cardTariff.setOnClickListener(this)
    }

    private fun startService() {
        val serviceIntent = Intent(requireActivity(), MyForegroundService::class.java)
        serviceIntent.putExtra("inputExtra", "Служба переднего плана запущена.")
        ContextCompat.startForegroundService(requireContext(), serviceIntent)
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
        }
    }

    override fun onStart() {
        super.onStart()
        IntentFilter("ACTION_CURRENT_LOCATION").also {
            requireActivity().registerReceiver(getLocationReceiver, it)
        }
        requireActivity().registerReceiver(
            restartForegroundServiceReceiver,
            IntentFilter("restartservice")
        )
        isCameraPosition = true
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(getLocationReceiver)
        requireActivity().unregisterReceiver(restartForegroundServiceReceiver)
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}