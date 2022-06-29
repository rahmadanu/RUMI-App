package com.example.rumiapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rumiapp.R
import com.example.rumiapp.adapters.CartItemListAdapter
import com.example.rumiapp.databinding.ActivityCartListBinding
import com.example.rumiapp.firestore.FirestoreClass
import com.example.rumiapp.models.Cart
import com.example.rumiapp.models.Menu
import com.example.rumiapp.utils.Constants

class CartListActivity : BaseActivity() {

    private var _binding: ActivityCartListBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMenuList: ArrayList<Menu>
    private lateinit var cartItemList: ArrayList<Cart>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        //getCartItemList()
        getMenuList()
    }

    fun getCartItemListSuccess(cartList: ArrayList<Cart>) {
        hideProgressDialog()

        if (cartList.size > 0) {
            binding.apply {
                rvCartItemsList.visibility = View.VISIBLE
                llCheckout.visibility = View.VISIBLE
                tvNoCartItemFound.visibility = View.GONE

                rvCartItemsList.layoutManager = LinearLayoutManager(this@CartListActivity)
                rvCartItemsList.setHasFixedSize(true)

                val adapter = CartItemListAdapter(this@CartListActivity, cartList, true)
                rvCartItemsList.adapter = adapter

                var subTotal = 0
                val shippingCharge = 4000

                for (item in cartList) {
                    val price = item.price.toInt()
                    val quantity = item.cart_quantity.toInt()

                    subTotal += price * quantity
                }

                tvSubTotal.text = "Rp${subTotal},-"
                tvShippingCharge.text = "Rp${shippingCharge},-"

                if (subTotal > 0) {
                    llCheckout.visibility = View.VISIBLE

                    val total = subTotal + shippingCharge
                    tvTotalAmount.text = "Rp$total,-"
                } else {
                    llCheckout.visibility = View.GONE
                }
            }
        } else {
            binding.apply {
                rvCartItemsList.visibility = View.GONE
                llCheckout.visibility = View.GONE
                tvNoCartItemFound.visibility = View.VISIBLE
            }
        }
    }

    fun getCartItemList() {
        //showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getCartList(this)
    }

    fun getMenuList() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllMenuList(this@CartListActivity)
    }

    fun getMenuListSuccess(menuList: ArrayList<Menu>) {
        mMenuList = menuList

        getCartItemList()
    }

    fun itemRemovedSuccess() {
        hideProgressDialog()

        Toast.makeText(this, resources.getString(R.string.msg_item_removed_successfully), Toast.LENGTH_SHORT).show()

        getCartItemList()
    }

    fun itemUpdateSuccess() {
        hideProgressDialog()

        getCartItemList()

        binding.btnCheckout.setOnClickListener {
            val intent = Intent(this, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }
    }

    private fun setupActionBar() {
        val toolbar = binding.toolbarCartListActivity

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