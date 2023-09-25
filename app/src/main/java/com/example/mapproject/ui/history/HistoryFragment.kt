package com.example.mapproject.ui.history

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mapproject.BuildConfig
import com.example.mapproject.R
import com.example.mapproject.adapters.*
import com.example.mapproject.databinding.LayoutDrawerHistoryFragmentBinding
import com.example.mapproject.model.devices.DeviceX
import com.example.mapproject.model.devices.ResultXXX
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import com.example.mapproject.util.toast
import dagger.hilt.android.AndroidEntryPoint
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog
import ir.hamsaa.persiandatepicker.api.PersianPickerDate
import ir.hamsaa.persiandatepicker.api.PersianPickerListener
import ir.smartlab.persiandatepicker.util.PersianCalendar
import ir.smartlab.persiandatepicker.util.PersianCalendarUtils
import org.jetbrains.annotations.NotNull
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import javax.inject.Inject


@AndroidEntryPoint
class HistoryFragment : Fragment(), OnItemGroupsClick , OnItemDetailClick {

    private var _binding: LayoutDrawerHistoryFragmentBinding? = null
    private val viewBinding get() = _binding!!
    private var mLocationOverlay: MyLocationNewOverlay? = null
    private lateinit var mapController: IMapController
    private var mScaleBarOverlay: ScaleBarOverlay? = null
    private lateinit var userName: String
    private lateinit var groupsDevicesAdapter: GroupsDevicesAdapter
    private var resultDevice: ArrayList<DeviceX> = arrayListOf()
    private var resultAllGroup: ArrayList<ResultXXX> = arrayListOf()
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var userID: String
    private lateinit var token: String
    private lateinit var tokenID: String
    private lateinit var detailDevicesGroupAdapter: GroupDetailDevicesAdapter


    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutDrawerHistoryFragmentBinding.inflate(inflater, container, false)
        viewBinding.layoutViewInDrawerHistoryFragment.mapInHistoryFragment.onResume()
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historyViewModel =
            ViewModelProvider(requireActivity())[HistoryViewModel::class.java]
        userName = sharedPreferences.getString(Constants.USERNAME, "").toString()
        userID = sharedPreferences.getString(Constants.KEY_USER_ID, "").toString()
        token = sharedPreferences.getString(Constants.KEY_TOKEN, "").toString()
        tokenID = sharedPreferences.getString(Constants.KEY_TOKEN_ID, "").toString()
        loadMap()
        changeMap()
        getDevicesFromServer("")
        checkListenerDrawer()
        startDatePicker()
        viewBinding.layoutViewInDrawerHistoryFragment.frMenuMapInHistoryFragment.setOnClickListener {
            viewBinding.drawerInHistoryFragment.openDrawer(GravityCompat.START)

        }
        viewBinding.btnDeviceHistoryFragment.setOnClickListener {
            if (viewBinding.rvGroupInHistoryFragment.visibility == View.VISIBLE) {
                viewBinding.rvGroupInHistoryFragment.visibility = View.GONE
                viewBinding.etSearchGroupInHistoryFragment.visibility = View.GONE
                viewBinding.txtEndDateTitleInHistoryFragment.visibility = View.VISIBLE
                viewBinding.txtStartDateTitleInHistoryFragment.visibility = View.VISIBLE
                viewBinding.btnStartDateHistoryFragment.visibility = View.VISIBLE
                viewBinding.btnEndDateHistoryFragment.visibility = View.VISIBLE
                viewBinding.btnTodayInHistoryFragment.visibility = View.VISIBLE
                viewBinding.btnLastWeekInHistoryFragment.visibility = View.VISIBLE
                viewBinding.btnYeserdayInHistoryFragment.visibility = View.VISIBLE
                viewBinding.btnSearchInHistoryFragment.visibility = View.VISIBLE
                viewBinding.btnResetInHistoryFragment.visibility = View.VISIBLE
            } else {
                setupRecyclerViewGroups()
                groupsDevicesAdapter.updateList(resultAllGroup)
                viewBinding.rvGroupInHistoryFragment.visibility = View.VISIBLE
                viewBinding.etSearchGroupInHistoryFragment.visibility = View.VISIBLE
                viewBinding.txtEndDateTitleInHistoryFragment.visibility = View.GONE
                viewBinding.txtStartDateTitleInHistoryFragment.visibility = View.GONE
                viewBinding.btnStartDateHistoryFragment.visibility = View.GONE
                viewBinding.btnEndDateHistoryFragment.visibility = View.GONE
                viewBinding.btnTodayInHistoryFragment.visibility = View.GONE
                viewBinding.btnLastWeekInHistoryFragment.visibility = View.GONE
                viewBinding.btnYeserdayInHistoryFragment.visibility = View.GONE
                viewBinding.btnSearchInHistoryFragment.visibility = View.GONE
                viewBinding.btnResetInHistoryFragment.visibility = View.GONE
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewBinding.drawerInHistoryFragment.isDrawerVisible(GravityCompat.START)) {
                    viewBinding.drawerInHistoryFragment.closeDrawer(GravityCompat.START)
                    return
                } else {
                    findNavController().navigate(R.id.action_monitorFragment_to_serviceCarFragment)
                    return
                }
            }
        })

    }

    private fun startDatePicker(){
        viewBinding.btnStartDateHistoryFragment.setOnClickListener {
            val persianCalendar = PersianCalendar()
            val picker = PersianDatePickerDialog(requireContext())
                .setPositiveButtonString("باشه")
                .setNegativeButton("بیخیال")
                .setTodayButton("امروز")
                .setTodayButtonVisible(true)
                .setMinYear(1300)
                .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                .setMaxMonth(PersianDatePickerDialog.THIS_MONTH)
                .setMaxDay(PersianDatePickerDialog.THIS_DAY)
                .setInitDate(persianCalendar.persianYear, persianCalendar.persianMonth, persianCalendar.persianDay)
                .setActionTextColor(Color.GRAY)
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(true)
                .setListener(object : PersianPickerListener {
                    override fun onDateSelected(@NotNull persianPickerDate: PersianPickerDate) {
                        Log.d(
                            "picker",
                            "onDateSelected: " + persianPickerDate.timestamp
                        ) //675930448000
                        Log.d(
                            "picker",
                            "onDateSelected: " + persianPickerDate.gregorianDate
                        ) //Mon Jun 03 10:57:28 GMT+04:30 1991
                        Log.d(
                            "picker",
                            "onDateSelected: " + persianPickerDate.persianLongDate
                        ) // دوشنبه  13  خرداد  1370
                        Log.d(
                            "picker",
                            "onDateSelected: " + persianPickerDate.persianMonthName
                        ) //خرداد
                        Log.d(
                            "picker",
                            "onDateSelected: " + PersianCalendarUtils.isPersianLeapYear(
                                persianPickerDate.persianYear
                            )
                        ) //true
                        Toast.makeText(
                            context,
                            (persianPickerDate.persianYear.toString() + "/" + persianPickerDate.persianMonth).toString() + "/" + persianPickerDate.persianDay,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onDismissed() {

                    }
                })

            picker.show()
        }
    }

    private fun getDevicesFromServer(id: String){
        historyViewModel.getDevices(
            "Bearer $token", userID, "1", id, "fa", "asia_tehran", "celsius", "km", "liter"
        )
        historyViewModel.getDevices.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgress()
                    response.data.let {
                        if (resultAllGroup.size != 0) {
                            resultAllGroup.clear()
                        }
                        for (i in it.result) {
                            resultAllGroup.add(i)
                        }

                    }
                }
                is Resource.Error -> {
                    hideProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        })
    }


    private fun getDetailGroupDevices(id: Int){
        if (resultDevice.size != 0){
            resultDevice.clear()
        }
        for (i in resultAllGroup) {
            for (j in i.devices) {
                if (id == i.id) {
                    resultDevice.add(j)
                }
            }
        }
    }

    private fun showProgress() {
        viewBinding.progressInHistoryFragment.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        viewBinding.progressInHistoryFragment.visibility = View.GONE
    }

    private fun setupRecyclerViewGroups() {
        groupsDevicesAdapter = GroupsDevicesAdapter(
            requireActivity(),
            this@HistoryFragment,
            resultDevice,
            this@HistoryFragment
        )
        viewBinding.rvGroupInHistoryFragment.apply {
            adapter = groupsDevicesAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    private fun checkListenerDrawer(){
        viewBinding.drawerInHistoryFragment.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
                sharedPreferences.edit()
                    .putBoolean(Constants.IS_GET_DEVICES, true)
                    .apply()
                viewBinding.appBarHeaderHistoryFragment.txtUserNameInMenuMonitorFragment.text = userName
            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })
    }

    private fun loadMap(){
        val provider = Configuration.getInstance()
        provider.userAgentValue = BuildConfig.APPLICATION_ID

        val ctx = requireActivity()
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        viewBinding.layoutViewInDrawerHistoryFragment.mapInHistoryFragment.onStartTemporaryDetach()
        viewBinding.layoutViewInDrawerHistoryFragment.mapInHistoryFragment.overlayManager.clear()
        viewBinding.layoutViewInDrawerHistoryFragment.mapInHistoryFragment.tileProvider.clearTileCache()


        viewBinding.layoutViewInDrawerHistoryFragment.mapInHistoryFragment.setMultiTouchControls(true)
        viewBinding.layoutViewInDrawerHistoryFragment.mapInHistoryFragment.isTilesScaledToDpi = true


        mapController = viewBinding.layoutViewInDrawerHistoryFragment.mapInHistoryFragment.controller
        mapController.setZoom(19.5)
        val startPoint = GeoPoint(35.781611, 51.365460)
        mapController.setCenter(startPoint)


        mLocationOverlay =
            MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), viewBinding.layoutViewInDrawerHistoryFragment.mapInHistoryFragment)
        mLocationOverlay!!.enableMyLocation()
        viewBinding.layoutViewInDrawerHistoryFragment.mapInHistoryFragment.overlays.add(mLocationOverlay)


        val dm = resources.displayMetrics
        mScaleBarOverlay = ScaleBarOverlay(viewBinding.layoutViewInDrawerHistoryFragment.mapInHistoryFragment)
        mScaleBarOverlay!!.setCentred(true)

        mScaleBarOverlay!!.setScaleBarOffset(dm.widthPixels / 2, 10)
        viewBinding.layoutViewInDrawerHistoryFragment.mapInHistoryFragment.overlays.add(this.mScaleBarOverlay)
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun changeMap() {
        val popupMenu = PopupMenu(requireContext(), viewBinding.layoutViewInDrawerHistoryFragment.frChangeMapInHistoryFragment)
        popupMenu.inflate(R.menu.menu_change_app)
        popupMenu.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener,
            PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.open_street_map -> {
                        viewBinding.layoutViewInDrawerHistoryFragment.mapInHistoryFragment.setTileSource(object : OnlineTileSourceBase(
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
                        viewBinding.layoutViewInDrawerHistoryFragment.mapInHistoryFragment.setTileSource(object : OnlineTileSourceBase(
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
                        viewBinding.layoutViewInDrawerHistoryFragment.mapInHistoryFragment.setTileSource(object : OnlineTileSourceBase(
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
                        viewBinding.layoutViewInDrawerHistoryFragment.mapInHistoryFragment.setTileSource(object : OnlineTileSourceBase(
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
                        viewBinding.layoutViewInDrawerHistoryFragment.mapInHistoryFragment.setTileSource(object : OnlineTileSourceBase(
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
                        viewBinding.layoutViewInDrawerHistoryFragment.mapInHistoryFragment.setTileSource(object : OnlineTileSourceBase(
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

        viewBinding.layoutViewInDrawerHistoryFragment.frChangeMapInHistoryFragment.setOnClickListener {
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

    override fun onItemGroupClick(id: Int) {
        getDetailGroupDevices(id)
        groupsDevicesAdapter = GroupsDevicesAdapter(
            requireActivity(),
            this@HistoryFragment,
            resultDevice,
            this@HistoryFragment
        )
        if (resultDevice.size == 0) {
            toast(
                requireActivity(),
                requireActivity().getString(R.string.toast_result_search_group)
            )
        }

    }

    override fun onItemDetailClick(id: String, name: String, latitude: Double, longitude: Double) {

    }

}