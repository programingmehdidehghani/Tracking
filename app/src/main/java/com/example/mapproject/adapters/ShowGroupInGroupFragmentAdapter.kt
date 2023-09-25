package com.example.mapproject.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mapproject.R
import com.example.mapproject.databinding.LayoutShowGroupInGroupFragmentItemBinding
import com.example.mapproject.model.devices.ResultXXX
import com.example.mapproject.util.toast
import java.util.ArrayList


interface OnItemClickGroupFragmentCallback {
    fun onItemDeleteClick(id: String, position: Int)

    fun onItemEditCLick(id: String,position: Int,name: String)
}
class ShowGroupInGroupFragmentAdapter(private val context: Context,private val onItemClickCallback: OnItemClickGroupFragmentCallback) : RecyclerView.Adapter<ShowGroupInGroupFragmentAdapter.ShowGroupInGroupViewHolder>() {

    private val resultList: ArrayList<ResultXXX> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowGroupInGroupViewHolder {
        val binding = LayoutShowGroupInGroupFragmentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ShowGroupInGroupViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    override fun onBindViewHolder(holder: ShowGroupInGroupViewHolder, position: Int) {
        holder.bind(resultList[position],onItemClickCallback)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<ResultXXX>) {
        this.resultList.clear()
        this.resultList.addAll(list)
    }


    inner class ShowGroupInGroupViewHolder(private val binding: LayoutShowGroupInGroupFragmentItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(result: ResultXXX,onItemClickCallback: OnItemClickGroupFragmentCallback){
            if (result.name == "default"){
                binding.txtShowGroupInGroupFragmentItem.text = "پیش فرض"
                binding.ivDeletedGroupInGroupFragmentItem.setOnClickListener {
                    toast(context,context.getString(R.string.toast_permission_delete_group))
                }
                binding.ivEditGroupInGroupFragmentItem.setOnClickListener {
                    toast(context,context.getString(R.string.toast_permission_edit_group))
                }
            } else {
                binding.txtShowGroupInGroupFragmentItem.text = result.name
                binding.ivDeletedGroupInGroupFragmentItem.setOnClickListener {
                    onItemClickCallback.onItemDeleteClick(
                        result.id.toString(),
                        adapterPosition
                    )
                }

                binding.ivEditGroupInGroupFragmentItem.setOnClickListener {
                    onItemClickCallback.onItemEditCLick(
                        result.id.toString(),
                        adapterPosition,
                        result.name.toString()
                    )
                }
            }


        }
    }
}
