package com.example.mapproject.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mapproject.BuildConfig
import com.example.mapproject.R
import com.example.mapproject.databinding.FragmentHomeBinding
import com.example.mapproject.model.constant.ResultXX
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.overlay.*
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


@Suppress("DEPRECATION")
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding


    private var mLocationOverlay: MyLocationNewOverlay? = null
    private var mRotationGestureOverlay: RotationGestureOverlay? = null
    private var mScaleBarOverlay: ScaleBarOverlay? = null
    private var mMinimapOverlay: MinimapOverlay? = null
    private var mOverlay: ItemizedOverlayWithFocus<OverlayItem>? = null
    private lateinit var mapController: IMapController
    private val geoPointArrayList = ArrayList<GeoPoint>()
    private lateinit var geoPoint: GeoPoint
    private lateinit var polyline: Polyline
    private lateinit var polygon: Polygon
    private var isPolygon : Boolean = false
    lateinit var constant: ResultXX


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        polygon = Polygon()
        polyline = Polyline()
        polyline.outlinePaint.strokeWidth = 10f
        polyline.outlinePaint.color = Color.BLUE
        constant = requireActivity().intent.extras?.getParcelable("constant")!!
        Log.i("constant", constant.toString())
        loadMap()
        binding.btnChangeMapInMainActivity.setOnClickListener {
            binding.map.setTileSource(object : OnlineTileSourceBase(
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

        binding.btnDrawPolyLineInMainActivity.setOnClickListener {
            Toast.makeText(requireContext(), "draw on the map", Toast.LENGTH_SHORT).show()
            val mReceive: MapEventsReceiver = object : MapEventsReceiver {
                @SuppressLint("ResourceAsColor")
                override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                    Log.i("PREFS_NAME","x is ${p.latitude}")
                    geoPoint = GeoPoint(p.latitude,p.longitude)
                    geoPointArrayList.add(geoPoint)
                    polyline.addPoint(geoPoint)
                    val firstLocation = geoPointArrayList[0]
                    val results = FloatArray(1)
                    Location.distanceBetween(p.latitude,p.longitude,firstLocation.latitude,firstLocation.longitude,results)
                    if (results[0] < 3.0 && geoPointArrayList.size > 1){
                        polygon.points = geoPointArrayList
                        polygon.fillPaint.color = R.color.purple_500
                        binding.map.invalidate()
                        binding.map.overlays.add(polygon)
                        binding.map.postInvalidate()
                        geoPointArrayList.clear()
                    } else {
                        binding.map.overlays.add(polyline)
                        binding.map.postInvalidate()

                    }
                    return false
                }

                override fun longPressHelper(p: GeoPoint): Boolean {
                    return false
                }
            }
            val overlayEvents = MapEventsOverlay(requireActivity(), mReceive)
            binding.map.overlays.add(overlayEvents)
        }
        reQuestPermission.launch(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION))
    }

    private val reQuestPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {
        it.entries.forEach { it ->
            val isGranted = it.value
            if (isGranted){
                addOverlayItem()
            }
        }
    }

    private fun loadMap(){
        val provider = Configuration.getInstance()
        provider.userAgentValue = BuildConfig.APPLICATION_ID

        val ctx = requireActivity()
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        binding.map.onStartTemporaryDetach()
        //viewBinding.map.overlays.clear()
        binding.map.tileProvider.clearTileCache()

        //viewBinding.map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        binding.map.setTileSource(object : OnlineTileSourceBase(
            "USGS Topo",
            0,
            30,
            256,
            "",
            arrayOf("https://mt2.google.com/vt/lyrs=m@221097413,traffic&hl=en")
        ) {
            override fun getTileURLString(pMapTileIndex: Long): String {
                return (baseUrl
                        + "&z=" + MapTileIndex.getZoom(pMapTileIndex)
                        + "&y=" + MapTileIndex.getY(pMapTileIndex)
                        + "&x=" + MapTileIndex.getX(pMapTileIndex))
            }
        })
        binding.map.setMultiTouchControls(true)
        binding.map.isTilesScaledToDpi = true


        mapController = binding.map.controller
        mapController.setZoom(19.5)

        val startPoint = GeoPoint(35.781611, 51.365460)
        mapController.setCenter(startPoint)

        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), binding.map)
        mLocationOverlay!!.enableMyLocation()
        binding.map.overlays.add(mLocationOverlay)


        val dm = resources.displayMetrics
        mScaleBarOverlay = ScaleBarOverlay(binding.map)
        mScaleBarOverlay!!.setCentred(true)

        mScaleBarOverlay!!.setScaleBarOffset(dm.widthPixels / 2, 10)
        binding.map.overlays.add(this.mScaleBarOverlay)


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addOverlayItem(){
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

        val icon = this.resources.getDrawable(R.drawable.baseline_location_on_24)

        for (i in 0 until items.size){
            items[i].setMarker(icon)
        }


        mOverlay = ItemizedOverlayWithFocus(items,
            object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                    mapController.animateTo(item.point)
                    Log.i("overLay","onItemSingleTop")
                    return false
                }

                override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                    Log.i("overLay","onItemLongPress")
                    return false
                }
            }, requireContext()
        )
        mOverlay!!.setFocusItemsOnTap(true)
        binding.map.overlays.add(mOverlay)


        binding.map.setHasTransientState(true)
    }

    override fun onDetach() {
        super.onDetach()
        binding.map.onDetach()
    }

}