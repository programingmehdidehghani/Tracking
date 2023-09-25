package com.example.mapproject.ui.monitor

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mapproject.BuildConfig
import com.example.mapproject.R
import com.example.mapproject.databinding.LayoutDrawerMonitorFragmentBinding
import com.example.mapproject.ui.devicesFragment.DevicesFragment
import com.example.mapproject.ui.devicesFragment.MyEventListener
import com.example.mapproject.ui.dialogs.DrawPolyGonCircleSaveDialog
import com.example.mapproject.ui.dialogs.MyDialogListener
import com.example.mapproject.ui.fragments.GroupFragment
import com.example.mapproject.ui.fragments.ShowFencesFragment
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Constants.Companion.setPersianNumbers
import com.example.mapproject.util.Resource
import com.example.mapproject.util.toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import dagger.hilt.android.AndroidEntryPoint
import ir.smartlab.persiandatepicker.util.PersianCalendar
import kotlinx.coroutines.*
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.sqrt


@Suppress("DEPRECATION", "UNREACHABLE_CODE")
@AndroidEntryPoint
class MonitorFragment : Fragment() , MyDialogListener , MyEventListener {

    private var _binding: LayoutDrawerMonitorFragmentBinding? = null
    private val viewBinding get() = _binding!!

    private var mLocationOverlay: MyLocationNewOverlay? = null
    private var mRotationGestureOverlay: RotationGestureOverlay? = null
    private var mScaleBarOverlay: ScaleBarOverlay? = null
    private var mMinimapOverlay: MinimapOverlay? = null
    private var mOverlay: ItemizedOverlayWithFocus<OverlayItem>? = null
    private lateinit var mapController: IMapController
    private val geoPointArrayList = ArrayList<GeoPoint>()
    private lateinit var geoPoint: GeoPoint
    private lateinit var polyline: Polyline
    private var polygon: Polygon? = null
    private var devicesMarker: Marker? = null
    private var drawPolygonMarker: Marker? = null
    private var drawPolygonFirstMarker: Marker? = null
    private val markerList = ArrayList<Marker>()
    private lateinit var monitorViewModel: MonitorViewModel
    var shapeList = ArrayList<String>()
    private var isBackFromDialog: Boolean? = false
    private var dialogDismissed = false
    var centerPoint: GeoPoint? = null
    var radius: Float? = null
    private lateinit var userName: String
    private val polygonListForDrawPolyline = ArrayList<Polygon>()
    private var polygonForShowAlphaOpacity = Polygon()


    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var userID: String
    private lateinit var token: String
    private lateinit var tokenID: String
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var circleOverlay: Overlay? = null
    private var circleDraw: Overlay? = null

    private var polygonHistory: ArrayList<Polygon> = arrayListOf()
    var circleHistory: ArrayList<Overlay> = arrayListOf()
    private var textOverlay: Overlay? = null
    private val polylineHistory = ArrayList<Polyline>()
    private var mReceive: MapEventsReceiver? = null
    private var overlayEvents: MapEventsOverlay? = null

    @SuppressLint("Range", "ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutDrawerMonitorFragmentBinding.inflate(inflater, container, false)
        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.onResume()

        return viewBinding.root
    }

    @SuppressLint("ClickableViewAccessibility", "DetachAndAttachSameFragment", "Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.drawerInMonitorFragment.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        if (dialogDismissed) {
            dialogDismissed = false
        }
        sharedPreferences.edit()
            .putBoolean(Constants.IS_GET_DEVICES, false)
            .apply()
        monitorViewModel = ViewModelProvider(requireActivity())[MonitorViewModel::class.java]
        userID = sharedPreferences.getString(Constants.KEY_USER_ID, "").toString()
        token = sharedPreferences.getString(Constants.KEY_TOKEN, "").toString()
        tokenID = sharedPreferences.getString(Constants.KEY_TOKEN_ID, "").toString()
        userName = sharedPreferences.getString(Constants.USERNAME, "").toString()
        val latitudeString = sharedPreferences.getString(Constants.LATITUDE, "")
        val longitudeString = sharedPreferences.getString(Constants.LONGITUDE, "")
        getDevicesFromServer("")
        if (latitudeString.isNullOrEmpty() && longitudeString.isNullOrEmpty()) {
            loadMap(0.0, 0.0)
        } else {
            latitude = latitudeString!!.toDouble()
            longitude = longitudeString!!.toDouble()
            loadMap(latitude!!, longitude!!)
        }

