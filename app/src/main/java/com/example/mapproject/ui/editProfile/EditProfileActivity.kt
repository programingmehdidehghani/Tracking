package com.example.mapproject.ui.editProfile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.size
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.mapproject.R
import com.example.mapproject.databinding.ActivityMainEditProfileBinding
import com.example.mapproject.model.changeInformationProfile.ChangeProfileInformationRequest
import com.example.mapproject.model.changeInformationProfile.SettingsX
import com.example.mapproject.model.changeInformationProfile.UnitsXX
import com.example.mapproject.ui.activities.*
import com.example.mapproject.ui.alerts.AlertsActivity
import com.example.mapproject.ui.changeEmail.EmailActivity
import com.example.mapproject.ui.changePass.ChangePasswordActivity
import com.example.mapproject.ui.phoneNumber.PhoneNumberActivity
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import com.google.android.material.navigation.NavigationView
import com.soundcloud.android.crop.Crop
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


@Suppress("DEPRECATION")
@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    private var _binding: ActivityMainEditProfileBinding? = null
    private val viewBinding get() = _binding!!

    private val editProfileActivity: EditProfileViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var resultGetImageUri: ActivityResultLauncher<Intent>


    private lateinit var userID: String
    private lateinit var token: String
    private lateinit var tokenID: String
    private var mapList: MutableList<String> = arrayListOf()
    private var timeZonesList: MutableList<String> = arrayListOf()
    private lateinit var name: String
    private lateinit var userName: String
    private lateinit var email: String
    private lateinit var profileImage: String

    private lateinit var mobileNumber: String
    private lateinit var capacitySelect: String
    private lateinit var temperatureSelect: String
    private lateinit var distanceSelect: String

    private lateinit var capacity: String
    private lateinit var distance: String
    private lateinit var temperature: String
    private lateinit var mapSelected: String
    private lateinit var timeZoneSelected: String

    private lateinit var mapSelectedServer: String
    private lateinit var timeZoneSelectedServer: String
    private lateinit var changeProfileInformationRequest : ChangeProfileInformationRequest
    private lateinit var imageUri: Uri
    private lateinit var cvImageProfile : androidx.cardview.widget.CardView
    private lateinit var radioGroupCapacity : RadioGroup
    private lateinit var radioGroupDistance : RadioGroup
    private lateinit var radioGroupTemperature : RadioGroup
    private lateinit var imageProfile : ImageView
    private lateinit var spinnerMap : androidx.appcompat.widget.AppCompatSpinner
    private lateinit var spinnerTimeZone : androidx.appcompat.widget.AppCompatSpinner
    private lateinit var btnEditProfile : com.google.android.material.button.MaterialButton
    private lateinit var etName : androidx.appcompat.widget.AppCompatEditText
    private lateinit var etUserName : androidx.appcompat.widget.AppCompatEditText
    private lateinit var etPhoneNumber : androidx.appcompat.widget.AppCompatEditText
    private lateinit var etEmail : androidx.appcompat.widget.AppCompatEditText
    private lateinit var progress : ProgressBar
    private lateinit var ivMainMenu: androidx.appcompat.widget.AppCompatImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainEditProfileBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setSupportActionBar(viewBinding.appBarEditProfile.toolbarInEditProfileActivity)

        viewBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        viewBinding.navViewEditProfile.setNavigationItemSelectedListener(this)
        getFindViewId()
        userID = sharedPreferences.getString(Constants.KEY_USER_ID,"").toString()
        token = sharedPreferences.getString(Constants.KEY_TOKEN,"").toString()
        tokenID = sharedPreferences.getString(Constants.KEY_TOKEN_ID,"").toString()
        name = sharedPreferences.getString(Constants.NAME,"").toString()
        userName = sharedPreferences.getString(Constants.USERNAME,"").toString()
        profileImage = sharedPreferences.getString(Constants.PROFILE_IMAGE,"").toString()

        email = sharedPreferences.getString(Constants.EMAIL,"").toString()
        mobileNumber = sharedPreferences.getString(Constants.MOBILE_NUMBER,"").toString()
        capacitySelect = sharedPreferences.getString(Constants.CAPACITY,"").toString()
        temperatureSelect = sharedPreferences.getString(Constants.TEMPERATURE,"").toString()
        distanceSelect = sharedPreferences.getString(Constants.DISTANCE,"").toString()
        mapSelectedServer = sharedPreferences.getString(Constants.MAP,"").toString()
        timeZoneSelectedServer = sharedPreferences.getString(Constants.TIME_ZONE,"").toString()
        ivMainMenu.setOnClickListener {
            viewBinding.drawerLayout.openDrawer(GravityCompat.START);
        }
        setProfileInformation()

    /*    viewBinding.ivBackPressInEditProfileActivity.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
            finishAffinity()
        }*/
        mapSpinner()
        radioButtonCapacity()
        locationSpinner()
        radioButtonTemperature()
        radioButtonDistance()
        editProfile()
        takeProfilePictureFromGallery()
    }

    private fun getFindViewId(){
        cvImageProfile = viewBinding.root.findViewById(R.id.cv_image_profile_in_edit_profile)
        radioGroupCapacity = viewBinding.root.findViewById(R.id.radio_capacity_in_edit_profile_activity)
        radioGroupDistance = viewBinding.root.findViewById(R.id.radio_distance_in_edit_profile_activity)
        radioGroupTemperature = viewBinding.root.findViewById(R.id.radio_temperature_in_edit_profile_activity)
        imageProfile = viewBinding.root.findViewById(R.id.iv_image_profile_in_edit_profile)
        spinnerMap = viewBinding.root.findViewById(R.id.spinner_map_in_profile_fragment)
        spinnerTimeZone = viewBinding.root.findViewById(R.id.spinner_time_zone_in_edit_fragment)
        btnEditProfile = viewBinding.root.findViewById(R.id.btn_edit_in_edit_profile_activity)
        etName = viewBinding.root.findViewById(R.id.et_last_name_in_edit_profile_activity)
        etUserName = viewBinding.root.findViewById(R.id.et_user_name_in_edit_profile_activity)
        etPhoneNumber = viewBinding.root.findViewById(R.id.et_phone_number_in_edit_profile_activity)
        etEmail = viewBinding.root.findViewById(R.id.et_email_in_edit_profile_activity)
        progress = viewBinding.root.findViewById(R.id.progress_in_edit_profile_activity)
        ivMainMenu = viewBinding.root.findViewById(R.id.iv_main_menu_in_edit_profile)

    }

    @SuppressLint("Recycle", "RestrictedApi")
    private fun takeProfilePictureFromGallery() {
        cvImageProfile.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA),
                    1)
            } else {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                resultGetImageUri.launch(intent)
            }
        }
        resultGetImageUri = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null) {
                    this.cacheDir.deleteRecursively()
                    val destination = Uri.fromFile(File(cacheDir, "cropped"))
                    Crop.of(data.data,destination).asSquare().start(this)
                }
            }
        }
    }

    @SuppressLint("Recycle")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK){
            imageUri = Crop.getOutput(data)
            if (imageUri.toString().isNotEmpty()){
                for (i in 0  until  radioGroupCapacity.size){
                    val radioButton = radioGroupCapacity.getChildAt(i) as RadioButton
                    if (radioButton.isChecked){
                        capacity = restoreInformationProfiles(radioButton.text.toString())
                    }
                }
                for (i in 0  until  radioGroupDistance.size){
                    val radioButton = radioGroupDistance.getChildAt(i) as RadioButton
                    if (radioButton.isChecked){
                        distance = restoreInformationProfiles(radioButton.text.toString())
                    }
                }
                for (i in 0  until  radioGroupTemperature.size){
                    val radioButton = radioGroupTemperature.getChildAt(i) as RadioButton
                    if (radioButton.isChecked){
                        temperature = restoreInformationProfiles(radioButton.text.toString())
                    }
                }
                Glide.with(this).load(imageUri)
                    .into(imageProfile)
                mapSelected = restoreInformationProfiles(spinnerMap.selectedItem.toString())
                timeZoneSelected = restoreInformationProfiles(spinnerTimeZone.selectedItem.toString())

                val fileDir = applicationContext.filesDir
                val file = File(fileDir,"image.png")
                val inputStream = contentResolver.openInputStream(imageUri)
                val outputStream = FileOutputStream(file)
                inputStream!!.copyTo(outputStream)
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file",file.name,requestBody)

                editProfileActivity.updatePictureProfile(userID, "Bearer $token","fa","asia_tehran","celsius","km","liter",
                    part
                )
                editProfileActivity.getResultPictureProfile.observe(this, Observer { response ->
                    when(response){
                        is Resource.Success ->{
                            hideProgress()
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
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }else {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                resultGetImageUri.launch(intent)
            }
        }
    }




    private fun editProfile(){
        btnEditProfile.setOnClickListener {
                for (i in 0  until  radioGroupCapacity.size){
                    val radioButton = radioGroupCapacity.getChildAt(i) as RadioButton
                    if (radioButton.isChecked){
                        capacity = restoreInformationProfiles(radioButton.text.toString())
                    }
                }
                for (i in 0  until  radioGroupDistance.size){
                    val radioButton = radioGroupDistance.getChildAt(i) as RadioButton
                    if (radioButton.isChecked){
                        distance = restoreInformationProfiles(radioButton.text.toString())
                    }
                }
                for (i in 0  until  radioGroupTemperature.size){
                    val radioButton = radioGroupTemperature.getChildAt(i) as RadioButton
                    if (radioButton.isChecked){
                        temperature = restoreInformationProfiles(radioButton.text.toString())
                    }
                }
                val unitsXX = UnitsXX(
                    capacity,
                    distance,
                    temperature
                )
                mapSelected = restoreInformationProfiles(spinnerMap.selectedItem.toString())
                timeZoneSelected = restoreInformationProfiles(spinnerTimeZone.selectedItem.toString())
                val settingsX = SettingsX(
                    mapSelected,
                    timeZoneSelected,
                    unitsXX
                )
                val name: String = etName.text.toString()
            changeProfileInformationRequest = if (name.isNotEmpty()){
                ChangeProfileInformationRequest(
                    name,
                    settingsX
                )
            } else {
                ChangeProfileInformationRequest(
                    etName.hint.toString(),
                    settingsX
                )
            }

                editProfileActivity.editProfile(userID, "Bearer $token","fa","asia_tehran","celsius","km","liter",changeProfileInformationRequest)
                editProfileActivity.getResultEditProfile.observe(this, Observer { response ->
                    when(response){
                        is Resource.Success ->{
                            hideProgress()
                            response.data.let {
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

    private fun restoreInformationProfiles(itemName : String) : String {
        when(itemName){
            "Asia/Tehran" ->{
                return "asia_tehran"
            }
            "UTC" ->{
                return "utc"
            }
            "نقشه راه OSM" ->{
                return "open_street_map"
            }
            "نقشه راه" ->{
                return "google_streets"
            }
            "نقشه DEM" ->{
                return "google_terrain"
            }
            "عکس ماهواره ای" ->{
                return "google_satellite"
            }
            "نقشه ترکیبی" ->{
                return "google_hybrid"
            }
            "نقشه ترافیکی" ->{
                return "google_traffic"
            }
            "لیتر" ->{
                return "liter"
            }
            "گالن" ->{
                return "gallon"
            }
            "C" ->{
                return "celsius"
            }
            "F" ->{
                return "fahrenheit"
            }
            "km,km/h" ->{
                return "km"
            }
            "mi,mph" ->{
                return "mi"
            }
            "nmi,knot" ->{
                return "nmi"
            }
            "Open Street Map" ->{
                return "open_street_map"
            }
            "Google Map(Street)" ->{
                return "google_streets"
            }
            "Google Map(Terrain)" ->{
                return "google_terrain"
            }
            "Google Map(Satellite)" ->{
                return "google_satellite"
            }
            "Google Map(Hybrid)" ->{
                return "google_hybrid"
            }
            "Google Map(Traffic)" ->{
                return "google_traffic"
            }
            "Liter" ->{
                return "liter"
            }
            "U.S Gallon" ->{
                return "gallon"
            }
        }
        return ""
    }

    private fun setProfileInformation(){
        if (userName.isNotEmpty()){
            etUserName.hint = userName
        }
        if (email.isNotEmpty() && email != "null"){
            etEmail.hint = email
        } else {
            etEmail.hint = "info@koja.co"
        }
        if (mobileNumber.isNotEmpty()){
            etPhoneNumber.hint = mobileNumber
        }
        if (name.isNotEmpty()){
            etName.hint = name
        }
        if (profileImage.isNotEmpty()){
            Glide.with(this).load(profileImage).placeholder(R.drawable.baseline_person_24)
                .error(R.drawable.baseline_person_24).into(imageProfile)
        }
    }

    private fun locationSpinner(){
        val jsonArrayTimeZones = JSONArray(sharedPreferences.getString(Constants.TIMEZONES_LIST,""))
        for (i in 0 until jsonArrayTimeZones.length()){
            when(jsonArrayTimeZones.getString(i)){
                "asia_tehran" ->{
                    timeZonesList.add("Asia/Tehran")
                    if (timeZoneSelectedServer == "asia_tehran"){
                        timeZoneSelectedServer = "0"
                    }
                }
                "utc" ->{
                    timeZonesList.add("UTC")
                    if (timeZoneSelectedServer == "utc"){
                        timeZoneSelectedServer = "1"
                    }
                }
            }
        }
        val langAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.spinner_text,timeZonesList)
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_location)
        spinnerTimeZone.adapter = langAdapter
        for (position in 0 until langAdapter.count) {
            if (langAdapter.getItemId(position).toString() == timeZoneSelectedServer) {
                spinnerTimeZone.setSelection(position)
                return
            }
        }
    }

    private fun mapSpinner(){
        val jsonArrayMaps = JSONArray(sharedPreferences.getString(Constants.MAP_LIST,""))
        for (i in 0 until jsonArrayMaps.length()){
            when(jsonArrayMaps.getString(i)){
                "open_street_map" ->{
                    mapList.add(getString(R.string.open_street_map))
                    if (mapSelectedServer == "open_street_map"){
                        mapSelectedServer = "0"
                    }
                }
                "google_streets" ->{
                    mapList.add(getString(R.string.google_streets))
                    if (mapSelectedServer == "google_streets"){
                        mapSelectedServer = "1"
                    }
                }
                "google_terrain" ->{
                    mapList.add(getString(R.string.google_terrain))
                    if (mapSelectedServer == "google_terrain"){
                        mapSelectedServer = "2"
                    }
                }
                "google_satellite" ->{
                    mapList.add(getString(R.string.google_satellite))
                    if (mapSelectedServer == "google_satellite"){
                        mapSelectedServer = "3"
                    }
                }
                "google_hybrid" ->{
                    mapList.add(getString(R.string.google_hybrid))
                    if (mapSelectedServer == "google_hybrid"){
                        mapSelectedServer = "4"
                    }
                }
                "google_traffic" ->{
                    mapList.add(getString(R.string.google_traffic))
                    if (mapSelectedServer == "google_traffic"){
                        mapSelectedServer = "5"
                    }
                }
            }
        }
        val langAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.spinner_text,mapList)
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_location)
        spinnerMap.adapter = langAdapter
        for (position in 0 until langAdapter.count) {
            if (langAdapter.getItemId(position).toString() == mapSelectedServer) {
                spinnerMap.setSelection(position)
                return
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun radioButtonCapacity(){
        val jsonArrayCapacity = JSONArray(sharedPreferences.getString(Constants.CAPACITY_LIST,""))
        for (i in 0 until jsonArrayCapacity.length()){
            when(jsonArrayCapacity.getString(i)){
                "liter" ->{
                    val radioButton = RadioButton(this)
                    radioButton.text = getString(R.string.liter)
                   // radioButton.text = "لیتر"
                    if (capacitySelect == "liter"){
                        radioButton.isChecked = true
                    }
                    radioButton.setTextColor(resources.getColor(R.color.whiteLow))
                    radioGroupCapacity.addView(radioButton)
                    if (radioButton.isChecked){
                        radioGroupCapacity.check(radioButton.id)
                    }
                }
                "gallon" ->{
                    val radioButton = RadioButton(this)
                    radioButton.text = getString(R.string.gallon)
                    //radioButton.text = "گالن"
                    if (capacitySelect == "gallon"){
                        radioButton.isChecked = true
                    }
                    radioButton.setTextColor(resources.getColor(R.color.whiteLow))
                    radioGroupCapacity.addView(radioButton)
                    if (radioButton.isChecked){
                        radioGroupCapacity.check(radioButton.id)
                    }
                }
            }
        }
    }


    @SuppressLint("ResourceAsColor")
    private fun radioButtonTemperature(){
        val jsonArrayTemperature = JSONArray(sharedPreferences.getString(Constants.TEMPERATURE_LIST,""))
        for (i in 0 until jsonArrayTemperature.length()){
            when(jsonArrayTemperature.getString(i)){
                "celsius" ->{
                    val radioButton = RadioButton(this)
                    radioButton.text = "C"
                    if (temperatureSelect == "celsius"){
                        radioButton.isChecked = true
                    }
                    radioButton.setTextColor(resources.getColor(R.color.whiteLow))
                    radioGroupTemperature.addView(radioButton)
                    if (radioButton.isChecked){
                        radioGroupTemperature.check(radioButton.id)
                    }
                }
                "fahrenheit" ->{
                    val radioButton = RadioButton(this)
                    radioButton.text = "F"
                    if (temperatureSelect == "fahrenheit"){
                        radioButton.isChecked = true
                    }
                    radioButton.setTextColor(resources.getColor(R.color.whiteLow))
                    radioGroupTemperature.addView(radioButton)
                    if (radioButton.isChecked){
                        radioGroupTemperature.check(radioButton.id)
                    }
                }
            }
        }

    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    private fun radioButtonDistance(){
        val jsonArrayDistance = JSONArray(sharedPreferences.getString(Constants.DISTANCE_LIST,""))
        for (i in 0 until jsonArrayDistance.length()){
            when(jsonArrayDistance.getString(i)){
                "km" ->{
                    val radioButton = RadioButton(this)
                    radioButton.text = "km,km/h"
                    if (distanceSelect == "km"){
                        radioButton.isChecked = true
                    }
                    radioButton.setTextColor(resources.getColor(R.color.whiteLow))
                    radioGroupDistance.addView(radioButton)
                    if (radioButton.isChecked){
                        radioGroupDistance.check(radioButton.id)
                    }
                }
                "mi" ->{
                    val radioButton = RadioButton(this)
                    radioButton.text = "mi,mph"
                    if (distanceSelect == "mi"){
                        radioButton.isChecked = true
                    }
                    radioButton.setTextColor(resources.getColor(R.color.whiteLow))
                    radioGroupDistance.addView(radioButton)
                    if (radioButton.isChecked){
                        radioGroupDistance.check(radioButton.id)
                    }
                }
                "nmi" ->{
                    val radioButton = RadioButton(this)
                    radioButton.text = "nmi,knot"
                    if (distanceSelect == "nmi"){
                        radioButton.isChecked = true
                    }
                    radioButton.setTextColor(resources.getColor(R.color.whiteLow))
                    radioGroupDistance.addView(radioButton)
                    if (radioButton.isChecked){
                        radioGroupDistance.check(radioButton.id)
                    }
                }
            }
        }
    }

    private fun showProgress() {
        progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progress.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (viewBinding.drawerLayout.isDrawerVisible(GravityCompat.START)){
            viewBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
            finishAffinity()
            super.onBackPressed()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.changePhoneNumber -> {
                val intent = Intent(this, PhoneNumberActivity::class.java)
                startActivity(intent)
            }
            R.id.changeEmail -> {
                val intent = Intent(this, EmailActivity::class.java)
                startActivity(intent)
            }
            R.id.changePassword -> {
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
            }
            R.id.alerts_edit_profile -> {
                val intent = Intent(this, AlertsActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }
}