package com.example.mapproject.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.mapproject.R
import com.example.mapproject.databinding.ActivityMainBinding
import com.example.mapproject.model.constant.ResultXX
import com.example.mapproject.ui.changePass.ChangePasswordActivity
import com.example.mapproject.ui.editProfile.EditProfileActivity
import com.example.mapproject.ui.login.LoginActivity
import com.example.mapproject.ui.viewModels.LogoutUserViewModel
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import com.example.mapproject.util.toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("DEPRECATION", "UNREACHABLE_CODE")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {


    private val logoutUserViewModel: LogoutUserViewModel by viewModels()

    @Inject
    lateinit var sharedPref : SharedPreferences

    private lateinit var userID: String
    private lateinit var token: String

    private var _binding: ActivityMainBinding? = null
    private val viewBinding get() = _binding!!

    lateinit var constant: ResultXX
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)
        setSupportActionBar(viewBinding.appBarMain2.toolbar)

        window.statusBarColor = ContextCompat.getColor(this, R.color.teal_700)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.shopNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottom: BottomNavigationView = viewBinding.root.findViewById(R.id.bottomNavigationView)
        val menuPicture : View? = viewBinding.root.findViewById(R.id.iv_menu_in_activity_main)
        val txtTitleFragment : TextView? = viewBinding.root.findViewById(R.id.txt_name_fragment_in_activity_main)

        viewBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        menuPicture!!.setOnClickListener {
            viewBinding.drawerLayout.openDrawer(GravityCompat.END);
        }
        bottom.setupWithNavController(navController)
        viewBinding.navView.setNavigationItemSelectedListener(this)
/*
        bottom.setOnItemSelectedListener{
            when (it.itemId) {
                R.id.trackingFragment -> {
                    txtTitleFragment!!.text = "مسیریاب ها"
                    true
                }
                R.id.deviceFragment -> {
                    txtTitleFragment!!.text = "محدوده ها(ردیاب ها)"
                    true
                }
                R.id.mapFragment -> {
                    txtTitleFragment!!.text = "نقشه"
                    true
                }
                R.id.serviceCarFragment -> {
                    txtTitleFragment!!.text = "سرویس(ردیاب)"
                    true
                }
                else -> {
                    true
                }
            }
        }
*/
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.serviceCourse -> {

            }
            R.id.addValidity -> {
                val intent = Intent(this, AddValidityActivity::class.java)
                startActivity(intent)
            }
            R.id.plan -> {

            }
            R.id.alerts -> {
                val intent = Intent(this, AlarmsActivity::class.java)
                startActivity(intent)
            }
            R.id.notification -> {

            }
            R.id.paymentPay -> {

            }
            R.id.editProfile -> {
                val intent = Intent(this, EditProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.changePass -> {
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
            }
            R.id.reminder -> {

            }
            R.id.speed -> {

            }
            R.id.getAlertSetting -> {

            }
            R.id.contactUs -> {
                val intent = Intent(this, ContactUsActivity::class.java)
                startActivity(intent)
            }
            R.id.exit -> {
                userID = sharedPref.getString(Constants.KEY_USER_ID,"").toString()
                token = sharedPref.getString(Constants.KEY_TOKEN,"").toString()
                logoutUserViewModel.resultLogoutUser("Bearer $token",userID,"fa","asia_tehran","celsius","km","liter")
                logoutUserViewModel.getResultLogout.observe(this, Observer { response ->
                    when(response){
                        is Resource.Success -> {
                          //  hideProgress()
                            response.data.let {
                                toast(this, it.message)
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        is Resource.Error -> {
                           // hideProgress()
                        }
                        is Resource.Loading -> {
                          //  showProgress()
                        }
                    }

                })
            }

        }
        return true
    }

    override fun onBackPressed() {
        if (viewBinding.drawerLayout.isDrawerOpen(GravityCompat.END)){
            viewBinding.drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }

}