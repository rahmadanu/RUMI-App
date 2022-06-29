package com.example.rumiapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rumiapp.R
import com.example.rumiapp.adapters.MenuListAdapter
import com.example.rumiapp.databinding.FragmentHomeBinding
import com.example.rumiapp.firestore.FirestoreClass
import com.example.rumiapp.models.Menu
import com.example.rumiapp.ui.activities.CartListActivity
import com.example.rumiapp.ui.activities.MenuItemDetailsActivity
import com.example.rumiapp.utils.Constants

class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    //private val viewModel: HomeViewModel by viewModels()

    //change 'home' to 'menu'

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMenuListFirestore()
        //homeViewModel = HomeViewModel(MobileNumberTestingModel())
  //      homeCheckPrice()
        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(requireContext(), CartListActivity::class.java))
        }
    }
/*
    private fun homeCheckPrice() {
        binding?.homeTesting?.text = homeViewModel.checkMobile(User().mobile).toString()
    }*/

    private fun getMenuListFirestore() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getMenuList(this@HomeFragment)
    }

    fun successGetMenuList(menuList: ArrayList<Menu>) {
        hideProgressDialog()

        for (i in menuList) {
            Log.i("item title", i.title)
        }

        binding.apply {

            if (menuList.size > 0) {

                rvMenuItems.visibility = View.VISIBLE
                tvNoMenuItemsFound.visibility = View.GONE

                rvMenuItems.layoutManager = LinearLayoutManager(activity)
                rvMenuItems.setHasFixedSize(true)

                val adapter = MenuListAdapter(requireActivity(), menuList)
                rvMenuItems.adapter = adapter

                adapter.setOnClickListener(object : MenuListAdapter.OnClickListener {
                    override fun onClick(position: Int, menu: Menu) {
                        val intent = Intent(context, MenuItemDetailsActivity::class.java)
                        intent.putExtra(Constants.EXTRA_MENU_ID, menu.menu_id)
                        startActivity(intent)
                    }
                })

            } else {
                rvMenuItems.visibility = View.GONE
                tvNoMenuItemsFound.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}