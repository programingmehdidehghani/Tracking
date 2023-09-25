package com.example.mapproject.ui.devicesFragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mapproject.R
import com.example.mapproject.adapters.*
import com.example.mapproject.databinding.FragmentDevicesBinding
import com.example.mapproject.model.devices.DeviceX
import com.example.mapproject.model.devices.ResultXXX
import com.example.mapproject.ui.monitor.MonitorFragment
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import com.example.mapproject.util.TextDrawable
import com.example.mapproject.util.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject



interface MyEventListener {
    fun onEventTriggered()
}
@Suppress("DEPRECATION")
@AndroidEntryPoint
class DevicesFragment : Fragment(), OnItemGroupsClick, OnItemAllStatusClick, OnItemDetailClick {

    private var _binding: FragmentDevicesBinding? = null
    private val viewBinding get() = _binding!!

    private lateinit var groupsDevicesAdapter: GroupsDevicesAdapter
    private var resultXXX: ArrayList<ResultXXX> = arrayListOf()
    private var resultDevice: ArrayList<DeviceX> = arrayListOf()
    private lateinit var allStatusDeviceAdapter: AllStatusDeviceAdapter
    private var isOnlineDevice: Boolean = false
    private var isOfflineDevice: Boolean = false
    private lateinit var devicesFragmentViewModel: DevicesFragmentViewModel
    private var resultSearch: ArrayList<ResultXXX> = arrayListOf()
    private var resultSearchDevice: ArrayList<DeviceX> = arrayListOf()
    private lateinit var detailDevicesGroupAdapter: GroupDetailDevicesAdapter
    private var resultAllGroup: ArrayList<ResultXXX> = arrayListOf()
    private var resultAllDevice: ArrayList<DeviceX> = arrayListOf()
    private var listener: MyEventListener? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var userID: String
    private lateinit var token: String
    private lateinit var tokenID: String
    private var deviceName: String = ""
    private var isClickDeletedDeviceName: Boolean = false
    private var deviceID: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDevicesBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        devicesFragmentViewModel =
            ViewModelProvider(requireActivity())[DevicesFragmentViewModel::class.java]
        userID = sharedPreferences.getString(Constants.KEY_USER_ID, "").toString()
        token = sharedPreferences.getString(Constants.KEY_TOKEN, "").toString()
        tokenID = sharedPreferences.getString(Constants.KEY_TOKEN_ID, "").toString()
        deviceName = sharedPreferences.getString(Constants.DEVICE_NAME, "").toString()
        deviceID = sharedPreferences.getString(Constants.DEVICE_ID, "").toString()
        viewBinding.txtShowGroupInDeviceFragment.text = deviceName
        getAllGroupDevices("","")
        setupRecyclerViewGroups()
        setupRecyclerViewAllStatus(resultDevice)
        searchGroups()
        deletedDeviceName()