        changeMap()
        drawPolyLine()
        drawCircle()
        // showDetailItemsMap()
        checkListenerDrawer()
        getGeofenceHistory()
        reQuestPermission.launch(
            arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewBinding.drawerInMonitorFragment.isDrawerVisible(GravityCompat.START)) {
                        viewBinding.drawerInMonitorFragment.closeDrawer(GravityCompat.START)
                        return
                    } else {
                        findNavController().navigate(R.id.action_monitorFragment_to_serviceCarFragment)
                        return
                    }
                }
            })
        viewBinding.layoutViewInDrawerMonitorFragment.frZoomInInMonitorFragment.setOnClickListener {
            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.controller.zoomIn()
        }
        viewBinding.layoutViewInDrawerMonitorFragment.frZoomOutInMonitorFragment.setOnClickListener {
            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.controller.zoomOut()
        }
        viewBinding.layoutViewInDrawerMonitorFragment.btnDetailsItemMapInMonitorFragment.setOnClickListener {
            sharedPreferences.edit()
                .putBoolean(Constants.IS_GET_DEVICES, true)
                .apply()
            viewBinding.drawerInMonitorFragment.openDrawer(GravityCompat.START)

            viewBinding.layoutViewInDrawerMonitorFragment.frChangeMapInMonitorFragment.setBackgroundResource(
                R.drawable.click_cancel_change_view_item_map
            )
            viewBinding.layoutViewInDrawerMonitorFragment.btnChangeMapInMonitorFragment.setBackgroundResource(
                R.drawable.map_app_ux_icon
            )


            viewBinding.layoutViewInDrawerMonitorFragment.frAddCircleInMonitorFragment.setBackgroundResource(
                R.drawable.click_cancel_change_view_item_map
            )
            viewBinding.layoutViewInDrawerMonitorFragment.btnAddCircleGonInMainActivity.setBackgroundResource(
                R.drawable.circle_draw_color
            )

            viewBinding.layoutViewInDrawerMonitorFragment.frMenuMapInMonitorFragment.setBackgroundResource(
                R.drawable.click_cancel_change_view_item_map
            )
            viewBinding.layoutViewInDrawerMonitorFragment.btnDetailsItemMapInMonitorFragment.setBackgroundResource(
                R.drawable.menu_for_map
            )

            viewBinding.layoutViewInDrawerMonitorFragment.frGetFencesInMonitorFragment.setBackgroundResource(
                R.drawable.click_cancel_change_view_item_map
            )
            viewBinding.layoutViewInDrawerMonitorFragment.btnGetFencesGonInMonitorFragment.setBackgroundResource(
                R.drawable.map_navigation_pin_icon
            )

            viewBinding.layoutViewInDrawerMonitorFragment.frAddPolygonInMonitorFragment.setBackgroundResource(
                R.drawable.click_cancel_change_view_item_map
            )
            viewBinding.layoutViewInDrawerMonitorFragment.btnAddPolyGonInMonitorFragment.setBackgroundResource(
                R.drawable.draw_polygon_icon
            )

            switchStatusFragment()
        }


    }


    @SuppressLint("ResourceAsColor")
    private fun getGeofenceHistory() {
        viewBinding.layoutViewInDrawerMonitorFragment.btnGetFencesGonInMonitorFragment.setOnClickListener {
            viewBinding.layoutViewInDrawerMonitorFragment.frChangeMapInMonitorFragment.setBackgroundResource(R.drawable.click_cancel_change_view_item_map)
            viewBinding.layoutViewInDrawerMonitorFragment.btnChangeMapInMonitorFragment.setBackgroundResource(R.drawable.map_app_ux_icon)

            viewBinding.layoutViewInDrawerMonitorFragment.frAddPolygonInMonitorFragment.setBackgroundResource(R.drawable.click_cancel_change_view_item_map)
            viewBinding.layoutViewInDrawerMonitorFragment.btnAddPolyGonInMonitorFragment.setBackgroundResource(R.drawable.baseline_polyline_24)

            viewBinding.layoutViewInDrawerMonitorFragment.frAddCircleInMonitorFragment.setBackgroundResource(R.drawable.click_cancel_change_view_item_map)
            viewBinding.layoutViewInDrawerMonitorFragment.btnAddCircleGonInMainActivity.setBackgroundResource(R.drawable.circle_draw_color)

            viewBinding.layoutViewInDrawerMonitorFragment.frMenuMapInMonitorFragment.setBackgroundResource(R.drawable.click_cancel_change_view_item_map)
            viewBinding.layoutViewInDrawerMonitorFragment.btnDetailsItemMapInMonitorFragment.setBackgroundResource(R.drawable.menu_for_map)

            viewBinding.layoutViewInDrawerMonitorFragment.frGetFencesInMonitorFragment.setBackgroundResource(R.drawable.on_click_add_poly_gon_monitor)
            viewBinding.layoutViewInDrawerMonitorFragment.btnGetFencesGonInMonitorFragment.setBackgroundResource(R.drawable.map_navigation_pin_icon)

            viewBinding.layoutViewInDrawerMonitorFragment.frAddPolygonInMonitorFragment.setBackgroundResource(R.drawable.click_cancel_change_view_item_map)
            viewBinding.layoutViewInDrawerMonitorFragment.btnAddPolyGonInMonitorFragment.setBackgroundResource(R.drawable.draw_polygon_icon)


            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.remove(overlayEvents)
            val isPolygon = viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.contains(polygon)
            val isCircleDraw = viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.contains(circleOverlay)
            if (isPolygon || isCircleDraw){
                if (polygonHistory.size != 0){
                    viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                        polygonHistory
                    )
                }
                if (circleHistory.size != 0){
                    viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                        circleHistory
                    )
                }
                if (polylineHistory.size != 0){
                    viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                        polylineHistory
                    )
                    viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.remove(drawPolygonMarker)
                    viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.remove(drawPolygonFirstMarker)
                }
                polygonHistory.clear()
                circleHistory.clear()
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
            } else {
                if (polylineHistory.size != 0) {
                    viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                        polylineHistory
                    )
                    viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.remove(
                        drawPolygonMarker
                    )
                }
                if (polygonHistory.size != 0) {
                    viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                        polygonHistory
                    )
                }
                if (polygonListForDrawPolyline.size != 0) {
                    viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                        polygonListForDrawPolyline
                    )
                }
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
                monitorViewModel.getFences(
                    "Bearer $token", userID, "fa", "asia_tehran", "celsius", "km", "liter"
                )
                monitorViewModel.getFences.observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            hideProgress(viewBinding.layoutViewInDrawerMonitorFragment.progressInMonitorFragment)
                            response.data.let {
                                val shape: ArrayList<GeoPoint> = arrayListOf()
                                for (i in it.result) {
                                    if (i.type == "polygon") {
                                        for (j in i.shape) {
                                            geoPoint = GeoPoint(j.latitude, j.longitude)
                                            shape.add(geoPoint)
                                        }
                                        val colorCode = i.color
                                        val colorString = "#$colorCode"
                                        polygon = Polygon()
                                        polygon!!.points = shape
                                        polygon!!.strokeWidth = 4f
                                        polygon!!.strokeColor = Color.parseColor(colorString)
                                        polygon!!.fillColor = ColorUtils.setAlphaComponent(
                                            Color.parseColor(colorString), 0x40
                                        )
                                        polygonHistory.add(polygon!!)
                                        val middleLatitude =
                                            shape.map { it.latitude }.average()
                                        val middleLongitude =
                                            shape.map { it.longitude }.average()
                                        textOverlay = object : Overlay() {
                                            override fun draw(
                                                canvas: Canvas?,
                                                mapView: MapView?,
                                                shadow: Boolean
                                            ) {
                                                if (canvas != null && mapView != null) {
                                                    val paint = Paint()
                                                    paint.color = Color.parseColor(colorString)
                                                    paint.textSize = 34.0f
                                                    val text = i.name
                                                    val textPoint =
                                                        GeoPoint(middleLatitude, middleLongitude)
                                                    val textPixels =
                                                        mapView.projection.toPixels(textPoint, null)
                                                    canvas.drawText(
                                                        text,
                                                        textPixels.x.toFloat(),
                                                        textPixels.y.toFloat(),
                                                        paint
                                                    )
                                                }
                                            }
                                        }
                                        shape.clear()
                                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(
                                            textOverlay
                                        )
                                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(
                                            polygon
                                        )
                                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
                                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
                                        circleHistory.add(textOverlay as Overlay)
                                    } else {
                                        var fillColor: Int = 0
                                        val colorCode = i.color
                                        val colorString = "#$colorCode"
                                        fillColor = ColorUtils.setAlphaComponent(
                                            Color.parseColor("#$colorCode"),
                                            0x40
                                        )
                                       /* val paint = Paint()
                                        paint.style = Paint.Style.FILL_AND_STROKE
                                        paint.color = Color.parseColor(colorString)
                                        paint.strokeWidth = 4f
                                        paint.isAntiAlias = true
                                        paint.setARGB(
                                            Color.alpha(fillColor),
                                            Color.red(fillColor),
                                            Color.green(fillColor),
                                            Color.blue(fillColor)
                                        )*/
                                        val fillPaint = Paint()
                                        fillPaint.style = Paint.Style.FILL
                                        fillPaint.color = fillColor
                                        fillPaint.isAntiAlias = true

                                        val strokePaint = Paint()
                                        strokePaint.style = Paint.Style.STROKE
                                        strokePaint.color = Color.parseColor(colorString)
                                        strokePaint.strokeWidth = 4f
                                        strokePaint.isAntiAlias = true

                                        var centerPointDraw: GeoPoint? = null
                                        var radiusDraw: Float? = null
                                        var radiusServer = i.radius.toString()
                                        radiusDraw = radiusServer.toFloat()
                                        val centerSplit = i.center.toString().split(",")
                                        val pattern = Pattern.compile("[-+]?\\d*\\.?\\d+")
                                        val lat = pattern.matcher(centerSplit[0])
                                        val long = pattern.matcher(centerSplit[1])
                                        val latitudeDouble = if (lat.find()) {
                                            lat.group().toDoubleOrNull()
                                        } else {
                                            null
                                        }
                                        val longitudeDouble = if (long.find()) {
                                            long.group().toDoubleOrNull()
                                        } else {
                                            null
                                        }
                                        centerPointDraw = GeoPoint(
                                            latitudeDouble!!.toDouble(),
                                            longitudeDouble!!.toDouble()
                                        )
                                        circleOverlay = object : Overlay() {
                                            override fun draw(
                                                canvas: Canvas?,
                                                mapView: MapView?,
                                                shadow: Boolean
                                            ) {
                                                if (centerPointDraw != null && radiusDraw != null) {
                                                    val centerPointPixels =
                                                        mapView!!.projection.toPixels(
                                                            centerPointDraw,
                                                            null
                                                        )
                                                    val radiusPixels =
                                                        mapView.projection.metersToEquatorPixels(
                                                            radiusDraw!!
                                                        )

                                                    canvas?.drawCircle(
                                                        centerPointPixels.x.toFloat(),
                                                        centerPointPixels.y.toFloat(),
                                                        radiusPixels.toFloat(),
                                                        strokePaint
                                                    )
                                                }
                                            }
                                        }

                                        circleDraw = object : Overlay() {
                                            override fun draw(
                                                canvas: Canvas?,
                                                mapView: MapView?,
                                                shadow: Boolean
                                            ) {
                                                if (centerPointDraw != null && radiusDraw != null) {
                                                    val centerPointPixels =
                                                        mapView!!.projection.toPixels(
                                                            centerPointDraw,
                                                            null
                                                        )
                                                    val radiusPixels =
                                                        mapView.projection.metersToEquatorPixels(
                                                            radiusDraw!!
                                                        )

                                                    canvas?.drawCircle(
                                                        centerPointPixels.x.toFloat(),
                                                        centerPointPixels.y.toFloat(),
                                                        radiusPixels.toFloat(),
                                                        fillPaint
                                                    )
                                                }
                                            }
                                        }
                                        textOverlay = object : Overlay() {
                                            override fun draw(
                                                canvas: Canvas?,
                                                mapView: MapView?,
                                                shadow: Boolean
                                            ) {
                                                if (canvas != null && mapView != null) {
                                                    val paint = Paint()
                                                    paint.color = Color.parseColor("#$colorCode")
                                                    paint.textSize = 34.0f
                                                    val text = i.name
                                                    val textPoint =
                                                        GeoPoint(latitudeDouble, longitudeDouble)
                                                    val textPixels =
                                                        mapView.projection.toPixels(textPoint, null)
                                                    canvas.drawText(
                                                        text,
                                                        textPixels.x.toFloat(),
                                                        textPixels.y.toFloat(),
                                                        paint
                                                    )
                                                }
                                            }
                                        }
                                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(
                                            textOverlay
                                        )
                                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(
                                            circleOverlay
                                        )
                                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(
                                            circleDraw
                                        )
                                        circleHistory.add(circleOverlay as Overlay)
                                        circleHistory.add(circleDraw as Overlay)
                                        circleHistory.add(textOverlay as Overlay)
                                    }
                                }
                            }
                        }
                        is Resource.Error -> {
                            hideProgress(viewBinding.layoutViewInDrawerMonitorFragment.progressInMonitorFragment)
                        }
                        is Resource.Loading -> {
                            showProgress(viewBinding.layoutViewInDrawerMonitorFragment.progressInMonitorFragment)
                            toast(requireContext(),requireActivity().getString(R.string.loading))
                        }
                        else -> {}
                    }
                })
            }
            return@setOnClickListener
                }
    }




   private fun checkListenerDrawer(){
        viewBinding.drawerInMonitorFragment.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
               sharedPreferences.edit()
                    .putBoolean(Constants.IS_GET_DEVICES, true)
                    .apply()
                viewBinding.appBarHeader.txtUserNameInMenuMonitorFragment.text = userName
            }

            override fun onDrawerClosed(drawerView: View) {
                sharedPreferences.edit()
                    .putBoolean(Constants.IS_GET_DEVICES, false)
                    .apply()
                //getDevicesFromServer(deviceId)
                val latitudeString = sharedPreferences.getString(Constants.LATITUDE, "")
                val longitudeString = sharedPreferences.getString(Constants.LONGITUDE, "")
                if (latitudeString!!.isNotEmpty() && longitudeString!!.isNotEmpty()){
                    latitude = latitudeString!!.toDouble()
                    longitude = longitudeString!!.toDouble()
                    loadMap(latitude!!, longitude!!)
                }
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })
    }


    private fun switchStatusFragment(){
        val showFencesFragment = ShowFencesFragment()
        val groupFragment = GroupFragment()
        val fragmentManager = requireActivity().supportFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.fragment_container)

        if (fragment != null) {
            val transactionDevice = fragmentManager.beginTransaction()
            transactionDevice.remove(fragment)
            transactionDevice.commit()
        }
        val deviceFragmentNew = DevicesFragment()
        deviceFragmentNew.setListener(this@MonitorFragment)
        val transactionDevice = fragmentManager.beginTransaction()
        setTargetFragment(deviceFragmentNew,0)
        transactionDevice.add(R.id.fragment_container, deviceFragmentNew)
        transactionDevice.commit()
        viewBinding.btnDevicesFragment.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragment = fragmentManager.findFragmentById(R.id.fragment_container)
            if (fragment != null) {
                val transactionDevice = fragmentManager.beginTransaction()
                transactionDevice.remove(fragment)
                transactionDevice.commit()
            }
            val deviceFragmentNew = DevicesFragment()
            val transactionDevice = fragmentManager.beginTransaction()
            transactionDevice.add(R.id.fragment_container, deviceFragmentNew)
            transactionDevice.commit()
        }
        viewBinding.btnGroupInMonitorFragment.setOnClickListener {
            var fragmentManagerGroup = requireActivity().supportFragmentManager
            val transactionGroup = fragmentManagerGroup.beginTransaction()
            transactionGroup.replace(R.id.fragment_container, groupFragment)
            transactionGroup.commit()
        }
        viewBinding.btnFencesInMonitorFragment.setOnClickListener {
            var fragmentManagerFences = requireActivity().supportFragmentManager
            val transactionFences = fragmentManagerFences.beginTransaction()
            transactionFences.replace(R.id.fragment_container, showFencesFragment)
            transactionFences.commit()
        }
    }



    override fun onResume() {
        super.onResume()
        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.onPause()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun drawCircle() {
        val paint = Paint()
        paint.color = Color.BLUE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        val colorCode = Integer.toHexString(Color.BLUE)
        val fillColor = ColorUtils.setAlphaComponent(
            Color.parseColor("#$colorCode"),
            0x40
        )
        val fillPaint = Paint()
        fillPaint.style = Paint.Style.FILL
        fillPaint.color = ColorUtils.setAlphaComponent(ContextCompat.getColor(requireContext(), R.color.polyGon), 0x40)
        fillPaint.isAntiAlias = true

        viewBinding.layoutViewInDrawerMonitorFragment.btnAddCircleGonInMainActivity.setOnClickListener {
            radius = null
            centerPoint = GeoPoint(0.0, 0.0)
            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(
                overlayEvents
            )
            viewBinding.layoutViewInDrawerMonitorFragment.frChangeMapInMonitorFragment.setBackgroundResource(R.drawable.click_cancel_change_view_item_map)
            viewBinding.layoutViewInDrawerMonitorFragment.btnChangeMapInMonitorFragment.setBackgroundResource(R.drawable.map_app_ux_icon)


            viewBinding.layoutViewInDrawerMonitorFragment.frAddCircleInMonitorFragment.setBackgroundResource(R.drawable.on_click_add_poly_gon_monitor)
            viewBinding.layoutViewInDrawerMonitorFragment.btnAddCircleGonInMainActivity.setBackgroundResource(R.drawable.picture_draw_circle_map_click)

            viewBinding.layoutViewInDrawerMonitorFragment.frMenuMapInMonitorFragment.setBackgroundResource(R.drawable.click_cancel_change_view_item_map)
            viewBinding.layoutViewInDrawerMonitorFragment.btnDetailsItemMapInMonitorFragment.setBackgroundResource(R.drawable.menu_for_map)

            viewBinding.layoutViewInDrawerMonitorFragment.frGetFencesInMonitorFragment.setBackgroundResource(R.drawable.click_cancel_change_view_item_map)
            viewBinding.layoutViewInDrawerMonitorFragment.btnGetFencesGonInMonitorFragment.setBackgroundResource(R.drawable.map_navigation_pin_icon)

            viewBinding.layoutViewInDrawerMonitorFragment.frAddPolygonInMonitorFragment.setBackgroundResource(R.drawable.click_cancel_change_view_item_map)
            viewBinding.layoutViewInDrawerMonitorFragment.btnAddPolyGonInMonitorFragment.setBackgroundResource(R.drawable.draw_polygon_icon)

            if (polylineHistory.size != 0){
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                    polylineHistory
                )
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.remove(drawPolygonMarker)
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.remove(drawPolygonFirstMarker)
            }
            if (polygonHistory.size != 0){
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                    polygonHistory
                )
            }
            if (polygonListForDrawPolyline.size != 0){
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                    polygonListForDrawPolyline
                )
            }
            if (polygonHistory.size != 0){
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                    polygonHistory
                )
            }
            if (circleHistory.size != 0){
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                    circleHistory
                )
            }
            if (polylineHistory.size != 0){
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                    polylineHistory
                )
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.remove(drawPolygonMarker)
            }
            polygonHistory.clear()
            circleHistory.clear()
            geoPointArrayList.clear()
            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
            toast(requireContext(),requireActivity().getString(R.string.toast_circle))
            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.setOnTouchListener { _, event ->
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            if (!isBackFromDialog!!) {
                                val touchedPoint = viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.projection.fromPixels(
                                    event.x.toInt(),
                                    event.y.toInt()
                                )

                                centerPoint = touchedPoint as GeoPoint?

                                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
                            }
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (!isBackFromDialog!!) {
                                val touchedPoint = viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.projection.fromPixels(
                                    event.x.toInt(),
                                    event.y.toInt()
                                )
                                val distance = FloatArray(1)
                                Location.distanceBetween(
                                    centerPoint!!.latitude,
                                    centerPoint!!.longitude,
                                    touchedPoint.latitude,
                                    touchedPoint.longitude,
                                    distance
                                )
                                radius = distance[0]
                                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
                            }
                        }
                        MotionEvent.ACTION_UP -> {
                                 if (!isBackFromDialog!!) {
                                     Log.i("radius","$radius")
                                     if (radius != 0.0f && radius != null){
                                         val bundle = Bundle()
                                         bundle.putBoolean("isDrawPolygon",false)
                                         bundle.putFloat("radius", radius!!.toFloat())
                                         bundle.putDouble("latitude", centerPoint!!.latitude)
                                         bundle.putDouble("longitude", centerPoint!!.longitude)
                                         bundle.putDouble("zoomLevel", viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.zoomLevelDouble)
                                         viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
                                         viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.setOnTouchListener(null)
                                         val dialog = DrawPolyGonCircleSaveDialog()
                                         dialog.setTargetFragment(this@MonitorFragment, 1)
                                         dialog.arguments = bundle
                                         dialog.listener = this
                                         radius = null
                                         centerPoint = GeoPoint(0.0, 0.0)
                                         dialog.show(requireFragmentManager(), "")
                                         dialogDismissed = true
                                     }
                                 }
                        }
                    }
                    true
                }

            circleOverlay = object : Overlay() {
                    override fun draw(canvas: Canvas?, mapView: MapView?, shadow: Boolean) {
                        if (!isBackFromDialog!!) {
                            if (centerPoint != null && radius != null) {
                                val centerPointPixels = mapView!!.projection.toPixels(centerPoint, null)
                                val radiusPixels = mapView.projection.metersToEquatorPixels(radius!!)
                                canvas?.drawCircle(
                                    centerPointPixels.x.toFloat(),
                                    centerPointPixels.y.toFloat(),
                                    radiusPixels.toFloat(),
                                    paint
                                )
                            }
                        }
                    }
                }


            circleDraw = object : Overlay() {
                override fun draw(
                    canvas: Canvas?,
                    mapView: MapView?,
                    shadow: Boolean
                ) {
                    if (!isBackFromDialog!!) {
                        if (centerPoint != null && radius != null) {
                            val centerPointPixels = mapView!!.projection.toPixels(centerPoint, null)
                            val radiusPixels = mapView.projection.metersToEquatorPixels(radius!!)
                            canvas?.drawCircle(
                                centerPointPixels.x.toFloat(),
                                centerPointPixels.y.toFloat(),
                                radiusPixels.toFloat(),
                                fillPaint
                            )
                        }
                    }
                }
            }
            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(
                circleOverlay
            )
            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(
                circleDraw
            )
            circleHistory.add(circleOverlay as Overlay)
            circleHistory.add(circleDraw as Overlay)
            isBackFromDialog = false

        }

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    fun getDevicesFromServer(id:String) {
            monitorViewModel.getDevices(
                "Bearer $token", userID, "1", id, "fa", "asia_tehran", "celsius", "km", "liter"
            )
            monitorViewModel.getDevices.observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        Log.i("getDevicesServer","is devices ....")
                        hideProgress(viewBinding.layoutViewInDrawerMonitorFragment.progressInMonitorFragment)
                        response.data.let {
                            if (markerList.size != 0){
                                for (i in markerList){
                                    val markerExists = viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.contains(i)
                                    if (markerExists){
                                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.remove(i)
                                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
                                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
                                    }
                                }
                            }
                            for (i in it.result) {
                                for (j in i.devices) {
                                    if (j.data != null){
                                        devicesMarker = Marker(viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment)
                                        devicesMarker!!.position.latitude = j.data?.location?.latitude!!
                                        devicesMarker!!.position.longitude = j.data?.location?.longitude!!
                                        devicesMarker!!.icon =
                                            requireActivity().getDrawable(R.drawable.marker_orginal)
                                        devicesMarker!!.title = j.name
                                        devicesMarker!!.rotation = 360 - j.data.course!!.toFloat()
                                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(devicesMarker)

                                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
                                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()

                                        devicesMarker!!.setOnMarkerClickListener { marker, mapView ->
                                            val infoWindow = CustomInfoWindow(viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment,
                                                marker.position.latitude, marker.position.longitude, j.name!!,
                                                j.imei.toString(),
                                                j.data.speed!!, j.data.connected_at.toString(),
                                                j.data.fixed_at.toString(), j.data.connection_status.toString(),requireContext())


                                            val infoWindowView = infoWindow.view
                                            infoWindowView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                                            val infoWindowHeight = infoWindowView.measuredWidth

                                            val markerPoint = mapView.projection.toPixels(marker.position, null)
                                            val newMarkerPoint = Point(markerPoint.x, markerPoint.y - infoWindowHeight)
                                            val newLatLng = mapView.projection.fromPixels(newMarkerPoint.x, newMarkerPoint.y)
                                            marker.infoWindow = infoWindow

                                            mapView.postDelayed({
                                                val mapController = mapView.controller
                                                mapController.animateTo(newLatLng)
                                                mapController.zoomTo(16)
                                                marker.showInfoWindow()
                                            }, 400)
                                            return@setOnMarkerClickListener true
                                        }
                                        markerList.add(devicesMarker!!)
                                        mReceive = object : MapEventsReceiver {
                                            @SuppressLint(
                                                "ResourceAsColor", "UseCompatLoadingForDrawables",
                                                "ClickableViewAccessibility"
                                            )
                                            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                                                InfoWindow.closeAllInfoWindowsOn(viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment);
                                                return false
                                            }

                                            override fun longPressHelper(p: GeoPoint): Boolean {
                                                return false
                                            }
                                        }
                                        overlayEvents = MapEventsOverlay(requireActivity(), mReceive)
                                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(
                                            overlayEvents
                                        )

                                        Log.i("getDevices","$j.data")
                                       /* drawPopupWindowMarker(
                                            j.data?.location?.latitude, j.data.location.longitude, j.name!!,
                                            j.imei.toString(),
                                            j.data.speed!!, j.data.connected_at.toString(),
                                            j.data.fixed_at.toString(), j.data.connection_status.toString(),devicesMarker!!
                                        )*/
                                    } else {
                                        continue
                                    }

                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        hideProgress(viewBinding.layoutViewInDrawerMonitorFragment.progressInMonitorFragment)
                    }
                    is Resource.Loading -> {
                        showProgress(viewBinding.layoutViewInDrawerMonitorFragment.progressInMonitorFragment)
                    }
                    else -> {}
                }
            })
    }

   /* private fun drawPopupWindowMarker(
        lat: Double,
        long: Double,
        name: String,
        imei: String,
        speed: Int,
        connectAt: String,
        fixed: String,
        connectionStatus: String,
        marker: Marker
    ) {

        val markerView =
            LayoutInflater.from(requireContext()).inflate(R.layout.layout_second_popup_window, null)
        val popupWindow = PopupWindow(
            markerView,
            500 ,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
       *//* val customInfoWindow = CustomInfoWindow(
            R.layout.layout_second_popup_window,
            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment,
            name,
            imei,
            speed,
            connectAt,
            fixed,
            connectionStatus,
            lat,
            long,
            requireContext(),
            monitorViewModel
        )*//*


*//*
        marker!!.setOnMarkerClickListener { _, _ ->
           popupWindow.showAtLocation(viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment, Gravity.TOP, lat.toInt(), long.toInt())
             val txtNameDevice =
                 markerView.findViewById<TextView>(R.id.txt_device_name_in_popup_marker)
             val txtImeiDevice = markerView.findViewById<TextView>(R.id.txt_imei_in_popup_marker)
             val imageStatus = markerView.findViewById<ImageView>(R.id.iv_status_in_popup_marker)
             val txtStatusConnection =
                 markerView.findViewById<TextView>(R.id.txt_show_status_in_popup_marker)
             val txtSpeed =
                 markerView.findViewById<TextView>(R.id.txt_detail_speed_device_in_popup_marker)
             val txtLastDate =
                 markerView.findViewById<TextView>(R.id.txt_detail_date_device_in_popup_marker)
             val txtLastLocation =
                 markerView.findViewById<TextView>(R.id.txt_detail_last_location_device_in_popup_marker)
             val txtTypeAddress =
                 markerView.findViewById<TextView>(R.id.txt_address_location_device_in_popup_marker)
             val txtShowAddress =
                 markerView.findViewById<TextView>(R.id.txt_detail_address_location_device_in_popup_marker)

             txtNameDevice.text = name
             txtImeiDevice.text = imei
             val drawable = ContextCompat.getDrawable(
                 requireContext(),
                 R.drawable.icon_online_status_in_adapter
             )
             val drawableOf =
                 ContextCompat.getDrawable(requireContext(), R.drawable.icon_offline_status_in_adapter)
             val showSpeed: String = resources.getString(R.string.detail_speed, speed.toString())
             txtSpeed.text = showSpeed

             val config: android.content.res.Configuration? =
                 requireActivity()!!.resources.configuration

             if (config!!.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                 val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                 val inputCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                 inputCalendar.time = dateFormat.parse(connectAt!!)!!
                 val calendarFA = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"), Locale("fa", "IR"))
                 calendarFA.timeInMillis = inputCalendar.timeInMillis
                 val persianCalendar = PersianCalendar()
                 persianCalendar.timeInMillis = calendarFA.timeInMillis
                 val newTimeZone = TimeZone.getTimeZone("Asia/Tehran")
                 persianCalendar.timeZone = newTimeZone
                 val lastDate = setPersianNumbers(persianCalendar.persianShortDateTime)
                 txtLastDate.text = lastDate


                 val inputFormatLastLocation = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                 val inputDateLastLocation = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                 inputDateLastLocation.time = inputFormatLastLocation.parse(fixed!!)!!
                 val calendarFALastLocation  = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"), Locale("fa", "IR"))
                 calendarFALastLocation.timeInMillis = inputDateLastLocation.timeInMillis
                 val persianCalendarLast = PersianCalendar()
                 persianCalendarLast.timeInMillis = calendarFALastLocation.timeInMillis
                 val newTimeZoneLocation = TimeZone.getTimeZone("Asia/Tehran")
                 persianCalendarLast.timeZone = newTimeZoneLocation
                 val lastDateLastLocation = setPersianNumbers(persianCalendarLast.persianShortDateTime)
                 txtLastLocation.text = lastDateLastLocation

             } else {
                 txtLastDate.text = connectAt
                 txtLastLocation.text = fixed
             }

             if (connectionStatus == "online") {
                 if (speed != 0){
                     imageStatus.setImageDrawable(drawable)
                     txtStatusConnection.text = requireActivity().getText(R.string.status_device_moving)
                 } else {
                     imageStatus.setImageDrawable(drawable)
                     txtStatusConnection.text = requireActivity().getText(R.string.status_device_online)
                 }
             } else {
                 imageStatus.setImageDrawable(drawableOf)
                 txtStatusConnection.text = requireActivity().getText(R.string.status_device_offline)
             }

             val content = SpannableString(requireActivity().getText(R.string.address_location))
             content.setSpan(UnderlineSpan(), 0, content.length, 0)
             txtTypeAddress.text = content
             txtTypeAddress.setOnClickListener {
                 getLocationAddress(lat.toString(),long.toString(),txtShowAddress,txtTypeAddress)
             }
            true
        }
*//*
    }*/
    

    private fun setPersianNumbers(str: String): String? {
        return str
            .replace("0", "۰")
            .replace("1", "۱")
            .replace("2", "۲")
            .replace("3", "۳")
            .replace("4", "۴")
            .replace("5", "۵")
            .replace("6", "۶")
            .replace("7", "۷")
            .replace("8", "۸")
            .replace("9", "۹")
    }

    @SuppressLint("ResourceType", "ClickableViewAccessibility")
    private fun drawPolyLine() {
        viewBinding.layoutViewInDrawerMonitorFragment.btnAddPolyGonInMonitorFragment.setOnClickListener {
            polyline = Polyline()
            polyline.outlinePaint.strokeWidth = 8f
            polyline.outlinePaint.color = Color.BLUE
            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.setOnTouchListener(null)
            if (polygonHistory.size != 0) {
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                    polygonHistory
                )
                polygonHistory.clear()

            }
            if (circleHistory.size != 0) {
                for (i in circleHistory) {
                    viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.remove(
                        i
                    )
                }
            }
            if (polylineHistory.size != 0) {
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                    polylineHistory
                )
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.remove(
                    drawPolygonMarker
                )
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.remove(
                    drawPolygonFirstMarker
                )
                polylineHistory.clear()
                geoPointArrayList.clear()
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
            }
            if (polygonListForDrawPolyline.size != 0) {
                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                    polygonListForDrawPolyline
                )
                polygonListForDrawPolyline.clear()
            }
            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
            viewBinding.layoutViewInDrawerMonitorFragment.frChangeMapInMonitorFragment.setBackgroundResource(
                R.drawable.click_cancel_change_view_item_map
            )
            viewBinding.layoutViewInDrawerMonitorFragment.btnChangeMapInMonitorFragment.setBackgroundResource(
                R.drawable.map_app_ux_icon
            )


            viewBinding.layoutViewInDrawerMonitorFragment.frAddCircleInMonitorFragment.setBackgroundResource(
                R.drawable.click_cancel_change_view_item_map
            )
            viewBinding.layoutViewInDrawerMonitorFragment.btnAddCircleGonInMainActivity.setBackgroundResource(
                R.drawable.circle_draw_color
            )

            viewBinding.layoutViewInDrawerMonitorFragment.frMenuMapInMonitorFragment.setBackgroundResource(
                R.drawable.click_cancel_change_view_item_map
            )
            viewBinding.layoutViewInDrawerMonitorFragment.btnDetailsItemMapInMonitorFragment.setBackgroundResource(
                R.drawable.menu_for_map
            )

            viewBinding.layoutViewInDrawerMonitorFragment.frGetFencesInMonitorFragment.setBackgroundResource(
                R.drawable.click_cancel_change_view_item_map
            )
            viewBinding.layoutViewInDrawerMonitorFragment.btnGetFencesGonInMonitorFragment.setBackgroundResource(
                R.drawable.map_navigation_pin_icon
            )

            viewBinding.layoutViewInDrawerMonitorFragment.frAddPolygonInMonitorFragment.setBackgroundResource(
                R.drawable.on_click_add_poly_gon_monitor
            )
            viewBinding.layoutViewInDrawerMonitorFragment.btnAddPolyGonInMonitorFragment.setBackgroundResource(
                R.drawable.draw_polygon_icon
            )
            toast(requireContext(),requireActivity().getString(R.string.toast_polygon))
            mReceive = object : MapEventsReceiver {
                @SuppressLint(
                    "ResourceAsColor", "UseCompatLoadingForDrawables",
                    "ClickableViewAccessibility"
                )
                override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {

                    val isPolygon =
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.contains(
                            polygon
                        )
                    if (isPolygon) {
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.remove(
                            polygon
                        )
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
                    }
                    geoPoint = GeoPoint(p.latitude, p.longitude)
                    geoPointArrayList.add(geoPoint)
                    polyline.addPoint(geoPoint)
                    var firstLocation = geoPointArrayList[0]
                    val markerExists =
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.contains(
                            drawPolygonFirstMarker
                        )
                    if (markerExists) {
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.remove(
                            drawPolygonMarker
                        )
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.remove(
                            drawPolygonFirstMarker
                        )
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
                    }
                    drawPolygonFirstMarker =
                        Marker(viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment)
                    drawPolygonFirstMarker!!.icon =
                        requireActivity().getDrawable(R.drawable.baseline_circle_24)
                    drawPolygonFirstMarker!!.position = firstLocation
                    viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(
                        drawPolygonFirstMarker
                    )
                    drawPolygonMarker =
                        Marker(viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment)
                    drawPolygonMarker!!.icon =
                        requireActivity().getDrawable(R.drawable.baseline_circle_24)
                    drawPolygonMarker!!.position = geoPoint
                    viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(
                        drawPolygonMarker
                    )
                    viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
                    viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()

                    val distance = getPixelDistance(firstLocation, geoPoint,viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.zoomLevelDouble,viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment)
                    Log.i("map", "distance is .. $distance")
                    if (geoPointArrayList.size > 2) {
                        val markerExists =
                            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.contains(
                                polygon
                            )
                        if (markerExists) {
                            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                                polygonListForDrawPolyline
                            )
                            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
                            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
                        }
                        if (polygonListForDrawPolyline.size != 0) {
                            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                                polygonListForDrawPolyline
                            )
                            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
                            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
                        }
                        polygonForShowAlphaOpacity = Polygon()
                        polygonForShowAlphaOpacity!!.points = geoPointArrayList
                        polygonForShowAlphaOpacity!!.fillPaint.color = ColorUtils.setAlphaComponent(ContextCompat.getColor(requireContext(), R.color.polyGon), 0x40)
                        polygonForShowAlphaOpacity!!.strokeWidth = Color.TRANSPARENT.toFloat()
                        polygonListForDrawPolyline.add(polygonForShowAlphaOpacity!!)
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(
                            polygonForShowAlphaOpacity
                        )
                        drawPolygonMarker!!.setOnMarkerClickListener { _, _ ->
                            Log.i("send","is polygon distance")
                            if (geoPointArrayList.size >= 3) {
                                //firstLocation = geoPoint
                                geoPointArrayList.add(geoPoint)
                                drawPolygon(geoPointArrayList)
                            }
                            true
                        }
                        drawPolygonFirstMarker!!.setOnMarkerClickListener { _, _ ->
                            if (geoPointArrayList.size >= 3) {
                                //firstLocation = geoPoint
                                geoPointArrayList.add(geoPoint)
                                drawPolygon(geoPointArrayList)
                            }
                            true
                        }
                        polyline!!.setOnClickListener { polyline, mapView, eventPos ->
                            if (distance < 80 && geoPointArrayList.size > 3) {
                                //firstLocation = geoPoint
                                geoPointArrayList.add(GeoPoint(
                                    eventPos!!.latitude,
                                    eventPos.longitude
                                ))
                                Log.i("clickMarker", "is .polyline")
                                drawPolygon(geoPointArrayList)
                            }
                            return@setOnClickListener true
                        }
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
                        polygonForShowAlphaOpacity.setOnClickListener { polygon, mapView, eventPos ->
                            if (polygonListForDrawPolyline.size != 0) {
                                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.removeAll(
                                    polygonListForDrawPolyline
                                )
                            }
                            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
                            geoPointArrayList.add(
                                GeoPoint(
                                    eventPos!!.latitude,
                                    eventPos.longitude
                                )
                            )
                            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.remove(
                                drawPolygonMarker
                            )
                            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
                            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
                            drawPolygonMarker =
                                Marker(viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment)
                            drawPolygonMarker!!.icon =
                                requireActivity().getDrawable(R.drawable.baseline_circle_24)
                            drawPolygonMarker!!.position =
                                GeoPoint(eventPos!!.latitude, eventPos.longitude)
                            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(
                                drawPolygonMarker
                            )
                            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
                            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
                            polyline.addPoint(GeoPoint(eventPos!!.latitude, eventPos.longitude))
                            polygon!!.points = geoPointArrayList

                            polygon!!.fillPaint.color = ColorUtils.setAlphaComponent(ContextCompat.getColor(requireContext(), R.color.polyGon), 0x40)
                            polygon!!.strokeWidth = Color.TRANSPARENT.toFloat()
                            mapView?.overlays!!.add(polygon)
                            if (distance < 80 && geoPointArrayList.size > 3) {
                                geoPointArrayList.add(
                                    GeoPoint(
                                        eventPos!!.latitude,
                                        eventPos.longitude
                                    )
                                )
                                drawPolygon(geoPointArrayList)
                            } else {
                                polylineHistory.add(polyline)
                                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.addAll(
                                    polylineHistory
                                )
                                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
                                viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
                                geoPointArrayList.add(
                                    GeoPoint(
                                        eventPos!!.latitude,
                                        eventPos.longitude
                                    )
                                )
                                Log.i("send","is polygon polyline distance")
                            }
                            mapView?.postInvalidate()
                            true
                        }
                    }
                    if (distance < 80 && geoPointArrayList.size > 3) {
                       // firstLocation = geoPoint
                        Log.i("send","is final distance")
                        drawPolygon(geoPointArrayList)
                    } else {
                        polylineHistory.add(polyline)
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.addAll(
                            polylineHistory
                        )
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
                        return true
                    }
                    return false
                }

                override fun longPressHelper(p: GeoPoint): Boolean {
                    return false
                }
            }
            overlayEvents = MapEventsOverlay(requireActivity(), mReceive)
            viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(
                overlayEvents
            )

        }

    }

    fun getPixelDistance(geoPoint1: GeoPoint, geoPoint2: GeoPoint, zoomLevel: Double, mapView: MapView): Double {
        mapView.controller.setZoom(zoomLevel)
        val point1 = Point()
        val projection = mapView.projection.toPixels(geoPoint1,point1)
        Log.i("tag1"," 1 .... $projection")
        val point2 = Point()
        val projection2 =mapView.projection.toPixels(geoPoint2,point2)
        Log.i("tag1"," 2 .... $projection2")

        val deltaX = point2.x - point1.x
        val deltaY = point2.y - point1.y
        return sqrt((deltaX * deltaX + deltaY * deltaY).toDouble())
    }

    @SuppressLint("ResourceAsColor")
    fun drawPolygon(geoPointArrayListFinal : ArrayList<GeoPoint>): Boolean {
        polygon = Polygon()
        // polyline = Polyline()
        var firstPoint = geoPointArrayListFinal[0]
        var lastPoint = geoPointArrayListFinal[geoPointArrayListFinal.lastIndex]
        lastPoint = firstPoint
        geoPointArrayListFinal[geoPointArrayListFinal.lastIndex] = lastPoint
        Log.i("send"," last is  $lastPoint")
        Log.i("send"," first is  $firstPoint")
        polygon!!.points = geoPointArrayListFinal
        polygon!!.fillPaint.color = R.color.purple_500
        polygon!!.strokeWidth = Color.TRANSPARENT.toFloat()
        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(polygon)
        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.postInvalidate()
        for (i in geoPointArrayListFinal) {
            val lat = i.latitude.toString() + ", " + i.longitude
            shapeList.add(lat)
        }
        val bundle = Bundle()
        bundle.putStringArrayList("shape", ArrayList(shapeList))
        bundle.putBoolean("isDrawPolygon",true)
        // val fm: FragmentManager = requireActivity().supportFragmentManager
        val dialog = DrawPolyGonCircleSaveDialog()
        dialog.setTargetFragment(this@MonitorFragment, 1)
        dialog.arguments = bundle
        dialog.listener = this
        geoPointArrayListFinal.clear()
        shapeList.clear()
        dialog.show(requireFragmentManager(), "")
        return true
    }




    private val reQuestPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        it.entries.forEach { it ->
            val isGranted = it.value
            if (isGranted) {
            }
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun changeMap() {
        val popupMenu = PopupMenu(requireContext(), viewBinding.layoutViewInDrawerMonitorFragment.frChangeMapInMonitorFragment)
        popupMenu.inflate(R.menu.menu_change_app)
        popupMenu.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener,
            PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.open_street_map -> {
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.setTileSource(object : OnlineTileSourceBase(
                            "open_street_map",
                            0,
                            30,
                            256,
                            "",
                            arrayOf("https://mt1.google.com/vt/lyrs=m")
                        ) {
                            override fun getTileURLString(pMapTileIndex: Long): String {
                                return (baseUrl
                                        + "&z=" + MapTileIndex.getZoom(pMapTileIndex)
                                        + "&y=" + MapTileIndex.getY(pMapTileIndex)
                                        + "&x=" + MapTileIndex.getX(pMapTileIndex))
                            }
                        })
                    }
                    R.id.road_map -> {
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.setTileSource(object : OnlineTileSourceBase(
                            "road_map",
                            0,
                            30,
                            256,
                            "",
                            arrayOf("https://mt1.google.com/vt/lyrs=r")
                        ) {
                            override fun getTileURLString(pMapTileIndex: Long): String {
                                return (baseUrl
                                        + "&z=" + MapTileIndex.getZoom(pMapTileIndex)
                                        + "&y=" + MapTileIndex.getY(pMapTileIndex)
                                        + "&x=" + MapTileIndex.getX(pMapTileIndex))
                            }
                        })
                    }
                    R.id.map_dem -> {
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.setTileSource(object : OnlineTileSourceBase(
                            "map_dem",
                            0,
                            30,
                            256,
                            "",
                            arrayOf("https://mt1.google.com/vt/lyrs=p")
                        ) {
                            override fun getTileURLString(pMapTileIndex: Long): String {
                                return (baseUrl
                                        + "&z=" + MapTileIndex.getZoom(pMapTileIndex)
                                        + "&y=" + MapTileIndex.getY(pMapTileIndex)
                                        + "&x=" + MapTileIndex.getX(pMapTileIndex))
                            }
                        })
                    }
                    R.id.map_satellite -> {
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.setTileSource(object : OnlineTileSourceBase(
                            "USGS Topo1",
                            0,
                            30,
                            256,
                            "",
                            arrayOf("https://mt2.google.com/vt/lyrs=s&hl=en")
                        ) {
                            override fun getTileURLString(pMapTileIndex: Long): String {
                                return (baseUrl
                                        + "&z=" + MapTileIndex.getZoom(pMapTileIndex)
                                        + "&y=" + MapTileIndex.getY(pMapTileIndex)
                                        + "&x=" + MapTileIndex.getX(pMapTileIndex))
                            }
                        })
                    }
                    R.id.google_hybrid -> {
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.setTileSource(object : OnlineTileSourceBase(
                            "google hybrid",
                            0,
                            30,
                            256,
                            "",
                            arrayOf("https://mt1.google.com/vt/lyrs=y")
                        ) {
                            override fun getTileURLString(pMapTileIndex: Long): String {
                                return (baseUrl
                                        + "&z=" + MapTileIndex.getZoom(pMapTileIndex)
                                        + "&y=" + MapTileIndex.getY(pMapTileIndex)
                                        + "&x=" + MapTileIndex.getX(pMapTileIndex))
                            }
                        })
                    }
                    R.id.google_traffic -> {
                        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.setTileSource(object : OnlineTileSourceBase(
                            "google_traffic",
                            0,
                            30,
                            256,
                            "",
                            arrayOf("https://mt1.google.com/vt/lyrs=m@221097413,traffic")
                        ) {
                            override fun getTileURLString(pMapTileIndex: Long): String {
                                return (baseUrl
                                        + "&z=" + MapTileIndex.getZoom(pMapTileIndex)
                                        + "&y=" + MapTileIndex.getY(pMapTileIndex)
                                        + "&x=" + MapTileIndex.getX(pMapTileIndex))
                            }
                        })
                    }
                }
                return true
            }
        })

        viewBinding.layoutViewInDrawerMonitorFragment.frChangeMapInMonitorFragment.setOnClickListener {
            viewBinding.layoutViewInDrawerMonitorFragment.frChangeMapInMonitorFragment.setBackgroundResource(R.drawable.on_click_add_poly_gon_monitor)
            viewBinding.layoutViewInDrawerMonitorFragment.btnChangeMapInMonitorFragment.setBackgroundResource(R.drawable.baseline_map_24_on_click)


            viewBinding.layoutViewInDrawerMonitorFragment.frAddCircleInMonitorFragment.setBackgroundResource(R.drawable.click_cancel_change_view_item_map)
            viewBinding.layoutViewInDrawerMonitorFragment.btnAddCircleGonInMainActivity.setBackgroundResource(R.drawable.circle_draw_color)

            viewBinding.layoutViewInDrawerMonitorFragment.frMenuMapInMonitorFragment.setBackgroundResource(R.drawable.click_cancel_change_view_item_map)
            viewBinding.layoutViewInDrawerMonitorFragment.btnDetailsItemMapInMonitorFragment.setBackgroundResource(R.drawable.menu_for_map)

            viewBinding.layoutViewInDrawerMonitorFragment.frGetFencesInMonitorFragment.setBackgroundResource(R.drawable.click_cancel_change_view_item_map)
            viewBinding.layoutViewInDrawerMonitorFragment.btnGetFencesGonInMonitorFragment.setBackgroundResource(R.drawable.map_navigation_pin_icon)

            viewBinding.layoutViewInDrawerMonitorFragment.frAddPolygonInMonitorFragment.setBackgroundResource(R.drawable.click_cancel_change_view_item_map)
            viewBinding.layoutViewInDrawerMonitorFragment.btnAddPolyGonInMonitorFragment.setBackgroundResource(R.drawable.draw_polygon_icon)



            try {
                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val menu = popup.get(popupMenu)
                menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                popupMenu.show()
            }
        }
    }


    private fun loadMap(latitude: Double,longitude: Double) {
        val provider = Configuration.getInstance()
        provider.userAgentValue = BuildConfig.APPLICATION_ID

        val ctx = requireActivity()
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.onStartTemporaryDetach()
        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlayManager.clear()
        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.tileProvider.clearTileCache()


        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.setMultiTouchControls(true)
        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.isTilesScaledToDpi = true


        mapController = viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.controller
        mapController.setZoom(12.5)

        if (!latitude.equals(0.0) && !longitude.equals(0.0)){
            val startPoint = GeoPoint(latitude, longitude)
            mapController.setCenter(startPoint)
        } else {
            val startPoint = GeoPoint(35.781611, 51.365460)
            mapController.setCenter(startPoint)
        }

        mLocationOverlay =
            MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment)
        mLocationOverlay!!.enableMyLocation()
        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(mLocationOverlay)


        val dm = resources.displayMetrics
        mScaleBarOverlay = ScaleBarOverlay(viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment)
        mScaleBarOverlay!!.setCentred(true)

        mScaleBarOverlay!!.setScaleBarOffset(dm.widthPixels / 2, 10)
        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(this.mScaleBarOverlay)


    }

  /*    @SuppressLint("UseCompatLoadingForDrawables")
      private fun addOverlayItem() {
          val items = ArrayList<OverlayItem>()
          items.add(
              OverlayItem(
                  "نقطه پیشفرض",
                  "توضیحات.",
                  GeoPoint(35.781611, 51.365160)
              )
          )


          items.add(
              OverlayItem(
                  "_Home_",
                  "توضیحات.",
                  GeoPoint(35.786611, 51.365260)
              )
          )

          items.add(
              OverlayItem(
                  "Point1",
                  "توضیحات.",
                  GeoPoint(35.782611, 51.365360)
              )
          )

          items.add(
              OverlayItem(
                  "Point2",
                  "توضیحات.",
                  GeoPoint(35.783611, 51.365460)
              )
          )

          items.add(
              OverlayItem(
                  "Point3",
                  "توضیحات.",
                  GeoPoint(35.784611, 51.365560)
              )
          )

          items.add(
              OverlayItem(
                  "Point3",
                  "توضیحات.",
                  GeoPoint(35.785611, 51.365660)
              )
          )

         *//* val icon = this.resources.getDrawable(R.drawable.baseline_location_on_24)

          for (i in 0 until items.size) {
              items[i].setMarker(icon)
          }*//*


          mOverlay = ItemizedOverlayWithFocus(
              items,
              object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                  override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                      mapController.animateTo(item.point)
                      Log.i("overLay", "onItemSingleTop")
                      return false
                  }

                  override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                      Log.i("overLay", "onItemLongPress")
                      return false
                  }
              }, requireContext()
          )
          mOverlay!!.setFocusItemsOnTap(true)
          viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(mOverlay)


          viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.setHasTransientState(true)
      }*/

    private fun showProgress(progress: ProgressBar) {
        progress.visibility = View.VISIBLE
    }

    private fun hideProgress(progress: ProgressBar) {
        progress.visibility = View.GONE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.onDetach()
        _binding = null
    }

    override fun onDialogDismissed() {
        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlayManager.clear()
        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.invalidate()
        isBackFromDialog = true
        sharedPreferences.edit()
            .putBoolean(Constants.IS_GET_DEVICES, false)
            .apply()
        viewBinding.layoutViewInDrawerMonitorFragment.frAddPolygonInMonitorFragment.setBackgroundResource(R.drawable.click_cancel_change_view_item_map)
        viewBinding.layoutViewInDrawerMonitorFragment.btnAddPolyGonInMonitorFragment.setBackgroundResource(R.drawable.draw_polygon_icon)
        viewBinding.layoutViewInDrawerMonitorFragment.frAddCircleInMonitorFragment.setBackgroundResource(R.drawable.click_cancel_change_view_item_map)
        viewBinding.layoutViewInDrawerMonitorFragment.btnAddCircleGonInMainActivity.setBackgroundResource(R.drawable.circle_draw_color)
    }

    override fun onDialogShown() {

        //getDevicesFromServer(deviceId)
    }

    override fun onStop() {
        super.onStop()
        Log.i("onStop","is ...")
        sharedPreferences.edit()
            .putBoolean(Constants.IS_GET_DEVICES, true)
            .apply()
       // getDevicesFromServer("")
    }


    override fun onEventTriggered() {
        viewBinding.drawerInMonitorFragment.closeDrawer(GravityCompat.START)
        mReceive = object : MapEventsReceiver {
            @SuppressLint(
                "ResourceAsColor", "UseCompatLoadingForDrawables",
                "ClickableViewAccessibility"
            )
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                InfoWindow.closeAllInfoWindowsOn(viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment);
                return false
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }
        overlayEvents = MapEventsOverlay(requireActivity(), mReceive)
        viewBinding.layoutViewInDrawerMonitorFragment.mapInMonitorFragment.overlays.add(
            overlayEvents
        )
    }


    fun getLocationAddress(lat: String,long: String,textShowAddress: TextView,txtTypeAddress: TextView,progress: ProgressBar){
        showProgress(progress)
        monitorViewModel.getAddress(
            "Bearer $token", lat, long, "fa", "asia_tehran", "celsius", "km", "liter"
        )
        monitorViewModel.getAddress.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgress(progress)
                    response.data.let {
                        txtTypeAddress.visibility = View.GONE
                        textShowAddress.text = it.result.address
                        textShowAddress.visibility = View.VISIBLE
                    }
                }
                is Resource.Error -> {
                    hideProgress(progress)
                }
                is Resource.Loading -> {
                    toast(requireContext(),requireActivity().getString(R.string.loading))
                    showProgress(progress)
                }
                else -> {}
            }
        })

    }


    inner class CustomInfoWindow(
        mapView: MapView,
        lat: Double,
        long: Double,
        name: String,
        imei: String,
        speed: Int,
        connectAt: String,
        fixed: String,
        connectionStatus: String,
        context: Context
    ) : InfoWindow(R.layout.layout_second_popup_window12,mapView) {

        init {
            val txtNameDevice =
                mView.findViewById<TextView>(R.id.txt_device_name_in_popup_marker)
            val txtImeiDevice = mView.findViewById<TextView>(R.id.txt_imei_in_popup_marker)
            val txtSpeed =
                mView.findViewById<TextView>(R.id.txt_detail_speed_device_in_popup_marker)
            val txtLastDate =
                mView.findViewById<TextView>(R.id.txt_detail_date_device_in_popup_marker)
            val txtLastLocation =
                mView.findViewById<TextView>(R.id.txt_detail_last_location_device_in_popup_marker)

            val imageStatus = mView.findViewById<ImageView>(R.id.iv_status_in_popup_marker)
            val txtStatusConnection =
                mView.findViewById<TextView>(R.id.txt_show_status_in_popup_marker)
            val txtTypeAddress =
                mView.findViewById<TextView>(R.id.txt_address_location_device_in_popup_marker)
            val txtShowAddress =
                mView.findViewById<TextView>(R.id.txt_detail_address_location_device_in_popup_marker)
            val progress =
                mView.findViewById<ProgressBar>(R.id.progress_in_window_popup_window)
            txtNameDevice.text = name
            txtImeiDevice.text = imei
            val drawable = ContextCompat.getDrawable(
                context,
                R.drawable.icon_online_status_in_adapter
            )
            val drawableOf =
                ContextCompat.getDrawable(context, R.drawable.icon_offline_status_in_adapter)
            if (connectionStatus == "online") {
                if (speed != 0){
                    imageStatus.setImageDrawable(drawable)
                    txtStatusConnection.text = context.getText(R.string.status_device_moving)
                } else {
                    imageStatus.setImageDrawable(drawable)
                    txtStatusConnection.text = context.getText(R.string.status_device_online)
                }
            } else {
                imageStatus.setImageDrawable(drawableOf)
                txtStatusConnection.text = context.getText(R.string.status_device_offline)
            }

            val content = SpannableString(context.getText(R.string.address_location))
            content.setSpan(UnderlineSpan(), 0, content.length, 0)
            txtTypeAddress.text = content
            val showSpeed: String = context.getString(R.string.detail_speed, speed.toString())
            txtSpeed.text = showSpeed

            val config: android.content.res.Configuration? =
                context!!.resources.configuration

            if (config!!.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                val inputCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                inputCalendar.time = dateFormat.parse(connectAt!!)!!
                val calendarFA = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"), Locale("fa", "IR"))
                calendarFA.timeInMillis = inputCalendar.timeInMillis
                val persianCalendar = PersianCalendar()
                persianCalendar.timeInMillis = calendarFA.timeInMillis
                val newTimeZone = TimeZone.getTimeZone("Asia/Tehran")
                persianCalendar.timeZone = newTimeZone
                val lastDate = Constants.setPersianNumbers(persianCalendar.persianShortDateTime)
                txtLastDate.text = lastDate


                val inputFormatLastLocation = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                val inputDateLastLocation = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                inputDateLastLocation.time = inputFormatLastLocation.parse(fixed!!)!!
                val calendarFALastLocation  = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"), Locale("fa", "IR"))
                calendarFALastLocation.timeInMillis = inputDateLastLocation.timeInMillis
                val persianCalendarLast = PersianCalendar()
                persianCalendarLast.timeInMillis = calendarFALastLocation.timeInMillis
                val newTimeZoneLocation = TimeZone.getTimeZone("Asia/Tehran")
                persianCalendarLast.timeZone = newTimeZoneLocation
                val lastDateLastLocation =
                    Constants.setPersianNumbers(persianCalendarLast.persianShortDateTime)
                txtLastLocation.text = lastDateLastLocation

            } else {
                txtLastDate.text = connectAt
                txtLastLocation.text = fixed
            }

            txtTypeAddress.setOnClickListener {
                getLocationAddress(lat.toString(),long.toString(),txtShowAddress,txtTypeAddress,progress)
            }
        }


        override fun onOpen(item: Any?) {

        }

        override fun onClose() {
            super.close()
        }


    }



}



