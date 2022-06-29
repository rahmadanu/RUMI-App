package com.example.rumiapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rumiapp.R
import com.example.rumiapp.databinding.ItemCartListBinding
import com.example.rumiapp.firestore.FirestoreClass
import com.example.rumiapp.models.Cart
import com.example.rumiapp.ui.activities.CartListActivity
import com.example.rumiapp.utils.Constants
import com.example.rumiapp.utils.GlideLoader

class CartItemListAdapter(
    private val context: Context,
    private val list: ArrayList<Cart>,
    private val updateCartItem: Boolean
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class CartItemListViewHolder(private val binding: ItemCartListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, cart: Cart, updateCartItem: Boolean) {
            binding.apply {
                GlideLoader(context).loadUserPicture(cart.image, ivCartItemImage)
                tvCartItemTitle.text = cart.title
                tvCartItemPrice.text = "Rp${cart.price},-"
                tvCartQuantity.text = cart.cart_quantity

                if (cart.cart_quantity == "0") {
                    ibRemoveCartItem.visibility = View.GONE
                    ibAddCartItem.visibility = View.GONE

                    if (updateCartItem) {
                        ibDeleteCartItem.visibility = View.VISIBLE
                    } else {
                        ibDeleteCartItem.visibility = View.GONE
                    }
                } else {

                    if (updateCartItem) {
                        ibRemoveCartItem.visibility = View.VISIBLE
                        ibAddCartItem.visibility = View.VISIBLE
                        ibDeleteCartItem.visibility = View.VISIBLE
                    } else {
                        ibRemoveCartItem.visibility = View.GONE
                        ibAddCartItem.visibility = View.GONE
                        ibDeleteCartItem.visibility = View.GONE
                    }
                }

                ibDeleteCartItem.setOnClickListener {
                    when (context) {
                        is CartListActivity -> {
                            context.showProgressDialog(context.resources.getString(R.string.please_wait))
                        }
                    }

                    FirestoreClass().removeItemFromCart(context, cart.id)
                }

                ibRemoveCartItem.setOnClickListener {
                    if (cart.cart_quantity == "1") {
                        FirestoreClass().removeItemFromCart(context, cart.id)
                    } else {

                        val cartQuantity: Int = cart.cart_quantity.toInt()

                        val itemHashMap = HashMap<String, Any>()
                        itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                        FirestoreClass().updateMyCart(context, cart.id, itemHashMap)
                    }
                }

                ibAddCartItem.setOnClickListener {  

                    val cartQuantity: Int = cart.cart_quantity.toInt()

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                    FirestoreClass().updateMyCart(context, cart.id, itemHashMap)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemCartListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartItemListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CartItemListViewHolder) {
            holder.bind(context, list[position], updateCartItem)
        }
    }

    override fun getItemCount(): Int = list.size
}