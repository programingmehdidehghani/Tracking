package com.example.mapproject.model.drawPolygon

data class DrawPolygon(
    val color: String,
    val description: String?,
    val name: String,
    val shape: List<Shape>
)