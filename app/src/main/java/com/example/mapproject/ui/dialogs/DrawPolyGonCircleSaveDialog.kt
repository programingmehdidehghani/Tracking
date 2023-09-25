package com.example.mapproject.ui.dialogs

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mapproject.R
import com.example.mapproject.databinding.LayoutPolyGonSaveDialogBinding
import com.example.mapproject.model.circleDrawMap.DrawCircleMap
import com.example.mapproject.model.drawPolygon.DrawPolygon
import com.example.mapproject.model.drawPolygon.Shape
import com.example.mapproject.model.editFences.EditFences
import com.example.mapproject.ui.viewModels.DrawPolygonCircleViewModel
import com.example.mapproject.util.Constants
import com.example.mapproject.util.Resource
import com.example.mapproject.util.toast
import com.flask.colorpicker.OnColorChangedListener
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.util.GeoPoint
import javax.inject.Inject
import kotlin.math.cos


interface MyDialogListener {
    fun onDialogDismissed()
    fun onDialogShown()

}
@Suppress("DEPRECATION", "UNREACHABLE_CODE")
@AndroidEntryPoint
class DrawPolyGonCircleSaveDialog : DialogFragment() , OnColorChangedListener {


    private var _binding: LayoutPolyGonSaveDialogBinding? = null
    private val viewBinding get() = _binding!!
    var selectionColor: Int? = null
    private var location = ArrayList<String>()
    private var shapeList = ArrayList<Shape>()
    lateinit var listener: MyDialogListener


    private var isDraw: Boolean = false
    var centerPoint: GeoPoint? = null
    var radius: Float? = null
    private var editId: String? = ""
    var name: String? = null
    var description: String? = null
    var color: String? = null


    private lateinit var viewModel: DrawPolygonCircleViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var userID: String
    private lateinit var token: String
    private var zoomLevel: Double? = null
    private var distanceInMeters: Double? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutPolyGonSaveDialogBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog!!.window ?: return
        val params = window.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        viewModel = ViewModelProvider(requireActivity())[DrawPolygonCircleViewModel::class.java]
        viewBinding.colorPickerViewInDialogPolyGon.addOnColorChangedListener(this)
        listener.onDialogShown()
        userID = sharedPreferences.getString(Constants.KEY_USER_ID, "").toString()
        token = sharedPreferences.getString(Constants.KEY_TOKEN, "").toString()
        isDraw = arguments?.getBoolean("isDrawPolygon")!!
        editId = arguments?.getString("fencesId") ?: ""
        if (isDraw) {
            location = arguments?.getStringArrayList("shape")!!

            for (i in location) {
                val lat = i.split(",")
                val shape = Shape(
                    lat[0].toDoubleOrNull()!!,
                    lat[1].toDoubleOrNull()!!
                )
                shapeList.add(shape)
            }
        } else {
            centerPoint = GeoPoint(
                arguments?.getDouble("latitude")!!,
                arguments?.getDouble("longitude")!!
            )
            radius = arguments?.getFloat("radius")!!
            zoomLevel = arguments?.getDouble("zoomLevel")!!
        }
        if (editId != "") {
            name = arguments?.getString("name")!!
            description = arguments?.getString("description")!!
            color = arguments?.getString("color")!!
            viewBinding.etNameInDialogPolyGon.setText(name)
            if (description != null) {
                viewBinding.etDescriptionInDialogPolyGon.setText(description)
            }
        }


        viewBinding.btnSaveInDialogPolyGon.setOnClickListener {
            Log.i("clickDialog","is click .....")
            if (selectionColor != null) {
                val hexColor = String.format("%06X", 0xFFFFFF and selectionColor!!)
                if (hexColor.isNotEmpty()) {
                    if (editId != "") {
                        editFences(hexColor, editId!!)
                        return@setOnClickListener
                    }
                    if (isDraw) {
                        drawPolygon(hexColor)
                    } else {
                        val radians = MetersPerEquatorPixelAtLatitude(centerPoint!!.latitude,centerPoint!!.longitude,zoomLevel!!.toDouble(),radius!!.toDouble())

                        drawCircle(
                            hexColor,
                            centerPoint!!.latitude,
                            centerPoint!!.longitude,
                            radians,
                            radius!!.toDouble()
                        )
                    }
                }
            } else {
                toast(
                    requireContext(),
                    requireActivity().getString(R.string.toast_select_color)
                )
            }

        }


