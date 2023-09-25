package com.example.mapproject.model.constant

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Service(
    val types: MutableList<String>
): Parcelable