package com.example.mapproject.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapproject.databinding.LayoutAllStatusDevicesItemBinding
import com.example.mapproject.model.devices.DeviceX
import com.example.mapproject.model.devices.ResultXXX
import java.util.ArrayList


interface OnItemAllStatusClick {
    fun onItemAllStatusClick(id: String)
}

class AllStatusDeviceAdapter(
    private val context: Context, private val onItemAllStatusClick: OnItemAllStatusClick,
    private val resultDevice: ArrayList<DeviceX>,
) : RecyclerView.Adapter<AllStatusDeviceAdapter.AllStatusDeviceViewHolder>() {

    private val resultList: ArrayList<ResultXXX> = arrayListOf()
    private lateinit var allDetailStatusAdapter: AllStatusDetailAdapter
    private var isViewVisible = true

    @SuppressLint("NotifyDataSetChanged")
    fun setViewVisibility(isVisible: Boolean) {
        isViewVisible = isVisible
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllStatusDeviceViewHolder {
        val binding = LayoutAllStatusDevicesItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AllStatusDeviceViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    private var isExpended = false
    override fun onBindViewHolder(holder: AllStatusDeviceViewHolder, position: Int) {
        //val holder1 = holder.bind(resultList[position], onItemAllStatusClick)
        val item = resultList[position]

        if (item.name == "default") {
            holder.binding.txtAllStatusInItemDeviceAdapter.text = "پیش فرض"
        } else {
            holder.binding.txtAllStatusInItemDeviceAdapter.text = item.name
        }

        if (isViewVisible) {
            holder.binding.rvAllStatusInItemDeviceAdapter.visibility = View.VISIBLE
        } else {
            holder.binding.rvAllStatusInItemDeviceAdapter.visibility = View.GONE
        }

        holder.binding.cvInAllStatusInItemDeviceAdapter.setOnClickListener {
            if (isExpended) {
                holder.binding.rvAllStatusInItemDeviceAdapter.visibility = View.GONE
                isExpended = false
            } else {
                holder.binding.rvAllStatusInItemDeviceAdapter.visibility = View.VISIBLE
                isExpended = true
            }
            onItemAllStatusClick.onItemAllStatusClick(item.id.toString())
            allDetailStatusAdapter = AllStatusDetailAdapter(context)
            holder.binding.rvAllStatusInItemDeviceAdapter.apply {
                hasFixedSize()
                adapter = allDetailStatusAdapter
                layoutManager = LinearLayoutManager(context)
            }
            allDetailStatusAdapter.updateList(resultDevice)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<ResultXXX>) {
        this.resultList.clear()
        this.resultList.addAll(list)
        notifyDataSetChanged()
    }


    inner class AllStatusDeviceViewHolder(val binding: LayoutAllStatusDevicesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /*  @SuppressLint("NotifyDataSetChanged")
          fun bind(result: ResultXXX, onItemAllStatusClick: OnItemAllStatusClick) {


          }*/

    }

}