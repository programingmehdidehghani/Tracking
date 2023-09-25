package com.example.mapproject.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mapproject.R
import com.example.mapproject.databinding.FragmentProfileBinding
import com.example.mapproject.model.*
import com.example.mapproject.model.changeInformationProfile.ChangeProfileInformationRequest
import com.example.mapproject.model.changeInformationProfile.SettingsX
import com.example.mapproject.model.changeInformationProfile.UnitsXX
import com.example.mapproject.model.constant.ResultXX
import com.example.mapproject.ui.dialogs.ChangePassDialog
import com.example.mapproject.ui.dialogs.PhoneMobileDialog
import com.example.mapproject.ui.viewModels.ProfileViewModel
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@Suppress("DEPRECATION")
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private lateinit var profileViewModel: ProfileViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var userID: String
    private lateinit var token: String
    private lateinit var tokenID: String
    lateinit var constant: ResultXX
    private lateinit var capacity: String
    private lateinit var distance: String
    private lateinit var temperature: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]
        constant = requireActivity().intent.extras?.getParcelable("constant")!!

        userID = sharedPreferences.getString(Constants.KEY_USER_ID,"").toString()
        token = sharedPreferences.getString(Constants.KEY_TOKEN,"").toString()
        tokenID = sharedPreferences.getString(Constants.KEY_TOKEN_ID,"").toString()

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
                        if (it.result.username!!.isNotEmpty()){
                            binding.etUserNameInProfileFragment.hint = it.result.username
                        }
                        if (it.result.name!!.isNotEmpty()){
                            binding.etNameInProfileFragment.hint = it.result.name
                        }
                        if (it.result.email.toString() != "null"){
                            binding.etEmailInProfileFragment.hint = it.result.email.toString()
                        }
                        if (it.result.mobile!!.isNotEmpty()){
                            binding.etPhoneNumberInProfileFragment.hint = it.result.mobile
                        }
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
        locationSpinner()
        mapSpinner()
        radioButtonCapacity()
        radioButtonTemperature()
        radioButtonDistance()
    }

    private fun locationSpinner(){
        val langAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), R.layout.spinner_text,constant.timezones)
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_location)
        binding.spinnerLocationInProfileFragment.adapter = langAdapter
    }

    private fun mapSpinner(){
        val langAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), R.layout.spinner_text,constant.maps)
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_location)
        binding.spinnerMapInFragmentProfile.adapter = langAdapter
    }

    @SuppressLint("ResourceAsColor")
    private fun radioButtonCapacity(){
        for(i in constant.units.capacity){
            val radioButton = RadioButton(requireContext())
            radioButton.text = i
            radioButton.setTextColor(R.color.whiteLow)
            binding.radioCapacityInProfileFragment.addView(radioButton)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun radioButtonTemperature(){
        for(i in constant.units.temperature){
            val radioButton = RadioButton(requireContext())
            radioButton.text = i
            radioButton.setTextColor(R.color.whiteLow)
            binding.radioTemperatureInProfileFragment.addView(radioButton)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun radioButtonDistance(){
        for(i in constant.units.distance){
            val radioButton = RadioButton(requireContext())
            radioButton.text = i
            radioButton.setTextColor(R.color.whiteLow)
            binding.radioDistanceInProfileFragment.addView(radioButton)
        }
    }


    private fun showProgress() {
        binding.progressInDashboardActivity.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressInDashboardActivity.visibility = View.GONE
    }

}