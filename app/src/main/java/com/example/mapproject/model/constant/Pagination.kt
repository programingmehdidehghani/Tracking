package com.example.mapproject.model.constant

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pagination(
    val max_per_page: Int,
    val per_page: Int
): Parcelable