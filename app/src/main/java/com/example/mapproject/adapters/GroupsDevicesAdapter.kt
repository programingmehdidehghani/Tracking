package com.example.mapproject.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapproject.databinding.LayoutItemGroupAdapterBinding
import com.example.mapproject.model.devices.DeviceX
import com.example.mapproject.model.devices.ResultXXX
import java.util.ArrayList


interface OnItemGroupsClick {
    fun onItemGroupClick(id: Int)
}

class GroupsDevicesAdapter(
    private val context: Context, private val onItemClickCallback: OnItemGroupsClick,
    private val resultDevice: ArrayList<DeviceX>,private val onItemDetailClick: OnItemDetailClick
) : RecyclerView.Adapter<GroupsDevicesAdapter.GroupsViewHolder>() {

    private val resultList: ArrayList<ResultXXX> = arrayListOf()
    private lateinit var detailDevicesGroupAdapter: GroupDetailDevicesAdapter


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        val binding = LayoutItemGroupAdapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GroupsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        holder.bind(resultList[position], onItemClickCallback)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<ResultXXX>) {
        this.resultList.clear()
        this.resultList.addAll(list)
        notifyDataSetChanged()
    }


    inner class GroupsViewHolder(private val binding: LayoutItemGroupAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(result: ResultXXX, onItemClickCallback: OnItemGroupsClick) {

            if (result.name == "default"){
                binding.txtItemGroupInItemGroupAdapter.text = "پیش فرض"
            } else {
                binding.txtItemGroupInItemGroupAdapter.text = result.name
            }

            binding.clParentInItemGroupInItemGroupAdapter.setOnClickListener {
                 onItemClickCallback.onItemGroupClick(
                      result.id
                  )
                if (binding.rvDetailDeviceInItemsAdapter.visibility == View.VISIBLE) {
                    binding.rvDetailDeviceInItemsAdapter.visibility = View.GONE
                } else {
                    detailDevicesGroupAdapter = GroupDetailDevicesAdapter(onItemDetailClick)
                    binding.rvDetailDeviceInItemsAdapter.apply {
                        hasFixedSize()
                        adapter = detailDevicesGroupAdapter
                        layoutManager = LinearLayoutManager(context)
                    }
                    detailDevicesGroupAdapter.updateList(resultDevice)
                    binding.rvDetailDeviceInItemsAdapter.visibility = View.VISIBLE
                }
            }
        }
    }


}