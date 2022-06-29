package com.example.rumiapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rumiapp.databinding.ItemMenuListBinding
import com.example.rumiapp.models.Menu
import com.example.rumiapp.utils.GlideLoader

class MenuListAdapter (
    private val context: Context,
    private var list: ArrayList<Menu>
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

    class MenuListHolder(private val binding: ItemMenuListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, menu: Menu) {
            binding.apply {
                GlideLoader(context).loadMenuItemPicture(menu.image, ivAvatar)
                tvMenuName.text = menu.title
                tvMenuPrice.text = "Rp${menu.price},-"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemMenuListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuListHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MenuListHolder) {
            holder.bind(context, list[position])
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, list[position])
            }
        }
    }

    override fun getItemCount(): Int = list.size

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, menu: Menu)
    }

}