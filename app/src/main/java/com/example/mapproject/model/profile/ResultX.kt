package com.example.mapproject.model.profile

data class ResultX(
    val alerts: Int,
    val avatar_url: String,
    val balance: Double,
    val created_at: String?,
    val email: Any?,
    val full_parents_ids: List<Int>,
    val id: Int,
    val messages: Int,
    val mobile: String?,
    val must_change_password: Boolean?,
    val name: String?,
    val parent: Parent,
    val permissions: List<String>,
    val role: String,
    val settings: Settings,
    val updated_at: String,
    val username: String?
)