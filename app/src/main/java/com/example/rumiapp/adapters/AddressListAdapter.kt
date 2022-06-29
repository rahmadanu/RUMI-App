package com.example.rumiapp.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.rumiapp.databinding.ItemAddressListBinding
import com.example.rumiapp.models.Address
import com.example.rumiapp.ui.activities.AddEditAddressActivity
import com.example.rumiapp.ui.activities.CheckoutActivity
import com.example.rumiapp.utils.Constants
import kotlinx.coroutines.selects.select

class AddressListAdapter(
    private val context: Context,
    private var list: ArrayList<Address>,
    private val selectAddress: Boolean
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //test

    class AddressListViewHolder(private val binding: ItemAddressListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, address: Address, selectAddress: Boolean) {
            binding.apply {
                tvAddressFullName.text = address.name
                tvAddressType.text = address.type
                tvAddressDetails.text = "${address.address}"
                tvAddressMobileNumber.text = address.mobileNumber

                if (selectAddress) {
                    itemView.setOnClickListener {
                        val intent = Intent(context, CheckoutActivity::class.java)
                        intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, address)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemAddressListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddressListViewHolder) {
            holder.bind(context, list[position], selectAddress)
        }
    }

    override fun getItemCount(): Int = list.size

    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])

        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }
}