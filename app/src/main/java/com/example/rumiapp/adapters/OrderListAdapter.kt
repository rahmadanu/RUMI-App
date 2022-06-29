package com.example.rumiapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rumiapp.databinding.FragmentOrdersBinding
import com.example.rumiapp.databinding.ItemOrderListBinding
import com.example.rumiapp.models.Order
import com.example.rumiapp.ui.activities.OrderDetailsActivity
import com.example.rumiapp.utils.Constants
import com.example.rumiapp.utils.GlideLoader

class OrderListAdapter(
    private val context: Context,
    private var list: ArrayList<Order>
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    class OrderListHolder(private val binding: ItemOrderListBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(context: Context, order: Order) {
                binding.apply {
                    GlideLoader(context).loadUserPicture(order.image, ivItemImage)

                    tvItemName.text = order.title
                    tvItemPrice.text = "Rp${order.total_amount},-"

                    itemView.setOnClickListener {
                        val intent = Intent(context, OrderDetailsActivity::class.java)
                        intent.putExtra(Constants.EXTRA_ORDER_DETAILS, order)
                        context.startActivity(intent)
                    }
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemOrderListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderListHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OrderListHolder) {
            holder.bind(context, list[position])
        }
    }

    override fun getItemCount(): Int = list.size
}