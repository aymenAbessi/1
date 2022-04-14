package com.example.readitonce

import android.content.Context
import android.graphics.Color
import android.graphics.Color.rgb

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import java.util.*



class MyAdapter(context: Context, resource: Int, objects: List<String?>) :
    ArrayAdapter<String?>(context, resource, objects) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, @Nullable convertView: View?, parent: ViewGroup): View {
        val rand = Random()
        // Java 'Color' class takes 3 floats, from 0 to 1.
        // Java 'Color' class takes 3 floats, from 0 to 1.
        val r: Int = (Math.random()*(255)).toInt()
        val g: Int = (Math.random()*(255)).toInt()
        val b: Int = (Math.random()*(255)).toInt()
        val v: View = super.getView(position, convertView, parent)
       v.setBackgroundColor(rgb(r,g,b))
        return v
    }
}





