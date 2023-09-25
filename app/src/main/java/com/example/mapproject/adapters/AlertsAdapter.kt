package com.example.mapproject.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mapproject.R
import com.example.mapproject.databinding.LayoutListAlertsAdapterBinding
import com.example.mapproject.model.alerts.Data

class AlertsAdapter(private val context: Context) : RecyclerView.Adapter<AlertsAdapter.AlertsViewHolder>() {


    private val alertsList: ArrayList<Data> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertsViewHolder {
        val binding = LayoutListAlertsAdapterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AlertsViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AlertsViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }


    inner class AlertsViewHolder(private val binding: LayoutListAlertsAdapterBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind(data: Data){
            binding.txtDeviceDetailNameInListAlertsAdapter.text = data.device?.name
            binding.txtDeviceDetailImeiInListAlertsAdapter.text = data.device?.imei
            val type = translateTypeDevice(data.type.toString())
            binding.txtDeviceDetailTypeInListAlertsAdapter.text = type
            val part: List<String> = data.created_at!!.split(" ")
            val date = part[0]
            binding.txtDeviceDetailSendTimeInListAlertsAdapter.text = date
            binding.txtDeviceDetailLocationInListAlertsAdapter.text = data.location?.latitude.toString() + " , " + data.location?.longitude.toString()
            if (data.read_at != null){
                binding.txtDeviceDetailStatusInListAlertsAdapter.text = context.getText(R.string.read_at)
                binding.ivUnreadStatusInListAdapter.visibility = View.GONE
            }else {
                binding.txtDeviceDetailStatusInListAlertsAdapter.text = context.getText(R.string.unread_at)
                binding.ivUnreadStatusInListAdapter.visibility = View.VISIBLE
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)


    private fun translateTypeDevice(itemName : String) : String {
        when(itemName){
            "over_speed" ->{
                return context.getString(R.string.over_speed)
            }
            "vibration" ->{
                return context.getString(R.string.vibration)
            }
            "low_battery" ->{
                return context.getString(R.string.low_battery)
            }
            "sos" ->{
                return context.getString(R.string.sos)
            }
            "fall_down" ->{
                return context.getString(R.string.fall)
            }
            "power_off" ->{
                return context.getString(R.string.power_off)
            }
            "power_on" ->{
                return context.getString(R.string.power_on)
            }
            "door" ->{
                return context.getString(R.string.door)
            }
            "geofence_enter" ->{
                return context.getString(R.string.geofence_enter)
            }
            "geofence_exit" ->{
                return context.getString(R.string.geofence_exit)
            }
            "accident" ->{
                return context.getString(R.string.accident)
            }
            "hard_acceleration" ->{
                return context.getString(R.string.hard_acceleration)
            }
            "hard_braking" ->{
                return context.getString(R.string.hard_braking)
            }
            "hard_cornering" ->{
                return context.getString(R.string.hard_cornering)
            }
            "power_cut" ->{
                return context.getString(R.string.power_cut)
            }
            "tampering" ->{
                return context.getString(R.string.tampering)
            }
            "acc_off" ->{
                return context.getString(R.string.acc_off)
            }
            "acc_on" ->{
                return context.getString(R.string.acc_on)
            }
        }
        return ""
    }




}