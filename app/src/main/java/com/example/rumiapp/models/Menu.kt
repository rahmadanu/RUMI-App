package com.example.rumiapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Menu(
    val user_id: String = "",
    val user_name: String = "",
    val title: String = "",
    val price: String = "",
    val description: String = "",
    //val stock: Boolean = false,
    val image: String = "",
    var menu_id: String = "",
): Parcelable