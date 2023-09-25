package com.example.mapproject.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.mapproject.R
import com.example.mapproject.databinding.FragmentManageInformationBinding
import com.example.mapproject.ui.dialogs.ChangeEmailDialog
import com.example.mapproject.ui.dialogs.ChangePassDialog
import com.example.mapproject.ui.dialogs.PhoneMobileDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class InformationManagementFragment : Fragment() {


    private lateinit var binding: FragmentManageInformationBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnChangePassInDashboardActivity.setOnClickListener {
            val fm: FragmentManager = requireActivity().supportFragmentManager
            val dialog = ChangePassDialog()
            dialog.show(fm,"")
        }
        binding.btnChangePhoneNumberInDashboardActivity.setOnClickListener {
            val fm: FragmentManager = requireActivity().supportFragmentManager
            val dialog = PhoneMobileDialog()
            dialog.show(fm,"")
        }
        binding.btnChangeEmailInDashboardActivity.setOnClickListener {
            val fm: FragmentManager = requireActivity().supportFragmentManager
            val dialog = ChangeEmailDialog()
            dialog.show(fm,"")
        }

    }




}