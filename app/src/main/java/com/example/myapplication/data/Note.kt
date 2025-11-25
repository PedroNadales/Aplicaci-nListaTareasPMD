package com.example.myapplication.data

data class Note(
    var id: Long = 0,
    var title: String,
    var content: String,
    var date: String = "",
    var important: Boolean = false,
    var rating: Float = 0f,
    var priority: Int = 0
)
