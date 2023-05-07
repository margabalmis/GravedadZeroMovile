package com.example.gravedadzero.model

import android.annotation.SuppressLint
import android.graphics.Color
import com.example.gravedadzero.R
import com.google.rpc.context.AttributeContext.Resource
import java.text.SimpleDateFormat
import java.util.*


data class Bulder(
    var name: String ?= null,
    var user_email: String?= null,
    var difficulty: Difficulty?= null,
    var date: Date?= null,
    var image:String?= null,
    var done: Boolean?= null,
    var shared: Boolean?= null){

    fun getColorDone(done: Boolean): Int{
        return if (done) ColorDone.Orange.color
        else ColorDone.Grey.color
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateBulder(): String {
        val pattern = "dd-MM-yyyy"
        return SimpleDateFormat(pattern).format(date!!)
    }
}

enum class Difficulty(val color: Int) {
    Grey(Color.parseColor("#5F5E62")),
    Blue(Color.parseColor("#2196F3")),
    Purple(Color.parseColor("#8E61DE")),
    Orange(Color.parseColor("#FF5722")),
    Green(Color.parseColor("#4CAF50")),
    Yellow(Color.parseColor("#FFC107")),

}

enum class ColorDone ( val color: Int){
    Orange(Color.parseColor("#FF8000")),
    Grey(Color.parseColor("#75E3E3E3")),
}