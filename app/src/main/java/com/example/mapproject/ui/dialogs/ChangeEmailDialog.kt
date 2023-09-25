package com.example.mapproject.ui.dialogs

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mapproject.databinding.DialogEmailBinding
import com.example.mapproject.model.changeEmail.EmailRequest
import com.example.mapproject.model.changeEmail.VerifyCodeEmailRequest
import com.example.mapproject.ui.changeEmail.ChangeEmailViewModel
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import com.example.mapproject.util.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChangeEmailDialog : DialogFragment() {


    private lateinit var binding: DialogEmailBinding

    private lateinit var viewModel: ChangeEmailViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var userID: String
    private lateinit var token: String
    private lateinit var email: String



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogEmailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog!!.window ?: return
        val params = window.attributes
        params.width = 700
        params.height = 1000
        viewModel = ViewModelProvider(requireActivity())[ChangeEmailViewModel::class.java]
        userID = sharedPreferences.getString(Constants.KEY_USER_ID,"").toString()
        token = sharedPreferences.getString(Constants.KEY_TOKEN,"").toString()
        binding.etFirstConfirmInDialogEmail.addTextChangedListener { editable ->
            editable.let {
                binding.etSecondConfirmInDialogEmail.requestFocus()
            }
            if (editable.toString() == ""){
                binding.etFirstConfirmInDialogEmail.requestFocus()
            }
        }
        binding.etSecondConfirmInDialogEmail.addTextChangedListener { editable ->
            editable.let {
                binding.etThirdConfirmInDialogEmail.requestFocus()
            }
            if (editable.toString() == ""){
                binding.etFirstConfirmInDialogEmail.requestFocus()
            }
        }
        binding.etThirdConfirmInDialogEmail.addTextChangedListener { editable ->
            editable.let {
                binding.etFourthConfirmInDialogEmail.requestFocus()
            }
            if (editable.toString() == ""){
                binding.etSecondConfirmInDialogEmail.requestFocus()
            }
        }
        binding.etFourthConfirmInDialogEmail.addTextChangedListener { editable ->
            editable.let {
                binding.etFifthConfirmInDialogEmail.requestFocus()
            }
            if (editable.toString() == ""){
                binding.etThirdConfirmInDialogEmail.requestFocus()
            }
        }
        binding.etFifthConfirmInDialogEmail.addTextChangedListener { editable ->
            editable.let {
                binding.etSixthConfirmInDialogEmail.requestFocus()
            }
            if (editable.toString() == ""){
                binding.etFourthConfirmInDialogEmail.requestFocus()
            }
        }
        binding.etSixthConfirmInDialogEmail.addTextChangedListener { editable ->
            if (editable.toString() == ""){
                binding.etFifthConfirmInDialogEmail.requestFocus()
            }
        }
        resultEmail()
        resultConfirmEmail()
    }

    private fun resultConfirmEmail(){
        binding.btnConfirmVerifyCodeInDialogEmail.setOnClickListener {
            if (binding.etFirstConfirmInDialogEmail.text.toString().isNotEmpty() &&
                binding.etSecondConfirmInDialogEmail.text.toString().isNotEmpty() &&
                binding.etThirdConfirmInDialogEmail.text.toString().isNotEmpty() &&
                binding.etFourthConfirmInDialogEmail.text.toString().isNotEmpty() &&
                binding.etFifthConfirmInDialogEmail.text.toString().isNotEmpty() &&
                binding.etSixthConfirmInDialogEmail.text.toString().isNotEmpty()){
                val verifyCode = binding.etFirstConfirmInDialogEmail.text.toString() + binding.etSecondConfirmInDialogEmail.text.toString() +
                        binding.etThirdConfirmInDialogEmail.text.toString() + binding.etFourthConfirmInDialogEmail.text.toString() +
                        binding.etFifthConfirmInDialogEmail.text.toString() + binding.etSixthConfirmInDialogEmail.text.toString()
                val verifyCodeEmailRequest = VerifyCodeEmailRequest(
                    verifyCode,
                    email
                )
                viewModel.getResultVerifyEmail("Bearer $token",userID,"fa","asia_tehran","celsius","km","liter",verifyCodeEmailRequest)
                showProgress()
                viewModel.getResultVerifyEmail.observe(viewLifecycleOwner, Observer { response ->
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

    private fun resultEmail(){
        binding.btnConfirmPassInDialogEmail.setOnClickListener {
            if (binding.etEmailInDialogEmail.text.toString().isNotEmpty()){
                email = binding.etEmailInDialogEmail.text.toString()
                val emailRequest = EmailRequest(
                    email
                )
                viewModel.getResultChangeEmail("Bearer $token",userID,"fa","asia_tehran","celsius","km","liter",emailRequest)
                showProgress()
                viewModel.getResultChangeEmail.observe(viewLifecycleOwner, Observer { response ->
                    when(response){
                        is Resource.Success -> {
                            hideProgress()
                            response.data.let {
                                toast(requireContext(), it.message)
                                binding.etEmailInDialogEmail.visibility = View.GONE
                                binding.etFirstConfirmInDialogEmail.visibility = View.VISIBLE
                                binding.etSecondConfirmInDialogEmail.visibility = View.VISIBLE
                                binding.etThirdConfirmInDialogEmail.visibility = View.VISIBLE
                                binding.etFourthConfirmInDialogEmail.visibility = View.VISIBLE
                                binding.etFifthConfirmInDialogEmail.visibility = View.VISIBLE
                                binding.etSixthConfirmInDialogEmail.visibility = View.VISIBLE
                                binding.btnConfirmPassInDialogEmail.visibility = View.GONE
                                binding.btnConfirmVerifyCodeInDialogEmail.visibility = View.VISIBLE
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
        binding.progressInDialogEmail.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressInDialogEmail.visibility = View.GONE
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