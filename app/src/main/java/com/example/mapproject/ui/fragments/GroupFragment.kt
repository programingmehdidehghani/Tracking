package com.example.mapproject.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mapproject.R
import com.example.mapproject.adapters.OnItemClickGroupFragmentCallback
import com.example.mapproject.adapters.ShowGroupInGroupFragmentAdapter
import com.example.mapproject.databinding.FragmentGroupBinding
import com.example.mapproject.model.SendNameToServer
import com.example.mapproject.model.devices.ResultXXX
import com.example.mapproject.ui.viewModels.GroupViewModel
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import com.example.mapproject.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject

@AndroidEntryPoint
class GroupFragment : Fragment(), OnItemClickGroupFragmentCallback {


    private var _binding: FragmentGroupBinding? = null
    private val viewBinding get() = _binding!!
    private lateinit var groupsFragment: GroupViewModel
    private var resultXXX: ArrayList<ResultXXX> = arrayListOf()
    private lateinit var showGroupInGroupFragmentAdapter: ShowGroupInGroupFragmentAdapter

    private lateinit var userID: String
    private lateinit var token: String
    private lateinit var tokenID: String
    private var isEdit: Boolean = false
    private var idItem: String = ""


    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        groupsFragment = ViewModelProvider(requireActivity())[GroupViewModel::class.java]
        userID = sharedPreferences.getString(Constants.KEY_USER_ID, "").toString()
        token = sharedPreferences.getString(Constants.KEY_TOKEN, "").toString()
        tokenID = sharedPreferences.getString(Constants.KEY_TOKEN_ID, "").toString()
        setupRecyclerView()
        getGroupList()
        addGroup()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addGroup() {
        viewBinding.ivAddGroupInGroupFragment.setOnClickListener {
            if (!isEdit) {
                val sendNameToServer = SendNameToServer(
                    viewBinding.etAddGroupInGroupFragment.text.toString()
                )

                if (viewBinding.etAddGroupInGroupFragment.text.toString().isNotEmpty()) {
                    groupsFragment.addGroups(
                        "Bearer $token",
                        userID,
                        "fa",
                        "asia_tehran",
                        "celsius",
                        "km",
                        "liter",
                        sendNameToServer
                    )
                    groupsFragment.addGroups.observe(viewLifecycleOwner, Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                hideProgress()
                                getGroupList()
                            }
                            is Resource.Error -> {
                                hideProgress()
                            }
                            is Resource.Loading -> {
                                showProgress()
                            }
                            else -> {}
                        }
                    })


                } else {
                    toast(
                        requireActivity(),
                        requireActivity().getString(R.string.toast_enter_name_group)
                    )
                }
            } else {
                editGroup(idItem)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun editGroup(id: String){
        val sendNameToServer = SendNameToServer(
            viewBinding.etAddGroupInGroupFragment.text.toString()
        )
        groupsFragment.editGroup(
            "Bearer $token", id, "fa", "asia_tehran", "celsius", "km", "liter",
            sendNameToServer
        )
        groupsFragment.editGroup.observeForever { response ->
            when (response) {
                is Resource.Success -> {
                    response.data.let {
                        getGroupList()
                        hideProgress()
                        isEdit = false
                        viewBinding.ivAddGroupInGroupFragment.setImageDrawable(requireActivity().getDrawable(R.drawable.baseline_add_circle))
                        viewBinding.etAddGroupInGroupFragment. hint = requireActivity().getText(R.string.et_name_group)
                    }
                }
                is Resource.Error -> {
                    hideProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
                else -> {}
            }
        }
    }

    private fun getGroupList(){
        groupsFragment.getDevices(
            "Bearer $token", userID, "1", "", "fa", "asia_tehran", "celsius", "km", "liter"
        )
        groupsFragment.getDevices.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgress()
                    response.data.let {
                        if (resultXXX.size != 0) {
                            resultXXX.clear()
                        }
                        resultXXX.addAll(it.result)
                        showGroupInGroupFragmentAdapter.notifyDataSetChanged()
                        showGroupInGroupFragmentAdapter.updateList(resultXXX)
                        viewBinding.rvGroupsInGroupFragment.visibility = View.VISIBLE
                    }
                }
                is Resource.Error -> {
                    hideProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
                else -> {}
            }
        })
    }

    private fun setupRecyclerView(){
        showGroupInGroupFragmentAdapter = ShowGroupInGroupFragmentAdapter(requireActivity(),this@GroupFragment)
        viewBinding.rvGroupsInGroupFragment.apply {
            adapter = showGroupInGroupFragmentAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    private fun showProgress() {
        viewBinding.progressInGroupFragment.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        viewBinding.progressInGroupFragment.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onItemDeleteClick(id: String, position: Int) {
        groupsFragment.deletedGroup(
            "Bearer $token", id, "fa", "asia_tehran", "celsius", "km", "liter"
        )
        groupsFragment.deletedGroup.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data.let {
                        if (resultXXX.isNotEmpty()) {
                            if (resultXXX.size > position) {
                                resultXXX.removeAt(position)
                                if (resultXXX.isNotEmpty()) {
                                    showGroupInGroupFragmentAdapter.notifyItemRemoved(position)
                                    showGroupInGroupFragmentAdapter.updateList(resultXXX)
                                    Log.i("sizeDelete", "is delete .....")
                                }
                            }
                        }
                        viewBinding.ivAddGroupInGroupFragment.setImageDrawable(
                            requireContext().getDrawable(
                                R.drawable.baseline_add_circle
                            )
                        )
                        // viewBinding.etAddGroupInGroupFragment.hint = requireContext().getText(R.string.et_name_group)
                        hideProgress()
                        getGroupList()
                    }
                }
                is Resource.Error -> {
                    hideProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
                else -> {}
            }
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onItemEditCLick(id: String, position: Int, name: String) {
        viewBinding.etAddGroupInGroupFragment.setText(name)
        viewBinding.ivAddGroupInGroupFragment.setImageDrawable(requireActivity().getDrawable(R.drawable.baseline_mode_edit_outline_24))
        isEdit = true
        idItem = id
    }


}