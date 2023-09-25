package com.example.mapproject.ui.dialogs

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mapproject.databinding.DialogChangePassBinding
import com.example.mapproject.model.changePassWord.ChangePasswordRequest
import com.example.mapproject.ui.login.LoginActivity
import com.example.mapproject.ui.changePass.ChangePassViewModel
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import com.example.mapproject.util.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChangePassDialog : DialogFragment() {

    private lateinit var binding: DialogChangePassBinding

    private lateinit var viewModel: ChangePassViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var userID: String
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogChangePassBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog!!.window ?: return
        val params = window.attributes
        params.width = 700
        params.height = 1000
        viewModel = ViewModelProvider(requireActivity())[ChangePassViewModel::class.java]
        binding.ivShowCurrentPassInDialogChangePass.setOnClickListener {
            binding.etCurrentPassInDialogChangePass.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.etCurrentPassInDialogChangePass.transformationMethod =
                PasswordTransformationMethod.getInstance();

        }
        binding.btnConfirmPassInDialogChangePass.setOnClickListener {
            if (binding.etCurrentPassInDialogChangePass.text.toString().isNotEmpty()
                && binding.etNewPassInDialogChangePass.text.toString().isNotEmpty() &&
                    binding.etConfirmPassInDialogChangePass.text.toString().isNotEmpty()){
                userID = sharedPreferences.getString(Constants.KEY_USER_ID,"").toString()
                token = sharedPreferences.getString(Constants.KEY_TOKEN,"").toString()
                val changePasswordRequest = ChangePasswordRequest(
                    binding.etCurrentPassInDialogChangePass.text.toString(),
                    binding.etNewPassInDialogChangePass.text.toString(),
                    binding.etConfirmPassInDialogChangePass.text.toString()
                )
                viewModel.getResultChange("Bearer $token",userID,"fa","asia_tehran","celsius","km","liter",changePasswordRequest)
                showProgress()
                viewModel.getResultChangePass.observe(viewLifecycleOwner, Observer { response ->
                    when(response){
                        is Resource.Success -> {
                            hideProgress()
                            response.data.let {
                                toast(requireContext(), it.message)
                                val intent = Intent(requireContext(), LoginActivity::class.java)
                                requireActivity().startActivity(intent)
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
            }  else {
                toast(requireContext(), "بر کردن همه موارد اجباریست")
            }
        }
    }

    private fun showProgress() {
        binding.progressInDialogChangePass.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressInDialogChangePass.visibility = View.GONE
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