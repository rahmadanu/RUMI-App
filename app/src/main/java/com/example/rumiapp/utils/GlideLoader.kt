package com.example.rumiapp.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.rumiapp.R
import java.io.IOException

class GlideLoader(val context: Context) {

    fun loadUserPicture(imageUri: Any, imageView: ImageView) {
        try {
            Glide.with(context)
                .load(Uri.parse(imageUri.toString()))
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadMenuItemPicture(imageUri: Any, imageView: ImageView) {
        try {
            Glide.with(context)
                .load(Uri.parse(imageUri.toString()))
                .centerCrop()
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}