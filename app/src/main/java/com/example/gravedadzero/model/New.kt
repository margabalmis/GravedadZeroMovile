package com.example.gravedadzero.model

import android.annotation.SuppressLint
import android.media.Image
import java.text.SimpleDateFormat
import java.util.*

data class New(
    val date: Date?= null,
    val texto: String?= null,
    val titulo: String?= null) {


    @SuppressLint("SimpleDateFormat")
    fun getDateNew(): String {
        val pattern = "dd-MM-yyyy"
        return SimpleDateFormat(pattern).format(date!!)
    }

}