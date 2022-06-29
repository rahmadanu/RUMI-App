package com.example.rumiapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.rumiapp.R
import com.example.rumiapp.databinding.ActivityMenuItemDetailsBinding
import com.example.rumiapp.firestore.FirestoreClass
import com.example.rumiapp.models.Cart
import com.example.rumiapp.models.Menu
import com.example.rumiapp.utils.Constants
import com.example.rumiapp.utils.GlideLoader

class MenuItemDetailsActivity : BaseActivity(), View.OnClickListener {

    private var _binding: ActivityMenuItemDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMenuDetails: Menu

    private var mMenuId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMenuItemDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_MENU_ID)) {
            mMenuId = intent.getStringExtra(Constants.EXTRA_MENU_ID)!!
            Log.i("Menu id", mMenuId)
        }
        setupActionBar()

        getMenuDetails()

        binding.btnAddToCart.setOnClickListener(this)
        binding.btnGoToCart.setOnClickListener(this)
    }

    fun getMenuDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getMenuDetails(this@MenuItemDetailsActivity, mMenuId)
    }

    fun menuDetailsSuccess(menu: Menu) {

        mMenuDetails = menu

        binding.apply {
            GlideLoader(this@MenuItemDetailsActivity).loadMenuItemPicture(menu.image, ivMenuDetailImage)
            tvMenuDetailsTitle.text = menu.title
            tvMenuDetailsPrice.text = menu.price
            tvMenuDetailsDescription.text = menu.description
        }

        if (FirestoreClass().getCurrentUserId() == menu.user_id) {
            //check this code again, it's not supposed to be our implementation
            hideProgressDialog()
        } else {
            FirestoreClass().checkIfItemsExistInCart(this, mMenuId)
        }
    }

    private fun addToCart() {

        val cartItems = Cart(
            FirestoreClass().getCurrentUserId(),
            mMenuId,
            mMenuDetails.title,
            mMenuDetails.price,
            mMenuDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )

        hideProgressDialog()

        FirestoreClass().addCartItems(this, cartItems)
    }

    fun addToCartSuccess() {
        hideProgressDialog()

        Toast.makeText(this, resources.getString(R.string.success_message_item_added_to_cart), Toast.LENGTH_SHORT).show()

        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE
    }

    fun menuItemExistInCart() {
        hideProgressDialog()

        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_add_to_cart -> {
                    addToCart()
                }
                R.id.btn_go_to_cart -> {
                    val intent = Intent(this, CartListActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    fun setupActionBar() {
        val toolbar = binding.toolbarMenuDetailsActivity
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}