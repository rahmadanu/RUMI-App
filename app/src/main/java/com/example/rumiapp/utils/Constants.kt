package com.example.rumiapp.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    const val USERS: String = "users"
    const val MENU: String = "menu"
    const val CART_ITEMS: String = "cart_items"
    const val ADDRESSES: String = "addresses"
    const val ORDERS: String = "orders"

    const val MERCHAT_ID_MIDTRANS = "G129384979"
    const val CLIENT_KEY_MIDTRANS = "SB-Mid-client-KB90Gcv5408xbmWu"
    const val SERVER_KEY_MIDTRANS = "SB-Mid-server-6oG0d_VXJDVKW_udiBnLsqPg"
    const val BASE_URL_MIDTRANS = "https://api.sandbox.midtrans.com"

    const val RUMI_PREFERENCES: String = "RumiPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"

    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val EXTRA_MENU_ID: String = "extra_menu_id"

    const val EXTRA_ADDRESS_DETAILS: String = "extra_address_details"

    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"

    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"

    const val EXTRA_ORDER_DETAILS: String = "extra_order_details"

    const val STORAGE_PERMISSION_CODE = 5
    const val PICK_IMAGE_REQUEST_CODE = 1

    const val ADD_ADDRESS_REQUEST_CODE: Int = 121

    const val DEFAULT_CART_QUANTITY = "1"

    const val MALE: String = "male"

    const val FEMALE: String = "female"
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"

    const val IMAGE: String = "image"

    const val COMPLETE_PROFILE: String = "profileCompleted"

    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"

    const val USER_ID: String = "user_id"
    const val MENU_ID: String = "menu_id"

    const val USER_PROFILE_IMAGE = "user_profile_image"

    const val CART_QUANTITY: String = "cart_quantity"

    const val HOME: String = "Home"
    const val OFFICE: String = "Office"
    const val OTHER: String = "Other"

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}