package com.example.mapproject.model.constant

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class UnitsX(
    val capacity: MutableList<String>,
    val distance: MutableList<String>,
    val temperature: MutableList<String>
): Parcelable