package com.example.mapproject.ui.dialogs

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mapproject.databinding.DialogPhoneMobileBinding
import com.example.mapproject.model.changePhoneMobile.ConfirmPhoneMobileRequest
import com.example.mapproject.model.changePhoneMobile.PhoneMobileRequest
import com.example.mapproject.ui.phoneNumber.PhoneMobileViewModel
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import com.example.mapproject.util.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PhoneMobileDialog : DialogFragment() {

    private lateinit var binding: DialogPhoneMobileBinding

    private lateinit var viewModel: PhoneMobileViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var userID: String
    private lateinit var token: String
    private lateinit var phoneNumber: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogPhoneMobileBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog!!.window ?: return
        val params = window.attributes
        params.width = 700
        params.height = 800
        viewModel = ViewModelProvider(requireActivity())[PhoneMobileViewModel::class.java]
        userID = sharedPreferences.getString(Constants.KEY_USER_ID,"").toString()
        token = sharedPreferences.getString(Constants.KEY_TOKEN,"").toString()
        binding.etFirstConfirmInDialogPhoneMobile.addTextChangedListener { editable ->
            editable.let {
                binding.etSecondConfirmInDialogPhoneMobile.requestFocus()
            }
            if (editable.toString() == ""){
                binding.etFirstConfirmInDialogPhoneMobile.requestFocus()
            }
        }
        binding.etSecondConfirmInDialogPhoneMobile.addTextChangedListener { editable ->
            editable.let {
                binding.etThirdConfirmInDialogPhoneMobile.requestFocus()
            }
            if (editable.toString() == ""){
                binding.etFirstConfirmInDialogPhoneMobile.requestFocus()
            }
        }
        binding.etThirdConfirmInDialogPhoneMobile.addTextChangedListener { editable ->
            editable.let {
                binding.etFourthConfirmInDialogPhoneMobile.requestFocus()
            }
            if (editable.toString() == ""){
                binding.etSecondConfirmInDialogPhoneMobile.requestFocus()
            }
        }
        binding.etFourthConfirmInDialogPhoneMobile.addTextChangedListener { editable ->
            editable.let {
                binding.etFifthConfirmInDialogPhoneMobile.requestFocus()
            }
            if (editable.toString() == ""){
                binding.etThirdConfirmInDialogPhoneMobile.requestFocus()
            }
        }
        binding.etFifthConfirmInDialogPhoneMobile.addTextChangedListener { editable ->
            editable.let {
                binding.etSixthConfirmInDialogPhoneMobile.requestFocus()
            }
            if (editable.toString() == ""){
                binding.etFourthConfirmInDialogPhoneMobile.requestFocus()
            }
        }
        binding.etSixthConfirmInDialogPhoneMobile.addTextChangedListener { editable ->
            if (editable.toString() == ""){
                binding.etFifthConfirmInDialogPhoneMobile.requestFocus()
            }
        }
        resultPhoneMobile()
        resultConfirmPhoneMobile()
    }

    private fun resultConfirmPhoneMobile(){
        binding.btnConfirmVerifyCodeInDialogChangePass.setOnClickListener {
            if (binding.etFirstConfirmInDialogPhoneMobile.text.toString().isNotEmpty() &&
                binding.etSecondConfirmInDialogPhoneMobile.text.toString().isNotEmpty() &&
                binding.etThirdConfirmInDialogPhoneMobile.text.toString().isNotEmpty() &&
                binding.etFourthConfirmInDialogPhoneMobile.text.toString().isNotEmpty() &&
                binding.etFifthConfirmInDialogPhoneMobile.text.toString().isNotEmpty() &&
                binding.etSixthConfirmInDialogPhoneMobile.text.toString().isNotEmpty()){
                val verifyCode = binding.etFirstConfirmInDialogPhoneMobile.text.toString() + binding.etSecondConfirmInDialogPhoneMobile.text.toString() +
                   binding.etThirdConfirmInDialogPhoneMobile.text.toString() + binding.etFourthConfirmInDialogPhoneMobile.text.toString() +
                          binding.etFifthConfirmInDialogPhoneMobile.text.toString() + binding.etSixthConfirmInDialogPhoneMobile.text.toString()
                val confirmPhoneMobileRequest = ConfirmPhoneMobileRequest(
                    verifyCode,
                    phoneNumber
                )
                viewModel.getResultConfirmPhoneMobile("Bearer $token",userID,"fa","asia_tehran","celsius","km","liter",confirmPhoneMobileRequest)
                showProgress()
                viewModel.getResultConfirmPhoneNumber.observe(viewLifecycleOwner, Observer { response ->
                    when(response){
                        is Resource.Success -> {
                            hideProgress()
                            response.data.let {
                                toast(requireContext(), it.message)
                                this.dismiss()
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

            } else {
                toast(requireContext(), "کد تآیید را کامل وارد کنید")
            }
        }
    }

    private fun resultPhoneMobile(){
        binding.btnConfirmPassInDialogChangePass.setOnClickListener {
            if (binding.etPhoneNumberInDialogPhoneMobile.text.toString().isNotEmpty()){
                phoneNumber = binding.etPhoneNumberInDialogPhoneMobile.text.toString()
                val phoneMobileRequest = PhoneMobileRequest(
                    phoneNumber
                )
                viewModel.getResultPhoneMobile("Bearer $token",userID,"fa","asia_tehran","celsius","km","liter",phoneMobileRequest)
                showProgress()
                viewModel.getResultPhoneMobile.observe(viewLifecycleOwner, Observer { response ->
                    when(response){
                        is Resource.Success -> {
                            hideProgress()
                            response.data.let {
                                toast(requireContext(), it.message)
                                binding.etPhoneNumberInDialogPhoneMobile.visibility = View.GONE
                                binding.etFirstConfirmInDialogPhoneMobile.visibility = View.VISIBLE
                                binding.etSecondConfirmInDialogPhoneMobile.visibility = View.VISIBLE
                                binding.etThirdConfirmInDialogPhoneMobile.visibility = View.VISIBLE
                                binding.etFourthConfirmInDialogPhoneMobile.visibility = View.VISIBLE
                                binding.etFifthConfirmInDialogPhoneMobile.visibility = View.VISIBLE
                                binding.etSixthConfirmInDialogPhoneMobile.visibility = View.VISIBLE
                                binding.btnConfirmPassInDialogChangePass.visibility = View.GONE
                                binding.btnConfirmVerifyCodeInDialogChangePass.visibility = View.VISIBLE
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
            }  else {
                toast(requireContext(), "شماره موبایل خود را وارد کنید")
            }
        }
    }



    private fun showProgress() {
        binding.progressInDialogPhoneMobile.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressInDialogPhoneMobile.visibility = View.GONE
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireActivity(), theme) {
            override fun onBackPressed() {
                this.dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.dismiss()
    }

    override fun onDetach() {
        super.onDetach()
        this.dismiss()
    }
}