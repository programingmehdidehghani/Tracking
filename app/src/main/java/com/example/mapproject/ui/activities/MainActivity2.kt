package com.example.mapproject.ui.activities

import android.R.attr.button
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.mapproject.R
import com.example.mapproject.databinding.ActivityMain2Binding
import com.example.mapproject.ui.dialogs.ChangePassDialog
import com.example.mapproject.ui.dialogs.PhoneMobileDialog
import com.example.mapproject.ui.editProfile.EditProfileActivity
import com.example.mapproject.ui.login.LoginActivity
import com.example.mapproject.ui.viewModels.LogoutUserViewModel
import com.example.mapproject.ui.viewModels.ProfileViewModel
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import com.example.mapproject.util.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity2 : AppCompatActivity() {


    private var _binding: ActivityMain2Binding? = null
    private val viewBinding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()
    private val logoutUserViewModel: LogoutUserViewModel by viewModels()


    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var userID: String
    private lateinit var token: String
    private lateinit var tokenID: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.back_ground_login)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.shopNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        viewBinding.bottomNavigationView.setupWithNavController(navController)
        userID = sharedPreferences.getString(Constants.KEY_USER_ID,"").toString()
        token = sharedPreferences.getString(Constants.KEY_TOKEN,"").toString()
        tokenID = sharedPreferences.getString(Constants.KEY_TOKEN_ID,"").toString()
        getProfile()
        popupMenu()
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun popupMenu() {
        viewBinding.ivMenuProfileInActivityMain2.setOnClickListener {
            val popupMenu = PopupMenu(this@MainActivity2, viewBinding.ivMenuProfileInActivityMain2)

            popupMenu.inflate(R.menu.popup_menu_profile)
            popupMenu.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener,
                PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                    when(menuItem.itemId){
                        R.id.setting ->{
                            val intent = Intent(this@MainActivity2, EditProfileActivity::class.java)
                            startActivity(intent)
                        }
                        R.id.wallet ->{

                        }
                        R.id.orders ->{

                        }
                        R.id.support ->{

                        }
                        R.id.exit ->{
                            logoutUserViewModel.resultLogoutUser("Bearer $token",userID,"fa","asia_tehran","celsius","km","liter")
                            logoutUserViewModel.getResultLogout.observe(this@MainActivity2, Observer { response ->
                                when(response){
                                    is Resource.Success -> {
                                        hideProgress()
                                        response.data.let {
                                            toast(this@MainActivity2, it.message)
                                            val intent = Intent(this@MainActivity2, LoginActivity::class.java)
                                            startActivity(intent)
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
                    }
                    return true
                }
            })
            viewBinding.ivMenuProfileInActivityMain2.setOnClickListener {
                try {
                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val menu = popup.get(popupMenu)
                menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                    .invoke(menu,true)
                } catch (e: Exception){
                    e.printStackTrace()
                } finally {
                    popupMenu.show()
                }
            }
        }
    }

    private fun getProfile(){
        profileViewModel.getProfile(userID, "Bearer $token","fa","asia_tehran","celsius","km","liter")
        profileViewModel.getProfile.observe(this, Observer { response ->
            when(response){
                is Resource.Success ->{
                    hideProgress()
                    response.data.let {
                        getItemsProfile(
                            it.result.must_change_password!!,
                            it.result.mobile.toString(),
                            it.result.name.toString(),it.result.email.toString(),
                            it.result.username.toString(),it.result.settings.units.capacity,it.result.settings.units.temperature
                        ,it.result.settings.units.distance,it.result.settings.map,it.result.settings.timezone,it.result.avatar_url)
                       /* if (it.result.must_change_password == true){
                            val fm: FragmentManager = this.supportFragmentManager
                            val dialog = ChangePassDialog()
                            dialog.show(fm,"")
                        } else {
                            if (it.result.mobile!!.isEmpty()){
                                val fm: FragmentManager = this.supportFragmentManager
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
                        Glide.with(this).load(it.result.avatar_url).placeholder(R.drawable.baseline_person_24)
                            .error(R.drawable.baseline_person_24).override(100,100).into(viewBinding.ivProfile)
                        viewBinding.txtUserNameInActivityMain2.text = it.result.name*/
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

    private fun getItemsProfile(mustChangePass: Boolean,mobilePhone:String,name:String,email:String,userName:String
     ,capacity:String,temperature: String,distance:String,map:String,timeZone:String,image:String) {
        if (mustChangePass){
            val fm: FragmentManager = this.supportFragmentManager
            val dialog = ChangePassDialog()
            dialog.show(fm,"")
        } else {
            if (mobilePhone.isEmpty()){
                val fm: FragmentManager = this.supportFragmentManager
                val dialog = PhoneMobileDialog()
                dialog.show(fm,"")
            }
        }
        sharedPreferences.edit()
            .putString(Constants.MOBILE_NUMBER, mobilePhone)
            .apply()
        sharedPreferences.edit()
            .putString(Constants.NAME, name)
            .apply()
        sharedPreferences.edit()
            .putString(Constants.EMAIL, email)
            .apply()
        sharedPreferences.edit()
            .putString(Constants.USERNAME,userName)
            .apply()
        sharedPreferences.edit()
            .putString(Constants.CAPACITY, capacity)
            .apply()
        sharedPreferences.edit()
            .putString(Constants.TEMPERATURE,temperature)
            .apply()
        sharedPreferences.edit()
            .putString(Constants.DISTANCE,distance)
            .apply()
        sharedPreferences.edit()
            .putString(Constants.MAP,map)
            .apply()
        sharedPreferences.edit()
            .putString(Constants.TIME_ZONE,timeZone)
            .apply()
        sharedPreferences.edit()
            .putString(Constants.PROFILE_IMAGE,image)
            .apply()
        Glide.with(this).load(image).placeholder(R.drawable.baseline_person_24)
            .error(R.drawable.baseline_person_24).override(100,100).into(viewBinding.ivProfile)
        viewBinding.txtUserNameInActivityMain2.text = name
    }

    private fun showProgress() {
        viewBinding.progressInActivityMain2.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        viewBinding.progressInActivityMain2.visibility = View.GONE
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }

}