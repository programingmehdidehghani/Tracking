package com.example.mapproject.ui.alerts

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mapproject.R
import com.example.mapproject.adapters.AlertsAdapter
import com.example.mapproject.databinding.ActivityAlertsBinding
import com.example.mapproject.databinding.ActivityEmailBinding
import com.example.mapproject.model.alerts.Alerts
import com.example.mapproject.model.alerts.Data
import com.example.mapproject.ui.activities.MainActivity2
import com.example.mapproject.ui.changeEmail.ChangeEmailViewModel
import com.example.mapproject.ui.editProfile.EditProfileActivity
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlertsActivity : AppCompatActivity() {

    private var _binding: ActivityAlertsBinding? = null
    private val viewBinding get() = _binding!!

    private val alertsViewModel: AlertsViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var userID: String
    private lateinit var token: String

    private lateinit var alertsAdapter : AlertsAdapter
    private var alertsList: ArrayList<Data> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAlertsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        userID = sharedPreferences.getString(Constants.KEY_USER_ID,"").toString()
        token = sharedPreferences.getString(Constants.KEY_TOKEN,"").toString()
        viewBinding.ivBackPressInAlertActivity.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
        setupRecyclerView()
        alertsViewModel.getAlerts("Bearer $token",userID ,"","","","","","","","","",
            "fa", "asia_tehran", "celsius", "km", "liter")
        alertsViewModel.getResultAlerts.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data.let {
                        alertsList.addAll(it.result.data)
                        hideProgress()
                        alertsAdapter.differ.submitList(alertsList)
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

    private fun setupRecyclerView(){
        alertsAdapter = AlertsAdapter(this)
        viewBinding.rv.apply {
            adapter = alertsAdapter
            layoutManager = LinearLayoutManager(this@AlertsActivity)
        }
    }

    private fun showProgress() {
        viewBinding.progressInAlertsActivity.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        viewBinding.progressInAlertsActivity.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }
}