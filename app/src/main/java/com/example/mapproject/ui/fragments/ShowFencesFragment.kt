package com.example.mapproject.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mapproject.R
import com.example.mapproject.adapters.FencesAdapter
import com.example.mapproject.adapters.OnItemClickCallback
import com.example.mapproject.databinding.FragmentShowFencesBinding
import com.example.mapproject.databinding.LayoutShowFenceDialogBinding
import com.example.mapproject.ui.dialogs.DrawPolyGonCircleSaveDialog
import com.example.mapproject.ui.dialogs.MyDialogListener
import com.example.mapproject.ui.viewModels.ShowFencesViewModel
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ShowFencesFragment : Fragment() , OnItemClickCallback , MyDialogListener {


    private var _binding: FragmentShowFencesBinding? = null
    private val viewBinding get() = _binding!!


    private lateinit var viewModel: ShowFencesViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var userID: String
    private lateinit var token: String

    private lateinit var fencesAdapter: FencesAdapter
    private var fencesList: ArrayList<com.example.mapproject.model.showFences.Result> = arrayListOf()
    private val listLock = Mutex()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowFencesBinding.inflate(inflater, container, false)
        return viewBinding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ShowFencesViewModel::class.java]
        userID = sharedPreferences.getString(Constants.KEY_USER_ID, "").toString()
        token = sharedPreferences.getString(Constants.KEY_TOKEN, "").toString()
        setupRecyclerView()
        getFences()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun getFences(){
        viewModel.getFences(
            "Bearer $token", userID, "fa", "asia_tehran", "celsius", "km", "liter"
        )
        viewModel.getFences.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data.let {
                        fencesList.clear()
                        fencesList.addAll(it.result)
                        if (fencesList.size != 0) {
                            fencesAdapter.updateList(fencesList)
                            viewBinding.txtShowDetailGlEndInShowFenceFragment.visibility = View.GONE
                            //fencesAdapter.notifyDataSetChanged()
                            viewBinding.rvShowFenceInGlEndInShowFenceFragment.visibility =
                                View.VISIBLE
                        } else {
                            viewBinding.rvShowFenceInGlEndInShowFenceFragment.visibility = View.GONE
                            viewBinding.txtShowDetailGlEndInShowFenceFragment.visibility =
                                View.VISIBLE
                        }
                        hideProgress()

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
        fencesAdapter = FencesAdapter(requireActivity(),this@ShowFencesFragment)
        viewBinding.rvShowFenceInGlEndInShowFenceFragment.apply {
            adapter = fencesAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    private fun showProgress() {
        viewBinding.progressInGlEndInShowFenceFragment.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        viewBinding.progressInGlEndInShowFenceFragment.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    override fun onDetach() {
        super.onDetach()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onItemDeleteClick(id: String, position: Int) {
        viewModel.deletedFences(
            "Bearer $token", id, "fa", "asia_tehran", "celsius", "km", "liter"
        )
        viewModel.deletedFences.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data.let {
                        if (fencesList.isNotEmpty()) {
                            if (position >= 0 && position < fencesList.size) { // add a check to make sure position is valid
                                fencesList.removeAt(position)
                                fencesAdapter.notifyItemRemoved(position)
                                if (fencesList.size != 0) {
                                    fencesAdapter.updateList(fencesList)
                                }
                            }
                        } else {
                            viewBinding.rvShowFenceInGlEndInShowFenceFragment.visibility = View.GONE
                            viewBinding.txtShowDetailGlEndInShowFenceFragment.visibility =
                                View.VISIBLE
                        }
                        getFences()
                        hideProgress()
                    }
                }
                is Resource.Error -> {
                    hideProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    override fun onItemEditCLick(id: String, position: Int,name: String,description: String,color: String) {
        val bundle = Bundle()
        bundle.putString("fencesId", id)
        bundle.putString("name", name)
        bundle.putString("description", description)
        bundle.putString("color", color)
        val dialog = DrawPolyGonCircleSaveDialog()
        dialog.setTargetFragment(this@ShowFencesFragment, 1)
        dialog.arguments = bundle
        dialog.listener = this
        dialog.show(requireFragmentManager(), "")
    }

    override fun onDialogDismissed() {
        getFences()
    }


    override fun onDialogShown() {
    }


}