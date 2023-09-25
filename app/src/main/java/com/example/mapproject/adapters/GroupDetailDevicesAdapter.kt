package com.example.mapproject.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mapproject.R
import com.example.mapproject.databinding.LayoutAllDetailStatusItemsBinding
import com.example.mapproject.databinding.LayoutDetailDeviceItemsBinding
import com.example.mapproject.model.devices.DeviceX
import ir.smartlab.persiandatepicker.util.PersianCalendar
import java.text.SimpleDateFormat
import java.util.*

interface OnItemDetailClick {
    fun onItemDetailClick(id: String,name: String,latitude: Double,longitude: Double)
}
class GroupDetailDevicesAdapter(private val onItemClickCallback: OnItemDetailClick): RecyclerView.Adapter<GroupDetailDevicesAdapter.DetailDevicesGroupViewHolder>() {


    private val resultList: ArrayList<DeviceX> = arrayListOf()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailDevicesGroupViewHolder {
        val binding = LayoutDetailDeviceItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DetailDevicesGroupViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    override fun onBindViewHolder(holder: DetailDevicesGroupViewHolder, position: Int) {
        holder.bind(resultList[position],onItemClickCallback)

    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<DeviceX>) {
        this.resultList.clear()
        this.resultList.addAll(list)
        notifyDataSetChanged()
    }



    inner class DetailDevicesGroupViewHolder(private val binding: LayoutDetailDeviceItemsBinding): RecyclerView.ViewHolder(binding.root){
            fun bind(deviceX: DeviceX,onItemClickCallback: OnItemDetailClick){
                   binding.txtDeviceNameInDetailItemDevice.text = deviceX.name
                   binding.clParentInDetailDeviceInDetailItem.setOnClickListener {
                       onItemClickCallback.onItemDetailClick(
                           deviceX.id.toString(),
                           deviceX.name.toString(),
                           deviceX.data!!.location.latitude.toString().toDouble(),
                           deviceX.data.location.longitude.toString().toDouble()
                       )
                   }
            }
    }

}