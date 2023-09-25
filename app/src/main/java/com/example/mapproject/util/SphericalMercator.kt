package com.example.mapproject.util

import android.location.Location

object SphericalMercator {
    private const val RADIUS = 6378137.0 /* in meters on the equator */

    /* These functions take their length parameter in meters and return an angle in degrees */
    fun y2lat(aY: Double): Double {
        return Math.toDegrees(Math.atan(Math.exp(aY / RADIUS)) * 2 - Math.PI / 2)
    }

    fun x2lon(aX: Double): Double {
        return Math.toDegrees(aX / RADIUS)
    }

    /* These functions take their angle parameter in degrees and return a length in meters */
    fun lat2y(aLat: Double): Double {
        return Math.log(Math.tan(Math.PI / 4 + Math.toRadians(aLat) / 2)) * RADIUS
    }

    fun lon2x(aLong: Double): Double {
        return Math.toRadians(aLong) * RADIUS
    }

    fun location2xy(location: Location?): String {
        return if (location != null) String.format(
            "%s,%s",
            lon2x(location.longitude),
            lat2y(location.latitude)
        ) else String.format("%s,%s", null, null)
    }

    fun fromLatLng(lat: Double, lng: Double): String {
        return String.format("%s,%s", lon2x(lng), lat2y(lat))
    }

    fun fromeBBOX(bbox: String): String? {
        val bboxs = bbox.split(",".toRegex()).toTypedArray() //
        return if (bboxs.size == 4) String.format(
            "%s,%s,%s,%s",
            x2lon(bboxs[0].toDouble()),
            y2lat(bboxs[1].toDouble()),
            x2lon(bboxs[2].toDouble()),
            y2lat(bboxs[3].toDouble())
        ) else null
    }
}