package com.example.rumiapp.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rumiapp.R
import com.example.rumiapp.adapters.AddressListAdapter
import com.example.rumiapp.databinding.ActivityAddressListBinding
import com.example.rumiapp.firestore.FirestoreClass
import com.example.rumiapp.models.Address
import com.example.rumiapp.utils.Constants
import com.example.rumiapp.utils.SwipeToDeleteCallback
import com.example.rumiapp.utils.SwipeToEditCallback

class AddressListActivity : BaseActivity() {

    private var _binding: ActivityAddressListBinding? = null
    private val binding get() = _binding!!

    private var mSelectAddress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }

        if (mSelectAddress) {
            binding.tvTitle.text = resources.getString(R.string.title_select_address)
        }

        binding.tvAddAddress.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)
            startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        }

        getAddressList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (resultCode == Constants.ADD_ADDRESS_REQUEST_CODE) {
                getAddressList()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "To add the address")
        }
    }

    fun getAddressList() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAddressList(this)
    }

    fun getAddressListSuccess(addressList: ArrayList<Address>) {
        hideProgressDialog()

        binding.apply {
            if (addressList.size > 0) {

                rvAddressList.visibility = View.VISIBLE
                tvNoAddressFound.visibility = View.GONE

                rvAddressList.layoutManager = LinearLayoutManager(this@AddressListActivity)
                rvAddressList.setHasFixedSize(true)

                val addressAdapter = AddressListAdapter(this@AddressListActivity, addressList, mSelectAddress)
                rvAddressList.adapter = addressAdapter

                if (!mSelectAddress) {

                    val editSwipeHandler = object : SwipeToEditCallback(this@AddressListActivity) {

                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            val adapter = rvAddressList.adapter as AddressListAdapter
                            adapter.notifyEditItem(this@AddressListActivity, viewHolder.adapterPosition)
                        }
                    }
                    val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                    editItemTouchHelper.attachToRecyclerView(rvAddressList)

                    val deleteSwipeHandler = object : SwipeToDeleteCallback(this@AddressListActivity) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            showProgressDialog(resources.getString(R.string.please_wait))

                            FirestoreClass().deleteAddress(this@AddressListActivity, addressList[viewHolder.adapterPosition].id)
                        }
                    }
                    val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                    deleteItemTouchHelper.attachToRecyclerView(rvAddressList)
                }
            } else {
                rvAddressList.visibility = View.GONE
                tvNoAddressFound.visibility = View.VISIBLE
            }
        }
    }

    fun deleteAddressSuccess() {
        hideProgressDialog()

        Toast.makeText(this, resources.getString(R.string.err_your_address_deleted_successfully), Toast.LENGTH_SHORT).show()

        getAddressList()
    }

    fun setupActionBar() {
        val toolbar = binding.toolbarAddressListActivity

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