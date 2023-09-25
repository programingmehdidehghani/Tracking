package com.example.mapproject.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.mapproject.databinding.DialogMonitorBinding
import com.example.mapproject.databinding.FragmentCarBinding

class MonitorDialog  : DialogFragment() {

    private var _binding: DialogMonitorBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogMonitorBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog!!.window ?: return
        val params = window.attributes
        params.width = 700
        params.height = 1000
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.dismiss()
        _binding = null
    }
    override fun onDetach() {
        super.onDetach()
        this.dismiss()
    }
}