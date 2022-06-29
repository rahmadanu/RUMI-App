package com.example.rumiapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rumiapp.R
import com.example.rumiapp.adapters.CartItemListAdapter
import com.example.rumiapp.databinding.ActivityOrderDetailsBinding
import com.example.rumiapp.models.Order
import com.example.rumiapp.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class OrderDetailsActivity : AppCompatActivity() {

    private var _binding: ActivityOrderDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        var orderDetails: Order = Order()
        if (intent.hasExtra(Constants.EXTRA_ORDER_DETAILS)) {
            orderDetails = intent.getParcelableExtra<Order>(Constants.EXTRA_ORDER_DETAILS)!!
        }

        setupUI(orderDetails)
    }

    private fun setupUI(orderDetails: Order) {

        binding.apply {
            tvOrderDetailsId.text = orderDetails.title

            val dateFormat = "dd MM yyyy HH::mm"

            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = orderDetails.order_datetime

            val orderDateTime = formatter.format(calendar.time)
            tvOrderDetailsDate.text = orderDateTime

            val diffInMilliSeconds: Long = System.currentTimeMillis() - orderDetails.order_datetime
            val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)
            Log.e("Difference in hours", "$diffInHours")

            when {
                diffInHours < 1 ->{
                    tvOrderStatus.text = resources.getString(R.string.order_status_pending)
                    tvOrderStatus.setTextColor(ContextCompat.getColor(this@OrderDetailsActivity, R.color.colorSnackBarError))
                }
                diffInHours < 2 -> {
                    tvOrderStatus.text = resources.getString(R.string.order_status_in_process)
                    tvOrderStatus.setTextColor(ContextCompat.getColor(this@OrderDetailsActivity, R.color.colorOrderStatusInProcess))
                }
                else -> {
                    tvOrderStatus.text = resources.getString(R.string.order_status_delivered)
                    tvOrderStatus.setTextColor(ContextCompat.getColor(this@OrderDetailsActivity, R.color.colorOrderStatusDelivered))
                }
            }

            rvMyOrderItemsList.layoutManager = LinearLayoutManager(this@OrderDetailsActivity)
            rvMyOrderItemsList.setHasFixedSize(true)

            val cartListAdapter = CartItemListAdapter(this@OrderDetailsActivity, orderDetails.items, false)
            rvMyOrderItemsList.adapter = cartListAdapter

            tvMyOrderDetailsAddressType.text = orderDetails.address.type
            tvMyOrderDetailsFullName.text = orderDetails.address.name
            tvMyOrderDetailsAddress.text = orderDetails.address.address
            tvMyOrderDetailsAdditionalNote.text = orderDetails.address.additionalNote

            if (orderDetails.address.otherDetails.isEmpty()) {
                tvMyOrderDetailsOtherDetails.visibility = View.VISIBLE
                tvMyOrderDetailsOtherDetails.text = orderDetails.address.otherDetails
            } else {
                tvMyOrderDetailsOtherDetails.visibility = View.GONE
            }

            tvMyOrderDetailsMobileNumber.text = orderDetails.address.mobileNumber

            tvOrderDetailsSubTotal.text = orderDetails.sub_total_amount
            tvOrderDetailsShippingCharge.text = orderDetails.shipping_charge
            tvOrderDetailsTotalAmount.text = orderDetails.total_amount
        }
    }

    private fun setupActionBar() {
        val toolbar = binding.toolbarMyOrderDetailsActivity

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