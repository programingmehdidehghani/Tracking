package com.example.mapproject.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mapproject.R
import com.example.mapproject.databinding.FragmentTrackingBinding
import com.example.mapproject.ui.dialogs.ChangePassDialog
import com.example.mapproject.ui.dialogs.PhoneMobileDialog
import com.example.mapproject.ui.viewModels.ProfileViewModel
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrackingFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel

    private var _binding: FragmentTrackingBinding? = null
    private val viewBinding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var userID: String
    private lateinit var token: String
    private lateinit var tokenID: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]
        userID = sharedPreferences.getString(Constants.KEY_USER_ID,"").toString()
        token = sharedPreferences.getString(Constants.KEY_TOKEN,"").toString()
        tokenID = sharedPreferences.getString(Constants.KEY_TOKEN_ID,"").toString()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        })
        //getProfile()
    }

    private fun getProfile(){
        profileViewModel.getProfile(userID, "Bearer $token","fa","asia_tehran","celsius","km","liter")
        profileViewModel.getProfile.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success ->{
                    hideProgress()
                    response.data.let {
                        if (it.result.must_change_password!!){
                            val fm: FragmentManager = requireActivity().supportFragmentManager
                            val dialog = ChangePassDialog()
                            dialog.show(fm,"")
                        } else {
                            if (it.result.mobile!!.isEmpty()){
                                val fm: FragmentManager = requireActivity().supportFragmentManager
                                val dialog = PhoneMobileDialog()
                                dialog.show(fm,"")
                            }
                        }
                        sharedPreferences.edit()
                            .putString(Constants.MOBILE_NUMBER, it.result.mobile)
                            .apply()
                        sharedPreferences.edit()
                            .putString(Constants.NAME, it.result.name)
                            .apply()
                        sharedPreferences.edit()
                            .putString(Constants.EMAIL, it.result.email.toString())
                            .apply()
                        sharedPreferences.edit()
                            .putString(Constants.USERNAME, it.result.username)
                            .apply()
                        sharedPreferences.edit()
                            .putString(Constants.CAPACITY, it.result.settings.units.capacity)
                            .apply()
                        sharedPreferences.edit()
                            .putString(Constants.TEMPERATURE, it.result.settings.units.temperature)
                            .apply()
                        sharedPreferences.edit()
                            .putString(Constants.DISTANCE, it.result.settings.units.distance)
                            .apply()
                        sharedPreferences.edit()
                            .putString(Constants.MAP, it.result.settings.map)
                            .apply()
                        sharedPreferences.edit()
                            .putString(Constants.TIME_ZONE, it.result.settings.timezone)
                            .apply()
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
        viewBinding.progressInTrackingFragment.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        viewBinding.progressInTrackingFragment.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}