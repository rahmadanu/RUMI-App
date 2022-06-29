package com.example.rumiapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rumiapp.R
import com.example.rumiapp.adapters.OrderListAdapter
import com.example.rumiapp.databinding.FragmentOrdersBinding
import com.example.rumiapp.firestore.FirestoreClass
import com.example.rumiapp.models.Order

class OrdersFragment : BaseFragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentOrdersBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getOrderList()
    }

    private fun getOrderList() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getOrderList(this@OrdersFragment)
    }

    fun getOrderListSuccess(orderList: ArrayList<Order>) {
        hideProgressDialog()

        binding.apply {

            if (orderList.size > 0) {
                rvMyOrderItems.visibility = View.VISIBLE
                tvNoOrdersFound.visibility = View.GONE

                rvMyOrderItems.layoutManager = LinearLayoutManager(activity)
                rvMyOrderItems.setHasFixedSize(true)

                val orderAdapter = OrderListAdapter(requireActivity(), orderList)
                rvMyOrderItems.adapter = orderAdapter
            } else {
                rvMyOrderItems.visibility = View.GONE
                tvNoOrdersFound.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}