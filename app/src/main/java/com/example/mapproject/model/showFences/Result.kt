package com.example.mapproject.model.showFences

data class Result(
    val center: Any,
    val color: String,
    val created_at: String,
    val description: String?,
    val id: Int,
    val name: String,
    val radius: Any,
    val shape: List<Shape>,
    val type: String,
    val updated_at: String
)