package com.example.mapproject.model.constant

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Transaction(
    val `data`: Data,
    val types: MutableList<String>
): Parcelable