package com.example.rumiapp.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.rumiapp.models.*
import com.example.rumiapp.ui.activities.*
import com.example.rumiapp.ui.fragments.HomeFragment
import com.example.rumiapp.ui.fragments.OrdersFragment
import com.example.rumiapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {

    private val mFirestoreClass = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {

        mFirestoreClass.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while registering the user.", e)
            }
    }

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    //supposed to be in activity, don't forget to update the verification status, put it in register activity
    fun sendEmailVerification(activity: Activity) {
        val currentUser = FirebaseAuth.getInstance().currentUser!!

        currentUser.sendEmailVerification()
            .addOnSuccessListener {
                Toast.makeText(activity, "Email verification sent", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Failed while sending email verification: " + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    fun checkIfEmailVerified(activity: Activity) {
        val currentUser = FirebaseAuth.getInstance().currentUser!!

        if (currentUser.isEmailVerified) {
            Toast.makeText(activity, "Your email is verified", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Your email is NOT verified", Toast.LENGTH_SHORT).show()
        }
    }

    fun getUserDetails(activity: Activity) {
        mFirestoreClass.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                val user = document.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(Constants.RUMI_PREFERENCES, Context.MODE_PRIVATE)

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(Constants.LOGGED_IN_USERNAME, "${user.firstName} ${user.lastName}")
                editor.apply()

                when(activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsProfileActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while getting user details.", e)
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFirestoreClass.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }

            .addOnFailureListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while updating the user details")
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileUri: Uri?, imageType: String) {
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(activity, imageFileUri)
        )

        storageRef.putFile(imageFileUri!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.i("Firebase image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, exception.message, exception)
            }
    }

    fun getMenuList(fragment: HomeFragment) {
        mFirestoreClass.collection(Constants.MENU)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val menuList: ArrayList<Menu> = ArrayList()

                for (i in document.documents) {
                    val menu = i.toObject(Menu::class.java)!!
                    menu.menu_id = i.id

                    menuList.add(menu)
                }
                fragment.successGetMenuList(menuList)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting menu list.", e)
            }
    }

    fun getAllMenuList(activity: Activity) {
        mFirestoreClass.collection(Constants.MENU)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val menuList: ArrayList<Menu> = ArrayList()

                for (i in document.documents) {
                    val menu = i.toObject(Menu::class.java)!!
                    menu.menu_id = i.id

                    menuList.add(menu)
                }
                when (activity) {
                    is CartListActivity -> {
                        activity.getMenuListSuccess(menuList)
                    }
                    is CheckoutActivity -> {
                        activity.getMenuListSuccess(menuList)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while getting menu list.", e)
            }
    }

    fun getMenuDetails(activity: MenuItemDetailsActivity, menuId: String) {

        mFirestoreClass.collection(Constants.MENU)
            .document(menuId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())

                val menu = document.toObject(Menu::class.java)!!

                activity.menuDetailsSuccess(menu)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting menu details.", e)
            }
    }

    fun addCartItems(activity: MenuItemDetailsActivity, cartItems: Cart) {

        mFirestoreClass.collection(Constants.CART_ITEMS)
            .document()
            .set(cartItems, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while creating cart item document", e)
            }
    }

    fun checkIfItemsExistInCart(activity: MenuItemDetailsActivity, menuId: String) {

        mFirestoreClass.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .whereEqualTo(Constants.MENU_ID, menuId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                if (document.documents.size > 0) {
                    activity.menuItemExistInCart()
                } else {
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while checking the exisiting cart list", e)
            }
    }

    fun getCartList(activity: Activity) {
        mFirestoreClass.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val cartList: ArrayList<Cart> = ArrayList()

                for (i in document.documents) {
                    val cartItem = i.toObject(Cart::class.java)!!
                    cartItem.id = i.id

                    cartList.add(cartItem)
                }

                when (activity) {
                    is CartListActivity -> {
                        activity.getCartItemListSuccess(cartList)
                    }
                    is CheckoutActivity -> {
                        activity.getCartItemListSuccess(cartList)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while getting cart item list", e)
            }

    }

    fun removeItemFromCart(context: Context, cart_id: String) {

        mFirestoreClass.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }

                Log.e(context.javaClass.simpleName, "Error while removing the cart item", e)
            }
    }

    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {

        mFirestoreClass.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }
            }
    }

    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {

        mFirestoreClass.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while adding the address", e)
            }
    }

    fun getAddressList(activity: AddressListActivity) {

        mFirestoreClass.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val addressList: ArrayList<Address> = ArrayList()

                for (i in document.documents) {
                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id

                    addressList.add(address)
                }

                activity.getAddressListSuccess(addressList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting the address list", e)
            }
    }

    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {

        mFirestoreClass.collection(Constants.ADDRESSES)
            .document(addressId)
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while updating the address", e)
            }
    }

    fun deleteAddress(activity: AddressListActivity, addressId: String) {

        mFirestoreClass.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while deleting the address", e)
            }
    }

    fun placeOrder(activity: CheckoutActivity, order: Order) {

        mFirestoreClass.collection(Constants.ORDERS)
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.placeAnOrderSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, " Error while placing an order", e)
            }
    }

    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<Cart>) {

        val writeBatch = mFirestoreClass.batch()

        for (cart in cartList) {

            val documentReference = mFirestoreClass.collection(Constants.CART_ITEMS)
                .document(cart.id)

            writeBatch.delete(documentReference)
        }

        writeBatch.commit()
            .addOnSuccessListener {
                activity.updateAllDetailsSuccess()
            }
    }

    fun getOrderList(fragment: OrdersFragment) {

        mFirestoreClass.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val list: ArrayList<Order> = ArrayList()

                for (i in document.documents) {

                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id

                    list.add(orderItem)
                }

                fragment.getOrderListSuccess(list)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()

                Log.e(fragment.javaClass.simpleName, "Error while getting the order list", e)
            }
    }
}