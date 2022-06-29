package com.example.rumiapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart(
    val user_id: String = "",
    val menu_id: String = "",
    val title: String = "",
    val price: String = "",
    val image: String = "",
    val cart_quantity: String = "",
    //val stock_quantity: String = "",
    var id: String = ""
) : Parcelable