        viewBinding.btnChooseColorInDialogPolyGon.setOnClickListener {
            if (viewBinding.colorPickerViewInDialogPolyGon.visibility == View.VISIBLE) {
                viewBinding.colorPickerViewInDialogPolyGon.visibility = View.GONE
                viewBinding.btnSaveInDialogPolyGon.visibility = View.VISIBLE
                viewBinding.btnCancelInDialogPolyGon.visibility = View.VISIBLE
            } else {
                viewBinding.colorPickerViewInDialogPolyGon.visibility = View.VISIBLE
                viewBinding.btnSaveInDialogPolyGon.visibility = View.GONE
                viewBinding.btnCancelInDialogPolyGon.visibility = View.GONE
            }

        }

        viewBinding.btnCancelInDialogPolyGon.setOnClickListener {
            _binding = null
            this.dismiss()
        }


    }


    private fun MetersPerEquatorPixelAtLatitude(latitude: Double,longitude: Double,zoomLevel:Double,radiusInRadians: Double): Double {

        val earthRadius = 6371.0

        val distanceInKilometers = 2 * Math.PI * earthRadius * radiusInRadians


        distanceInMeters = distanceInKilometers * 1000

        val metersPerPixel =
            cos(latitude * Math.PI / 180) * 2 * Math.PI * earthRadius / (256 * Math.pow(
                2.0,
                zoomLevel
            ))
        return distanceInMeters!! / metersPerPixel
     }


        private fun editFences(color: String, id: String) {
            val editFences = EditFences(
                color,
                viewBinding.etDescriptionInDialogPolyGon.text.toString(),
                viewBinding.etNameInDialogPolyGon.text.toString(),
            )

            viewModel.editFences(
                "Bearer $token", id, "fa", "asia_tehran", "celsius", "km", "liter", editFences
            )
            viewModel.editFences.observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgress()
                        response.data.let {
                            listener.onDialogDismissed()
                            this.dismiss()
                        }
                    }
                    is Resource.Error -> {
                        shapeList.clear()
                        hideProgress()
                    }
                    is Resource.Loading -> {
                        showProgress()
                    }
                }
            })
        }

        private fun drawCircle(
            hexColor: String,
            latitude: Double,
            longitude: Double,
            meters: Double,
            radius: Double
        ) {
            val drawCircleMap = DrawCircleMap(
                hexColor,
                viewBinding.etDescriptionInDialogPolyGon.text.toString(),
                latitude,
                longitude,
                meters,
                viewBinding.etNameInDialogPolyGon.text.toString(),
                radius
            )

            viewModel.circleDraw(
                "Bearer $token",
                userID,
                "fa",
                "asia_tehran",
                "celsius",
                "km",
                "liter",
                drawCircleMap
            )
            viewModel.drawCircle.observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgress()
                        response.data.let {
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
        }


        private fun drawPolygon(hexColor: String) {
            val drawPolygon = DrawPolygon(
                hexColor,
                viewBinding.etDescriptionInDialogPolyGon.text.toString(),
                viewBinding.etNameInDialogPolyGon.text.toString(),
                shapeList
            )

            viewModel.drawPolygon(
                "Bearer $token", userID, "fa", "asia_tehran", "celsius", "km", "liter", drawPolygon
            )
            viewModel.drawPolygon.observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgress()
                        response.data.let {
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
        }

        private fun showProgress() {
            viewBinding.progressInPolyGonDialog.visibility = View.VISIBLE
        }

        private fun hideProgress() {
            viewBinding.progressInPolyGonDialog.visibility = View.GONE
        }

        override fun onDestroyView() {
            super.onDestroyView()
            val resultIntent = Intent()
            resultIntent.putExtra("result", "some_result")
            targetFragment?.onActivityResult(
                targetRequestCode,
                Activity.RESULT_CANCELED,
                resultIntent
            )
            _binding = null
            this.dismiss()
        }

        override fun onDetach() {
            super.onDetach()
            listener?.onDialogDismissed()
            this.dismiss()
        }


        override fun onColorChanged(selectedColor: Int) {
            Log.i("color", "$selectedColor")
            selectionColor = viewBinding.colorPickerViewInDialogPolyGon.selectedColor
            viewBinding.colorPickerViewInDialogPolyGon.visibility = View.GONE
            viewBinding.btnSaveInDialogPolyGon.visibility = View.VISIBLE
            viewBinding.btnCancelInDialogPolyGon.visibility = View.VISIBLE
        }

}