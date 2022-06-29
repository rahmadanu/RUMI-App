package com.example.rumiapp.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class MSPEditText(context: Context, attrs: AttributeSet): AppCompatEditText(context, attrs) {

    init {
        applyFont()
    }

    private fun applyFont(){
        val boldTypeFace: Typeface =
            Typeface.createFromAsset(context.assets, "Montserrat-Regular.ttf")
        typeface = boldTypeFace
    }
}