        viewBinding.txtShowGroupInDeviceFragment.setOnClickListener {
            if (viewBinding.rvGroupInDeviceFragment.visibility == View.VISIBLE) {
                allStatusDeviceAdapter =
                    AllStatusDeviceAdapter(requireActivity(), this@DevicesFragment, resultAllDevice)
                viewBinding.rvStatusInDeviceFragment.apply {
                    adapter = allStatusDeviceAdapter
                    layoutManager = LinearLayoutManager(requireActivity())
                }
                setupRecyclerViewAllStatus(resultAllDevice)
                allStatusDeviceAdapter.updateList(resultAllGroup)
                viewBinding.etSearchGroupInFragmentDevice.text!!.clear()
                viewBinding.rvGroupInDeviceFragment.visibility = View.GONE
                viewBinding.etSearchGroupInFragmentDevice.visibility = View.GONE
                viewBinding.txtStatusDeviceInDeviceFragment.visibility = View.VISIBLE
                viewBinding.lnThreeViewInDeviceFragment.visibility = View.VISIBLE
                viewBinding.rvStatusInDeviceFragment.visibility = View.VISIBLE
                isOfflineDevice = false
                isOnlineDevice = false
                viewBinding.btnAllStatusInDeviceFragment.setBackgroundColor(
                    requireActivity().getColor(
                        R.color.veryBlack
                    )
                )
                viewBinding.btnStatusOnlineInDeviceFragment.setBackgroundColor(
                    requireActivity().getColor(
                        R.color.DarkBlue
                    )
                )
                viewBinding.btnStatusOfflineInDeviceFragment.setBackgroundColor(
                    requireActivity().getColor(
                        R.color.DarkBlue
                    )
                )
            } else {
                if (resultXXX.size != 0) {
                    setupRecyclerViewAllStatus(resultDevice)
                    allStatusDeviceAdapter.updateList(resultXXX)
                }
                groupsDevicesAdapter = GroupsDevicesAdapter(
                    requireActivity(),
                    this@DevicesFragment,
                    resultAllDevice,
                    this@DevicesFragment
                )
                viewBinding.rvGroupInDeviceFragment.apply {
                    adapter = groupsDevicesAdapter
                    layoutManager = LinearLayoutManager(requireActivity())
                }
                groupsDevicesAdapter.updateList(resultAllGroup)
                viewBinding.rvGroupInDeviceFragment.visibility = View.VISIBLE
                viewBinding.etSearchGroupInFragmentDevice.visibility = View.VISIBLE
                statusView()
            }

        }
        clickOnButtonsForState()

    }

    private fun deletedDeviceName() {
        if (deviceID != ""){
            viewBinding.ivDeleteDeviceNameInDeviceFragment.visibility = View.VISIBLE
        }
        viewBinding.ivDeleteDeviceNameInDeviceFragment.setOnClickListener {
            sharedPreferences.edit()
                .putString(Constants.DEVICE_ID, "")
                .apply()
            sharedPreferences.edit()
                .putString(Constants.DEVICE_NAME, "")
                .apply()
            sharedPreferences.edit()
                .putString(Constants.LATITUDE, "")
                .apply()
            sharedPreferences.edit()
                .putString(Constants.LONGITUDE, "")
                .apply()
            viewBinding.txtShowGroupInDeviceFragment.text =
                requireActivity().getText(R.string.select_group)
            viewBinding.ivDeleteDeviceNameInDeviceFragment.visibility = View.GONE
            showNumberStatusResult("")
        }
    }

    private fun searchGroups() {
        viewBinding.etSearchGroupInFragmentDevice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length > 3) {
                    devicesFragmentViewModel.getResultSearchGroup(
                        "Bearer $token",
                        userID,
                        s.toString(),
                        "1",
                        "",
                        "fa",
                        "asia_tehran",
                        "celsius",
                        "km",
                        "liter"
                    )
                    devicesFragmentViewModel.getResultSearchGroup.observe(
                        viewLifecycleOwner,
                        Observer { response ->
                            when (response) {
                                is Resource.Success -> {
                                    hideProgress()
                                    response.data.let {
                                        if (resultSearch.size != 0 || resultSearchDevice.size != 0) {
                                            resultSearch.clear()
                                            resultSearchDevice.clear()
                                        }
                                        for (i in it.result) {
                                            resultSearch.add(i)
                                            for (j in i.devices) {
                                                resultSearchDevice.add(j)
                                            }
                                        }
                                        if (resultSearch.size != 0) {
                                            detailDevicesGroupAdapter =
                                                GroupDetailDevicesAdapter(this@DevicesFragment)
                                            viewBinding.rvGroupInDeviceFragment.apply {
                                                hasFixedSize()
                                                adapter = detailDevicesGroupAdapter
                                                layoutManager = LinearLayoutManager(context)
                                            }
                                            detailDevicesGroupAdapter.updateList(resultSearchDevice)
                                          //  allStatusDeviceAdapter.updateList(resultAllGroup)
                                            viewBinding.rvGroupInDeviceFragment.visibility =
                                                View.VISIBLE
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
                } else {
                    groupsDevicesAdapter = GroupsDevicesAdapter(
                        requireActivity(),
                        this@DevicesFragment,
                        resultDevice,
                        this@DevicesFragment
                    )
                    viewBinding.rvGroupInDeviceFragment.apply {
                        hasFixedSize()
                        adapter = groupsDevicesAdapter
                        layoutManager = LinearLayoutManager(requireActivity())
                    }
                    groupsDevicesAdapter.updateList(resultAllGroup)
                    viewBinding.rvGroupInDeviceFragment.visibility = View.GONE
                    viewBinding.rvGroupInDeviceFragment.visibility = View.VISIBLE
                    toast(requireContext(),"حداقل سه کاراکتر وارد نمایید")
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == "") {
                    groupsDevicesAdapter = GroupsDevicesAdapter(
                        requireActivity(),
                        this@DevicesFragment,
                        resultDevice,
                        this@DevicesFragment
                    )
                    viewBinding.rvGroupInDeviceFragment.apply {
                        adapter = groupsDevicesAdapter
                        layoutManager = LinearLayoutManager(requireActivity())
                    }
                    groupsDevicesAdapter.updateList(resultAllGroup)
                    viewBinding.rvGroupInDeviceFragment.visibility = View.GONE
                    viewBinding.rvGroupInDeviceFragment.visibility = View.VISIBLE
                }
            }

        })

    }

    private fun clickOnButtonsForState() {
        viewBinding.btnAllStatusInDeviceFragment.setOnClickListener {
            viewBinding.btnAllStatusInDeviceFragment.setBackgroundColor(requireActivity().getColor(R.color.veryBlack))
            viewBinding.btnStatusOnlineInDeviceFragment.setBackgroundColor(
                requireActivity().getColor(
                    R.color.DarkBlue
                )
            )
            viewBinding.btnStatusOfflineInDeviceFragment.setBackgroundColor(
                requireActivity().getColor(
                    R.color.DarkBlue
                )
            )
            allStatusDeviceAdapter.setViewVisibility(false)
            if (isClickDeletedDeviceName) {
                setupRecyclerViewAllStatus(resultAllDevice)
                allStatusDeviceAdapter.updateList(resultAllGroup)
            } else {
                setupRecyclerViewAllStatus(resultDevice)
                allStatusDeviceAdapter.updateList(resultXXX)
            }
            isOfflineDevice = false
            isOnlineDevice = false
        }
        viewBinding.btnStatusOfflineInDeviceFragment.setOnClickListener {
            viewBinding.btnStatusOfflineInDeviceFragment.setBackgroundColor(
                requireActivity().getColor(
                    R.color.gary_low
                )
            )
            viewBinding.btnStatusOnlineInDeviceFragment.setBackgroundColor(
                requireActivity().getColor(
                    R.color.DarkBlue
                )
            )
            viewBinding.btnAllStatusInDeviceFragment.setBackgroundColor(requireActivity().getColor(R.color.DarkBlue))
            allStatusDeviceAdapter.setViewVisibility(false)
            if (isClickDeletedDeviceName) {
                setupRecyclerViewAllStatus(resultAllDevice)
                allStatusDeviceAdapter.updateList(resultAllGroup)
            } else {
                setupRecyclerViewAllStatus(resultDevice)
                allStatusDeviceAdapter.updateList(resultXXX)
            }
            isOfflineDevice = true
            isOnlineDevice = false
        }
        viewBinding.btnStatusOnlineInDeviceFragment.setOnClickListener {
            viewBinding.btnStatusOnlineInDeviceFragment.setBackgroundColor(
                requireActivity().getColor(
                    R.color.veryGreen
                )
            )
            viewBinding.btnStatusOfflineInDeviceFragment.setBackgroundColor(
                requireActivity().getColor(
                    R.color.DarkBlue
                )
            )
            viewBinding.btnAllStatusInDeviceFragment.setBackgroundColor(requireActivity().getColor(R.color.DarkBlue))
            allStatusDeviceAdapter.setViewVisibility(false)
            if (isClickDeletedDeviceName) {
                setupRecyclerViewAllStatus(resultAllDevice)
                allStatusDeviceAdapter.updateList(resultAllGroup)
            } else {
                setupRecyclerViewAllStatus(resultDevice)
                allStatusDeviceAdapter.updateList(resultXXX)
            }
            isOnlineDevice = true
            isOfflineDevice = false
        }
    }

    private fun getAllGroupDevices(id: String, status: String) {
        devicesFragmentViewModel.getDevices(
            "Bearer $token", userID, "1", id, "fa", "asia_tehran", "celsius", "km", "liter"
        )
        devicesFragmentViewModel.getDevices.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgress()
                    response.data.let {
                        if (resultXXX.size != 0 || resultDevice.size != 0 || resultAllGroup.size != 0) {
                            resultXXX.clear()
                            resultDevice.clear()
                            resultAllGroup.clear()
                        }
                        for (i in it.result) {
                            resultXXX.add(i)
                            resultAllGroup.add(i)
                                for (j in i.devices) {
                                    if (status == "") {
                                        resultDevice.add(j)
                                    } else {
                                        if (status == j.data!!.connection_status) {
                                            resultDevice.add(j)
                                        }
                                    }
                                }
                        }
                    }
                    if (resultXXX.size != 0) {
                        setupRecyclerViewAllStatus(resultDevice)
                        allStatusDeviceAdapter.updateList(resultXXX)
                        viewBinding.rvStatusInDeviceFragment.visibility = View.VISIBLE
                    }

                    showNumberStatusResult(deviceID)

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

    private fun statusView() {
        viewBinding.txtStatusDeviceInDeviceFragment.visibility = View.GONE
        viewBinding.lnThreeViewInDeviceFragment.visibility = View.GONE
        viewBinding.rvStatusInDeviceFragment.visibility = View.GONE
    }

    private fun setupRecyclerViewGroups() {
        groupsDevicesAdapter = GroupsDevicesAdapter(
            requireActivity(),
            this@DevicesFragment,
            resultDevice,
            this@DevicesFragment
        )
        viewBinding.rvGroupInDeviceFragment.apply {
            adapter = groupsDevicesAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    private fun setupRecyclerViewAllStatus(resultDevices: ArrayList<DeviceX>) {
        allStatusDeviceAdapter =
            AllStatusDeviceAdapter(requireActivity(), this@DevicesFragment, resultDevices)
        viewBinding.rvStatusInDeviceFragment.apply {
            adapter = allStatusDeviceAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    private fun showProgress() {
        viewBinding.progressInDeviceFragment.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        viewBinding.progressInDeviceFragment.visibility = View.GONE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemGroupClick(id: Int) {
        getAllGroup(id, "")
        if (viewBinding.rvGroupInDeviceFragment.visibility == View.VISIBLE) {
            groupsDevicesAdapter = GroupsDevicesAdapter(
                requireActivity(),
                this@DevicesFragment,
                resultAllDevice,
                this@DevicesFragment
            )
            if (resultAllDevice.size == 0) {
                toast(
                    requireActivity(),
                    requireActivity().getString(R.string.toast_result_search_group)
                )
            }
        }
    }

    override fun onItemAllStatusClick(id: String) {
        showProgress()
        deviceID = sharedPreferences.getString(Constants.DEVICE_ID, "").toString()
        if (isOnlineDevice) {
                getStatusDevices(id, "online",deviceID)
                if (resultDevice.size == 0) {
                    toast(
                        requireActivity(),
                        requireActivity().getString(R.string.toast_result_search_group)
                    )
                }
            } else if (isOfflineDevice) {
                getStatusDevices(id, "offline",deviceID)
                if (resultDevice.size == 0) {
                    toast(
                        requireActivity(),
                        requireActivity().getString(R.string.toast_result_search_group)
                    )
                }
            } else {
                getStatusDevices(id, "",deviceID)
                if (resultDevice.size == 0) {
                    toast(
                        requireActivity(),
                        requireActivity().getString(R.string.toast_result_search_group)
                    )
                }
            }
    }
    private fun getStatusDevices(id: String, status: String,deviceID : String) {
        if (resultDevice.size != 0) {
            resultDevice.clear()
        }
        for (i in resultXXX) {
            if (id == i.id.toString()) {
                for (j in i.devices) {
                    if (deviceID != ""){
                        if (deviceID == j.id.toString()){
                            if (status == "") {
                                resultDevice.add(j)
                            } else {
                                if (status == j.data!!.connection_status) {
                                    resultDevice.add(j)
                                }
                            }
                        }
                    } else {
                        if (status == "") {
                            resultDevice.add(j)
                        } else {
                            if (status == j.data!!.connection_status) {
                                resultDevice.add(j)
                            }
                        }
                    }

                }
            }
        }
        AllStatusDeviceAdapter(requireActivity(), this@DevicesFragment, resultDevice)
        val config: android.content.res.Configuration? =
            requireContext().resources.configuration
        val onlineTextDrawable = TextDrawable(requireContext())
        val offlineTextDrawable = TextDrawable(requireContext())
        var resultList : String = ""
        if (deviceID != ""){
            if (resultDevice.isNotEmpty() && resultDevice[0].data?.connection_status?.isNotEmpty() == true){
                resultList = resultDevice[0].data!!.connection_status.toString()
                if (resultList == "online"){
                    if (config!!.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                        onlineTextDrawable.setText("۱")
                        offlineTextDrawable.setText("۰")
                        viewBinding.btnStatusOnlineInDeviceFragment.icon = onlineTextDrawable
                        viewBinding.btnStatusOfflineInDeviceFragment.icon = offlineTextDrawable
                    } else {
                        onlineTextDrawable.setText("1")
                        offlineTextDrawable.setText("0")
                        viewBinding.btnStatusOnlineInDeviceFragment.icon = onlineTextDrawable
                        viewBinding.btnStatusOfflineInDeviceFragment.icon = offlineTextDrawable
                    }
                } else if (resultList == "offline"){
                    if (config!!.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                        offlineTextDrawable.setText("۱")
                        onlineTextDrawable.setText("۰")
                        viewBinding.btnStatusOfflineInDeviceFragment.icon = offlineTextDrawable
                        viewBinding.btnStatusOnlineInDeviceFragment.icon = onlineTextDrawable
                    } else {
                        offlineTextDrawable.setText("1")
                        onlineTextDrawable.setText("0")
                        viewBinding.btnStatusOnlineInDeviceFragment.icon = onlineTextDrawable
                        viewBinding.btnStatusOfflineInDeviceFragment.icon = offlineTextDrawable
                    }
                }
            }
        } else {
            showNumberStatusResult(deviceID)
        }



        hideProgress()
    }


    private fun showNumberStatusResult(deviceID: String){
        var onlineDevice: ArrayList<DeviceX> = arrayListOf()
        var offlineDevice: ArrayList<DeviceX> = arrayListOf()

        for (i in resultXXX){
            for (j in i.devices){
                if(deviceID != ""){
                    if (deviceID == j.id.toString()){
                        if (j.data!!.connection_status == "online"){
                            onlineDevice.add(j)
                        }
                        if (j.data!!.connection_status == "offline"){
                            offlineDevice.add(j)
                        }
                    }
                } else {
                    if (j.data!!.connection_status == "online"){
                        onlineDevice.add(j)
                    }
                    if (j.data!!.connection_status == "offline"){
                        offlineDevice.add(j)
                    }
                }

            }
        }
        val config: android.content.res.Configuration? =
            requireContext().resources.configuration
        val onlineTextDrawable = TextDrawable(requireContext())
        /* val onlineBackgroundDrawable = ResourcesCompat.getDrawable(resources, R.drawable.white_circle, null)
         onlineBackgroundDrawable?.setBounds(0, 0, onlineTextDrawable.intrinsicWidth, onlineTextDrawable.intrinsicHeight)*/


        val offlineTextDrawable = TextDrawable(requireContext())
        /*   val offlineBackgroundDrawable = ResourcesCompat.getDrawable(resources, R.drawable.white_circle, null)
           offlineBackgroundDrawable?.setBounds(0, 0, offlineTextDrawable.intrinsicWidth, offlineTextDrawable.intrinsicHeight)*/

        if (config!!.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            onlineTextDrawable.setText(Constants.setPersianNumbers(onlineDevice.size.toString())
                .toString())
            viewBinding.btnStatusOnlineInDeviceFragment.icon = onlineTextDrawable
            //  onlineTextDrawable.setBackground(onlineBackgroundDrawable)
            offlineTextDrawable.setText(Constants.setPersianNumbers(offlineDevice.size.toString())
                .toString())
            //  offlineTextDrawable.setBackground(offlineBackgroundDrawable)
            viewBinding.btnStatusOfflineInDeviceFragment.icon = offlineTextDrawable
        } else {
            onlineTextDrawable.setText(onlineDevice.size.toString())
            viewBinding.btnStatusOnlineInDeviceFragment.icon = onlineTextDrawable
            offlineTextDrawable.setText(offlineDevice.size.toString())
            viewBinding.btnStatusOfflineInDeviceFragment.icon = offlineTextDrawable
        }
    }



    private fun getAllGroup(id: Int, status: String) {
        if (resultAllDevice.size != 0) {
            resultAllDevice.clear()
        }
        for (i in resultAllGroup) {
            if (id == i.id) {
                for (j in i.devices) {
                    if (status == "") {
                        resultAllDevice.add(j)
                    } else {
                        if (status == j.data!!.connection_status) {
                            resultAllDevice.add(j)
                        }
                    }
                }
                AllStatusDeviceAdapter(requireActivity(), this@DevicesFragment, resultAllDevice)

            }
        }
        hideProgress()
    }

    override fun onItemDetailClick(id: String, name: String, latitude: Double, longitude: Double) {
        sharedPreferences.edit()
            .putString(Constants.DEVICE_ID, id)
            .apply()
        sharedPreferences.edit()
            .putString(Constants.DEVICE_NAME, name)
            .apply()
        sharedPreferences.edit()
            .putString(Constants.LATITUDE, latitude.toString())
            .apply()
        sharedPreferences.edit()
            .putString(Constants.LONGITUDE, longitude.toString())
            .apply()
        /*viewBinding.rvGroupInDeviceFragment.visibility = View.GONE
        viewBinding.etSearchGroupInFragmentDevice.visibility = View.GONE
        viewBinding.txtStatusDeviceInDeviceFragment.visibility = View.VISIBLE
        viewBinding.lnThreeViewInDeviceFragment.visibility = View.VISIBLE
        viewBinding.rvStatusInDeviceFragment.visibility = View.VISIBLE
        viewBinding.txtShowGroupInDeviceFragment.text = name
        viewBinding.ivDeleteDeviceNameInDeviceFragment.visibility = View.VISIBLE*/
        listener?.onEventTriggered()
    }

    fun setListener(listener: MyEventListener) {
        this.listener = listener
    }

}