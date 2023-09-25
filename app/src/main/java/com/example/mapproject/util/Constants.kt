package com.example.mapproject.util

class Constants {
    companion object {
        const val BASE_URL = "https://api.koja.co/api/"
        const val SHARED_PREFERENCES_NAME = "sharedPref"
        const val DEFAULT_LANGUAGE = "DEFAULT_LANGUAGE"
        const val IS_CHANGE_LANGUAGE = "IS_CHANGE_LANGUAGE"
        const val KEY_USER_ID = "KEY_USER_ID"
        const val KEY_TOKEN_ID = "KEY_TOKEN_ID"
        const val KEY_TOKEN = "KEY_TOKEN"
        const val MAP_LIST = "MAP_LIST"
        const val TIMEZONES_LIST = "TIMEZONES_LIST"
        const val DISTANCE_LIST = "DISTANCE_LIST"
        const val CAPACITY_LIST = "CAPACITY_LIST"
        const val TEMPERATURE_LIST = "TEMPERATURE_LIST"
        const val USERNAME = "USERNAME"
        const val PROFILE_IMAGE = "PROFILE_IMAGE"
        const val EMAIL = "EMAIL"
        const val MOBILE_NUMBER = "MOBILE_NUMBER"
        const val CAPACITY = "CAPACITY"
        const val TEMPERATURE = "TEMPERATURE"
        const val DISTANCE = "DISTANCE"
        const val NAME = "NAME"
        const val MAP = "MAP"
        const val TIME_ZONE = "TIME_ZONE"
        const val USER_NAME_FOR_FORGET_PASS = "USER_NAME_FOR_FORGET_PASS"
        const val IS_GET_DEVICES = "IS_GET_DEVICES"
        const val DEVICE_ID = "DEVICE_ID"
        const val DEVICE_NAME = "DEVICE_NAME"
        const val LATITUDE = "LATITUDE"
        const val LONGITUDE = "LONGITUDE"


        fun setPersianNumbers(str: String): String? {
            return str
                .replace("0", "۰")
                .replace("1", "۱")
                .replace("2", "۲")
                .replace("3", "۳")
                .replace("4", "۴")
                .replace("5", "۵")
                .replace("6", "۶")
                .replace("7", "۷")
                .replace("8", "۸")
                .replace("9", "۹")
        }
    }


}