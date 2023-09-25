package com.example.mapproject.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mapproject.databinding.FragmentCarBinding
import com.example.mapproject.ui.monitor.MonitorViewModel
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject


@AndroidEntryPoint
class CarFragment : Fragment() {


    private lateinit var binding: FragmentCarBinding

    private lateinit var monitorViewModel: MonitorViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var userID: String
    private lateinit var token: String
    private lateinit var tokenID: String


    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        monitorViewModel = ViewModelProvider(requireActivity())[MonitorViewModel::class.java]
        userID = sharedPreferences.getString(Constants.KEY_USER_ID, "").toString()
        token = sharedPreferences.getString(Constants.KEY_TOKEN, "").toString()
        tokenID = sharedPreferences.getString(Constants.KEY_TOKEN_ID, "").toString()
        monitorViewModel.getDevices(
            "Bearer $token", userID, "1", "", "fa", "asia_tehran", "celsius", "km", "liter"
        )
        monitorViewModel.getDevices.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgress()
                    response.data.let {
                        Log.i("device", it.result[0].devices.toString())
                        binding.txtShowKilometerInFragmentCar.text = it.result[0].devices[0].name
                        binding.txtShow1KilometerInFragmentCar.text = it.result[0].devices[0].updated_at
                        binding.txtSpeedOriginalInCarFragment.text = it.result[0].devices[0].data?.speed.toString()
                        binding.txtSpeed2OriginalInCarFragment.text = it.result[0].devices[0].data?.connection_status.toString()
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


    private fun showProgress() {
        binding.progressInCarFragment.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressInCarFragment.visibility = View.GONE
    }


}
