package com.example.rumiapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rumiapp.R
import com.example.rumiapp.adapters.CartItemListAdapter
import com.example.rumiapp.databinding.ActivityCheckoutBinding
import com.example.rumiapp.firestore.FirestoreClass
import com.example.rumiapp.models.Address
import com.example.rumiapp.models.Cart
import com.example.rumiapp.models.Menu
import com.example.rumiapp.models.Order
import com.example.rumiapp.utils.Constants

class CheckoutActivity : BaseActivity() {

    private var _binding: ActivityCheckoutBinding? = null
    private val binding get() = _binding!!

    private var mAddressDetails: Address? = null

    private lateinit var mMenuList: ArrayList<Menu>
    private lateinit var mCartList: ArrayList<Cart>

    private var mSubTotal: Int = 0
    private var mShippingCharge: Int = 5000
    private var mTotalAmount: Int = 0

    //private var transactionResult = TransactionResult()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_SELECTED_ADDRESS)!!
        }

        if (mAddressDetails != null) {
            binding.apply {
                tvCheckoutAddressType.text = mAddressDetails?.type
                tvCheckoutFullName.text = mAddressDetails?.name
                tvCheckoutAddress.text = "${mAddressDetails?.address}"
                tvCheckoutAdditionalNote.text = mAddressDetails?.additionalNote

                if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                    tvCheckoutOtherDetails.text = mAddressDetails?.otherDetails
                }
                tvMobileNumber.text = mAddressDetails?.mobileNumber
            }
        }
        getMenuList()

        //doMidtransPayment()

        binding.btnPlaceOrder.setOnClickListener {

            /*for (item in mCartList) {
                val transactionRequest = TransactionRequest(
                    "Payment-Midtrans" + System.currentTimeMillis().toString(),
                    item.price.toDouble())

                val detail = ItemDetails(item.menu_id.toString(),
                    item.price.toDouble(),
                    item.cart_quantity.toInt(),
                    item.title)

                val itemDetails = ArrayList<ItemDetails>()

                itemDetails.add(detail)
                uiKitDetails(transactionRequest)
                transactionRequest.itemDetails = itemDetails
                MidtransSDK.getInstance().transactionRequest = transactionRequest
                MidtransSDK.getInstance().startPaymentUiFlow(this)
            }*/
            placeAnOrder()
        }
    }

    fun getMenuList() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllMenuList(this)
    }

    fun getMenuListSuccess(menuList: ArrayList<Menu>) {

        mMenuList = menuList

        getCartItemList()
    }

    private fun getCartItemList() {

        FirestoreClass().getCartList(this)
    }

    fun getCartItemListSuccess(cartList: ArrayList<Cart>) {
        hideProgressDialog()

        mCartList = cartList

        binding.apply {
            rvCartListItems.layoutManager = LinearLayoutManager(this@CheckoutActivity)
            rvCartListItems.setHasFixedSize(true)

            val cartListAdapter = CartItemListAdapter(this@CheckoutActivity, mCartList, false)
            rvCartListItems.adapter = cartListAdapter

            for (item in mCartList) {

                val price = item.price.toInt()
                val quantity = item.cart_quantity.toInt()

                mSubTotal += price * quantity
            }

            tvCheckoutSubTotal.text = "$mSubTotal"
            tvCheckoutShippingCharge.text = "Rp${mShippingCharge}"

            if (mSubTotal > 0) {
                llCheckoutPlaceOrder.visibility = View.VISIBLE

                mTotalAmount = mSubTotal + mShippingCharge
                tvCheckoutTotalAmount.text = "$mTotalAmount"
            } else {
                llCheckoutPlaceOrder.visibility = View.GONE
            }
        }
    }

    private fun placeAnOrder() {
        showProgressDialog(resources.getString(R.string.please_wait))

        val order = Order(
            FirestoreClass().getCurrentUserId(),
            mCartList,
            mAddressDetails!!,
            "Order no.${System.currentTimeMillis()}",
            mCartList[0].image,
            mSubTotal.toString(),
            mShippingCharge.toString(),
            mTotalAmount.toString(),
            System.currentTimeMillis()
        )

        FirestoreClass().placeOrder(this, order)
    }

    fun placeAnOrderSuccess() {

        FirestoreClass().updateAllDetails(this, mCartList)
    }

    fun updateAllDetailsSuccess() {
        hideProgressDialog()

        Toast.makeText(this, "Your order is placed successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /*private fun doMidtransPayment() {
        SdkUIFlowBuilder.init().setClientKey(Constants.CLIENT_KEY_MIDTRANS).setContext(applicationContext)
            .setTransactionFinishedCallback {
                if (TransactionResult.STATUS_SUCCESS == "success") {
                    Toast.makeText(this, "Success transaction", Toast.LENGTH_LONG).show()
                } else if (TransactionResult.STATUS_PENDING == "pending") {
                    Toast.makeText(this, "Pending transaction", Toast.LENGTH_LONG).show()
                } else if (TransactionResult.STATUS_FAILED == "failed") {
                    Toast.makeText(
                        this,
                        "Failed ${transactionResult.response.statusMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (transactionResult.status.equals(
                        TransactionResult.STATUS_INVALID,
                        true
                    )
                ) {
                    Toast.makeText(this, "Invalid transaction", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Failure transaction", Toast.LENGTH_LONG).show()
                }
            }
            .setMerchantBaseUrl(Constants.BASE_URL_MIDTRANS).enableLog(true).setColorTheme(
                CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .setLanguage("en").buildSDK()
    }

    private fun uiKitDetails(transactionRequest: TransactionRequest) {

        val user: User = User()

        val customerDetails = CustomerDetails()
        customerDetails.customerIdentifier = user.id
        customerDetails.phone = user.mobile.toString()
        customerDetails.firstName = user.firstName
        customerDetails.lastName = user.lastName
        customerDetails.email = user.email


        val shippingAddress = ShippingAddress()
        shippingAddress.address = mAddressDetails?.address
        shippingAddress.city = "Surabaya (dummy)"
        shippingAddress.postalCode = "60293 (dummy)"
        customerDetails.shippingAddress = shippingAddress

        val billingAddress = BillingAddress()
        billingAddress.address = mAddressDetails?.address
        billingAddress.city = "Surabaya (dummy)"
        billingAddress.postalCode = "60293 (dummy)"

        customerDetails.billingAddress = billingAddress
        transactionRequest.customerDetails = customerDetails
    }*/

    private fun setupActionBar() {

        val toolbar = binding.toolbarCheckoutActivity

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