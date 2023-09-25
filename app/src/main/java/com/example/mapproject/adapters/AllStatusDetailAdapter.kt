package com.example.mapproject.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mapproject.R
import com.example.mapproject.databinding.LayoutAllDetailStatusItemsBinding
import com.example.mapproject.model.devices.DeviceX
import ir.smartlab.persiandatepicker.util.PersianCalendar
import java.text.SimpleDateFormat
import java.util.*

class AllStatusDetailAdapter(private val context: Context)
    : RecyclerView.Adapter<AllStatusDetailAdapter.AllDetailStatusViewHolder>() {

    private val resultList: ArrayList<DeviceX> = arrayListOf()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllDetailStatusViewHolder {
        val binding = LayoutAllDetailStatusItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AllDetailStatusViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return resultList.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: AllDetailStatusViewHolder, position: Int) {
       // holder.bind(resultList[position])
        val item = resultList[position]

        holder.binding.txtDeviceNameInShowStatusDeviceInAllDetailItems.text = item.name

        val config: android.content.res.Configuration? =
            context.resources.configuration

        if (config!!.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val inputCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            inputCalendar.time = dateFormat.parse(item.data!!.connected_at!!)!!
            val calendarFA = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"), Locale("fa", "IR"))
            calendarFA.timeInMillis = inputCalendar.timeInMillis
            val persianCalendar = PersianCalendar()
            persianCalendar.timeInMillis = calendarFA.timeInMillis
            val newTimeZone = TimeZone.getTimeZone("Asia/Tehran")
            persianCalendar.timeZone = newTimeZone
            val lastDate = setPersianNumbers(persianCalendar.persianShortDateTime)
            holder.binding.txtConnectAtInShowStatusDeviceInAllDetailItems.text= lastDate
        } else {
            holder.binding.txtConnectAtInShowStatusDeviceInAllDetailItems.text = item.data!!.connected_at
        }


        if (item.data.connection_status == "online"){
            holder.binding.ivShowStatusDeviceInAllDetailItems.background = context.getDrawable(R.drawable.icon_online_status_in_adapter)
        } else if (item.data.connection_status == "offline"){
            holder.binding.ivShowStatusDeviceInAllDetailItems.background = context.getDrawable(R.drawable.icon_offline_status_in_adapter)
        }
    }



    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<DeviceX>) {
        this.resultList.clear()
        this.resultList.addAll(list)
        notifyDataSetChanged()
    }


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



    inner class AllDetailStatusViewHolder(val binding: LayoutAllDetailStatusItemsBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(deviceX: DeviceX){

        }
    }
}