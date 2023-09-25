package com.example.mapproject.model.constant

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Audits(
    val device: MutableList<String>,
    val user: MutableList<String>
): Parcelable